package by.mashnyuk.intelligenceservice.service.analyzers;

import by.mashnyuk.intelligenceservice.model.dto.request.AudioMetricsDto;
import by.mashnyuk.intelligenceservice.model.dto.request.AudioPointDto;
import by.mashnyuk.intelligenceservice.model.metrics.ProsodyMetrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ProsodyAnalyzer {

  private static final double MONOTONE_THRESHOLD = 0.18;
  private static final double LONG_PAUSE_SEC = 1.5;
  private static final double EXTREME_PAUSE_SEC = 2.5;
  private static final double DEAD_AIR_THRESHOLD = 0.35;

  public ProsodyMetrics analyze(AudioMetricsDto audio) {

    double pitchRange =
            audio.maxPitchHz() - audio.minPitchHz();

    double normalizedPitchRange =
            pitchRange / Math.max(audio.avgPitchHz(), 1);

    boolean monotone =
            normalizedPitchRange < MONOTONE_THRESHOLD;

    double pitchStability =
            normalizeInverse(audio.pitchVariance());

    double rhythmStability =
            calculateRhythm(audio.timeline());

    double deadAirRatio =
            calculateDeadAir(audio.timeline());

    double pausePenalty =
            calculatePausePenalty(audio.avgPauseMs(),deadAirRatio);

    boolean excessiveSilence =
            deadAirRatio > DEAD_AIR_THRESHOLD;

    return ProsodyMetrics.builder()
            .avgPitch(audio.avgPitchHz())
            .pitchRange(pitchRange)
            .pitchStability(Math.max(0.01, pitchStability))
            .rhythmStability(rhythmStability)
            .speechRateVariance(audio.rmsVariance())
            .pausePenalty(pausePenalty)
            .deadAirRatio(deadAirRatio)
            .monotone(monotone)
            .excessiveSilence(excessiveSilence)
            .build();
  }

  private double calculatePausePenalty(double avgPauseMs, double deadAirRatio) {
    double pauseSec = avgPauseMs / 1000.0;
    double penalty = 0.0;

    if (deadAirRatio > 0.35) {
      penalty = 0.4;
    } else if (deadAirRatio > 0.25) {
      penalty = 0.25;
    } else if (deadAirRatio > 0.15) {
      penalty = 0.15;
    } else if (deadAirRatio > 0.1) {
      penalty = 0.05;
    }

    if (pauseSec > EXTREME_PAUSE_SEC) {
      penalty = Math.max(penalty, 0.5);
    } else if (pauseSec > LONG_PAUSE_SEC) {
      penalty = Math.max(penalty, 0.3);
    }

    log.debug("Pause penalty: {} (avgPause={:.2f}s, deadAir={:.2%})",
            penalty, pauseSec, deadAirRatio);

    return penalty;
  }

  private double calculateDeadAir(List<AudioPointDto> timeline) {

    if (timeline.isEmpty()) {
      return 0;
    }

    long silent =
            timeline.stream()
                    .filter(p -> Boolean.FALSE.equals(p.speech()))
                    .count();

    return (double) silent / timeline.size();
  }

  private double calculateRhythm(List<AudioPointDto> timeline) {
    List<Double> intervals = new ArrayList<>();
    Double previous = null;

    for (AudioPointDto point : timeline) {
      if (Boolean.TRUE.equals(point.speech())) {
        if (previous != null) {
          double interval = point.timeSec() - previous;
          intervals.add(interval);
        }
        previous = point.timeSec();
      }
    }

    if (intervals.isEmpty()) {
      return 0.5;
    }

    double variance = variance(intervals);

    if (variance < 0.001) {
      log.warn("Very low rhythm variance: {}, possible measurement issue", variance);
      double deadAir = calculateDeadAir(timeline);
      return 0.5 + (1 - deadAir) * 0.3;
    }

    double avgInterval = intervals.stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0.5);

    double normalizedVariance = avgInterval > 0 ? variance / (avgInterval * avgInterval) : variance;

    double stability = normalizeInverse(normalizedVariance * 10);

    log.debug("Rhythm: avgInterval={:.4f}s, variance={:.6f}, normVariance={:.6f}, stability={:.4f}",
            avgInterval, variance, normalizedVariance, stability);

    return stability;
  }

  private double variance(List<Double> values) {

    if (values.isEmpty()) {
      return 0;
    }

    double avg =
            values.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0);

    double sum = 0;

    for (double value : values) {
      sum += Math.pow(value - avg, 2);
    }

    return sum / values.size();
  }

  private double normalizeInverse(double value) {
    if (value == 0) return 1.0;
    double scaled = value / 1000.0;
    double result = 1.0 / (1.0 + Math.log1p(scaled * 10));
    log.debug("Pitch variance: {} -> stability: {}", value, result);
    return Math.min(1.0, Math.max(0.0, result));
  }
}