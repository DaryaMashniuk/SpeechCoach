package by.mashnyuk.intelligenceservice.service.analyzers;

import by.mashnyuk.intelligenceservice.model.metrics.RhythmMetrics;
import by.mashnyuk.intelligenceservice.model.dto.request.AudioPointDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RhythmAnalyzer {

  private static final double PAUSE_THRESHOLD = 0.3;

  public RhythmMetrics analyze(List<AudioPointDto> timeline) {

    List<Double> speechChunks = new ArrayList<>();

    Double chunkStart = null;
    Double previousTime = null;

    for (AudioPointDto point : timeline) {

      if (Boolean.TRUE.equals(point.speech())) {

        if (chunkStart == null) {
          chunkStart = point.timeSec();
        }

      } else {

        if (chunkStart != null && previousTime != null) {

          double duration =
                  previousTime - chunkStart;

          if (duration > PAUSE_THRESHOLD) {
            speechChunks.add(duration);
          }

          chunkStart = null;
        }
      }

      previousTime = point.timeSec();
    }

    double variance =
            calculateVariance(speechChunks);

    double stability =
            1.0 / (1.0 + variance);

    return RhythmMetrics.builder()
            .speechRateVariance(variance)
            .rhythmStability(stability)
            .build();
  }

  private double calculateVariance(List<Double> values) {

    if (values.isEmpty()) {
      return 0;
    }

    double avg =
            values.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0);

    double variance = 0;

    for (double value : values) {
      variance += Math.pow(value - avg, 2);
    }

    return variance / values.size();
  }
}
