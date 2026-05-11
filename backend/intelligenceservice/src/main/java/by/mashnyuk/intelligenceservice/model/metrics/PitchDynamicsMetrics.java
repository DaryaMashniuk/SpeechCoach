package by.mashnyuk.intelligenceservice.model.metrics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PitchDynamicsMetrics {

  private double pitchRange;
  private double pitchStability;
  private boolean monotone;
}
