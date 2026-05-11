package by.mashnyuk.intelligenceservice.service.analyzers;

import by.mashnyuk.intelligenceservice.model.metrics.BehavioralMetrics;
import by.mashnyuk.intelligenceservice.model.metrics.LexicalMetrics;
import by.mashnyuk.intelligenceservice.model.metrics.ProsodyMetrics;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BehaviorAnalyzer {

  public BehavioralMetrics analyze(LexicalMetrics lexical, ProsodyMetrics prosody) {
    List<String> patterns = new ArrayList<>();

    double nervousness = (lexical.getFillerCount() * 0.05) + (1 - prosody.getRhythmStability());
    if (nervousness > 0.6) patterns.add("NERVOUS_SPEECH");

    double confidence = 1.0 - prosody.getPausePenalty() - (prosody.isMonotone() ? 0.2 : 0);
    if (confidence < 0.5) patterns.add("LOW_CONFIDENCE");

    return BehavioralMetrics.builder()
            .confidenceScore(clamp(confidence))
            .nervousnessScore(clamp(nervousness))
            .clarityScore(lexical.getLexicalDensity())
            .detectedPatterns(patterns)
            .build();
  }

  private double clamp(double val) { return Math.max(0, Math.min(1, val)); }
}
