package by.mashnyuk.intelligenceservice.service.analyzers;

import by.mashnyuk.intelligenceservice.model.metrics.BehavioralMetrics;
import by.mashnyuk.intelligenceservice.model.metrics.LexicalMetrics;
import by.mashnyuk.intelligenceservice.model.metrics.ProsodyMetrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BehaviorAnalyzer {

  public BehavioralMetrics analyze(LexicalMetrics lexical, ProsodyMetrics prosody) {
    List<String> patterns = new ArrayList<>();

    double nervousness = calculateNervousness(lexical, prosody);
    if (nervousness > 0.5) patterns.add("NERVOUS_SPEECH");
    if (nervousness > 0.7) patterns.add("HIGH_ANXIETY");

    double confidence = calculateConfidence(lexical, prosody);
    if (confidence < 0.4) patterns.add("LOW_CONFIDENCE");
    if (confidence > 0.8) patterns.add("HIGH_CONFIDENCE");

    if (prosody.isMonotone()) {
      patterns.add("MONOTONE_DELIVERY");
    }

    if (lexical.getFillerCount() > 8) {
      patterns.add("EXCESSIVE_FILLERS");
    } else if (lexical.getFillerCount() > 4) {
      patterns.add("MODERATE_FILLERS");
    }

    if (prosody.isExcessiveSilence()) {
      patterns.add("EXCESSIVE_SILENCE");
    }

    double wpm = lexical.getWpm();
    if (wpm > 180) {
      patterns.add("FAST_SPEECH");
    } else if (wpm < 120) {
      patterns.add("SLOW_SPEECH");
    } else {
      patterns.add("OPTIMAL_SPEED");
    }

    log.debug("Detected patterns: {}", patterns);

    return BehavioralMetrics.builder()
            .confidenceScore(clamp(confidence))
            .nervousnessScore(clamp(nervousness))
            .clarityScore(lexical.getLexicalDensity())
            .detectedPatterns(patterns)
            .build();
  }

  private double calculateNervousness(LexicalMetrics lexical, ProsodyMetrics prosody) {
    double score = 0.0;

    score += Math.min(0.4, lexical.getFillerCount() * 0.04);

    score += (1 - prosody.getRhythmStability()) * 0.3;

    score += prosody.getDeadAirRatio() * 0.3;

    return clamp(score);
  }

  private double calculateConfidence(LexicalMetrics lexical, ProsodyMetrics prosody) {
    double score = 0.5;

    double wpm = lexical.getWpm();
    double wpmScore = 0;
    if (wpm >= 130 && wpm <= 160) {
      wpmScore = 0.15;
    } else if (wpm >= 110 && wpm <= 180) {
      wpmScore = 0.1;
    } else if (wpm > 180) {
      wpmScore = -0.1;
    } else {
      wpmScore = -0.05;
    }
    score += wpmScore;

    if (!prosody.isMonotone()) {
      double pitchRange = prosody.getPitchRange();
      if (pitchRange > 300) {
        score += 0.15;
      } else if (pitchRange > 150) {
        score += 0.1;
      }
    } else {
      score -= 0.15;
    }

    double rhythmStability = prosody.getRhythmStability();
    if (rhythmStability > 0.8) {
      score += 0.1;
    } else if (rhythmStability < 0.5) {
      score -= 0.1;
    }

    score -= prosody.getPausePenalty() * 0.5;
    if (prosody.isExcessiveSilence()) {
      score -= 0.1;
    }

    int fillerCount = lexical.getFillerCount();
    if (fillerCount > 10) {
      score -= 0.15;
    } else if (fillerCount > 5) {
      score -= 0.1;
    } else if (fillerCount > 2) {
      score -= 0.05;
    }

    double lexicalVariety = lexical.getLexicalVariety();
    if (lexicalVariety > 0.6) {
      score += 0.1;
    } else if (lexicalVariety < 0.3) {
      score -= 0.1;
    }

    log.debug("Confidence calculation: wpm={}, wpmScore={}, final={}",
            wpm, wpmScore, score);

    return clamp(score);
  }

  private double clamp(double val) {
    return Math.max(0, Math.min(1, val));
  }
}