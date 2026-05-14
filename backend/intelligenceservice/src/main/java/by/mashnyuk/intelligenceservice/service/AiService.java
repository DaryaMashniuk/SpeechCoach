package by.mashnyuk.intelligenceservice.service;

import by.mashnyuk.intelligenceservice.model.metrics.BehavioralMetrics;
import by.mashnyuk.intelligenceservice.model.metrics.LexicalMetrics;
import by.mashnyuk.intelligenceservice.model.OverallScore;
import by.mashnyuk.intelligenceservice.model.metrics.ProsodyMetrics;
import by.mashnyuk.intelligenceservice.model.metrics.StructureMetrics;
import by.mashnyuk.intelligenceservice.model.dto.request.IntelligenceAnalyzeRequest;

public interface AiService {
  String generateCoachingFeedback(
          IntelligenceAnalyzeRequest request,
          LexicalMetrics lexical,
          ProsodyMetrics prosody,
          BehavioralMetrics behavior,
          StructureMetrics structure,
          OverallScore score
  );
}
