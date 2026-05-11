package by.mashnyuk.intelligenceservice.service.analyzers;

import by.mashnyuk.intelligenceservice.model.metrics.PitchDynamicsMetrics;
import by.mashnyuk.intelligenceservice.model.dto.request.AudioMetricsDto;
import org.springframework.stereotype.Service;

@Service
public class PitchDynamicsAnalyzer {

  public PitchDynamicsMetrics analyze(AudioMetricsDto audio) {

    double range =
            audio.maxPitchHz()
                    - audio.minPitchHz();
    double normalizedRange =
            range / audio.avgPitchHz();
    boolean monotone = normalizedRange < 0.18;

    double stability =
            1.0 /
                    (1.0 + audio.pitchVariance());

    return PitchDynamicsMetrics.builder()
            .pitchRange(range)
            .pitchStability(stability)
            .monotone(monotone)
            .build();
  }
}
