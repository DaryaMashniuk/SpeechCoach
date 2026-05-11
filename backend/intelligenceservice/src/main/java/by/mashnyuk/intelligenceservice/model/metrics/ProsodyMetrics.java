package by.mashnyuk.intelligenceservice.model.metrics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProsodyMetrics {
  private double avgPitch;
  private double pitchRange;
  private double rhythmStability;
  private double speechRateVariance;
  private double pausePenalty;
  private boolean monotone;
}