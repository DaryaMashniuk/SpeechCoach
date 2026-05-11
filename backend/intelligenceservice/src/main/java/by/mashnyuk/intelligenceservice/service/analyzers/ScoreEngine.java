package by.mashnyuk.intelligenceservice.service.analyzers;

import by.mashnyuk.intelligenceservice.model.metrics.BehavioralMetrics;
import by.mashnyuk.intelligenceservice.model.metrics.LexicalMetrics;
import by.mashnyuk.intelligenceservice.model.OverallScore;
import by.mashnyuk.intelligenceservice.model.metrics.ProsodyMetrics;
import org.springframework.stereotype.Service;

@Service
public class ScoreEngine {
  public OverallScore calculate(LexicalMetrics l, ProsodyMetrics p, BehavioralMetrics b) {
    return OverallScore.builder()
            .clarity(l.getLexicalDensity() * 0.7 + (1 - l.getFillerCount()*0.01) * 0.3)
            .delivery(b.getConfidenceScore())
            .engagement(p.isMonotone() ? 0.4 : 0.9)
            .overall((b.getConfidenceScore() + l.getLexicalDensity() + (p.isMonotone() ? 0.5 : 1)) / 3 * 100)
            .build();
  }
}
