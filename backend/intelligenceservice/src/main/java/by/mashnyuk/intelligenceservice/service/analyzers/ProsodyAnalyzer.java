package by.mashnyuk.intelligenceservice.service.analyzers;

import by.mashnyuk.intelligenceservice.model.metrics.ProsodyMetrics;
import by.mashnyuk.intelligenceservice.model.dto.request.AudioMetricsDto;
import by.mashnyuk.intelligenceservice.model.dto.request.AudioPointDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProsodyAnalyzer {

  public ProsodyMetrics analyze(AudioMetricsDto audio) {

    double rhythmStability = calculateRhythm(audio.timeline());

    double pausePenalty = audio.avgPauseMs() > 1500 ? 0.3 : 0.0;

    double pitchRange = audio.maxPitchHz() - audio.minPitchHz();

    return ProsodyMetrics.builder()
            .avgPitch(audio.avgPitchHz())
            .pitchRange(pitchRange)
            .rhythmStability(rhythmStability)
            .speechRateVariance(audio.rmsVariance())
            .pausePenalty(pausePenalty)
            .monotone(pitchRange < 40)
            .build();
  }

  private double calculateRhythm(List<AudioPointDto> timeline) {
    List<Double> speechIntervals = new ArrayList<>();
    Double lastTime = null;
    for (var point : timeline) {
      if (Boolean.TRUE.equals(point.speech())) {
        if (lastTime != null) speechIntervals.add(point.timeSec() - lastTime);
        lastTime = point.timeSec();
      }
    }
    return 1.0 / (1.0 + calculateVariance(speechIntervals));
  }

  private double calculateVariance(List<Double> vals) {
    if (vals.isEmpty()) return 0;
    double avg = vals.stream().mapToDouble(d -> d).average().orElse(0);
    return vals.stream().mapToDouble(v -> Math.pow(v - avg, 2)).average().orElse(0);
  }
}