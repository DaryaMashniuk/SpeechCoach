package by.mashnyuk.intelligenceservice.model.metrics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RhythmMetrics {

  private double rhythmStability;
  private double speechRateVariance;
}
