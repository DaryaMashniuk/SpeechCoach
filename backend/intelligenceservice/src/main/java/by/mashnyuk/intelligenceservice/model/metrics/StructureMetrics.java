package by.mashnyuk.intelligenceservice.model.metrics;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StructureMetrics {

  private boolean hasIntroduction;
  private boolean hasConclusion;
  private boolean supportedByHeuristics;

  private double coherenceScore;
  private double transitionScore;
  private double argumentationScore;
  private double topicConsistencyScore;

  private int topicJumps;

  private List<String> detectedTransitions;
  private List<String> missingParts;
}
