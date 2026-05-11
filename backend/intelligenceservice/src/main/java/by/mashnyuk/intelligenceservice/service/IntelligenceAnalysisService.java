package by.mashnyuk.intelligenceservice.service;

import by.mashnyuk.intelligenceservice.model.dto.request.IntelligenceAnalyzeRequest;
import by.mashnyuk.intelligenceservice.model.dto.response.IntelligenceAnalyzeResponse;

public interface IntelligenceAnalysisService {
  IntelligenceAnalyzeResponse fullAnalysis(IntelligenceAnalyzeRequest request);
}
