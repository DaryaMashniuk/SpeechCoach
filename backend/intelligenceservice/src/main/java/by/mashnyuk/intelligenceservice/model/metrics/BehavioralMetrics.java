package by.mashnyuk.intelligenceservice.model.metrics;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BehavioralMetrics {
  private double confidenceScore;
  private double nervousnessScore;
  private double clarityScore;
  private List<String> detectedPatterns;
}
