package by.mashnyuk.intelligenceservice.service.analyzers;

import by.mashnyuk.intelligenceservice.model.metrics.BehavioralMetrics;
import by.mashnyuk.intelligenceservice.model.metrics.LexicalMetrics;
import by.mashnyuk.intelligenceservice.model.OverallScore;
import by.mashnyuk.intelligenceservice.model.metrics.ProsodyMetrics;
import by.mashnyuk.intelligenceservice.model.metrics.StructureMetrics;
import org.springframework.stereotype.Service;

@Service
public class ScoreEngine {


  private static final double CLARITY_WEIGHT = 0.30;
  private static final double DELIVERY_WEIGHT = 0.35;
  private static final double ENGAGEMENT_WEIGHT = 0.20;
  private static final double STRUCTURE_WEIGHT = 0.15;

  public OverallScore calculate(
          LexicalMetrics lexical,
          ProsodyMetrics prosody,
          BehavioralMetrics behavior,
          StructureMetrics structure
  ) {
    double clarity = calculateClarity(lexical);
    double delivery = calculateDelivery(behavior, prosody, lexical);
    double engagement = calculateEngagement(prosody);
    double structureScore = calculateStructure(structure, lexical);

    double overall = (
            clarity * CLARITY_WEIGHT +
                    delivery * DELIVERY_WEIGHT +
                    engagement * ENGAGEMENT_WEIGHT +
                    structureScore * STRUCTURE_WEIGHT
    ) * 100;

    return OverallScore.builder()
            .clarity(clarity)
            .delivery(delivery)
            .engagement(engagement)
            .structure(structureScore)
            .overall(clamp(overall, 0, 100))
            .build();
  }

  private double calculateClarity(LexicalMetrics l) {

    double fillerRatio = (double) l.getFillerCount() / Math.max(1, l.getTotalWords());
    double fillerScore = clamp(1.0 - (fillerRatio * 10));

    double densityScore = Math.min(1.0, l.getLexicalDensity() * 1.8);
    double varietyScore = Math.min(1.0, l.getLexicalVariety() * 2.0);

    return (fillerScore * 0.5) + (densityScore * 0.3) + (varietyScore * 0.2);
  }

  private double calculateDelivery(BehavioralMetrics b, ProsodyMetrics p, LexicalMetrics l) {

    double wpm = l.getWpm();
    double wpmScore = 1.0;
    if (wpm > 180) wpmScore = Math.max(0.5, 1.0 - (wpm - 180) / 100.0);
    if (wpm < 110) wpmScore = Math.max(0.5, 1.0 - (110 - wpm) / 100.0);

    return (b.getConfidenceScore() * 0.4) +
            (p.getRhythmStability() * 0.3) +
            (wpmScore * 0.3);
  }

  private double calculateEngagement(ProsodyMetrics p) {
    double rangeScore = clamp(p.getPitchRange() / 250.0);
    double stabilityScore = p.getPitchStability();

    double base = p.isMonotone() ? 0.4 : 0.8;

    return clamp(base * 0.6 + rangeScore * 0.3 + stabilityScore * 0.1);
  }

  private double calculateStructure(StructureMetrics s, LexicalMetrics l) {
    double complexityFactor = l.getTotalWords() < 50 ? 0.5 : 1.0;

    double score = (s.getCoherenceScore() * 0.5) +
            (s.getTopicConsistencyScore() * 0.5);

    if (s.isHasIntroduction()) score += 0.1;
    if (s.isHasConclusion()) score += 0.1;
    if (s.getArgumentationScore() > 0) score += 0.05;

    if (complexityFactor < 1.0) {
      score = 0.7 + (score * 0.3);
    }

    return clamp(score);
  }

  private double clamp(double val) {
    return Math.max(0, Math.min(1, val));
  }

  private double clamp(double val, double min, double max) {
    return Math.max(min, Math.min(max, val));
  }
}