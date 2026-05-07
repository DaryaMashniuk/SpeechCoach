package by.mashnyuk.orchestratorservice.service;

import by.mashnyuk.orchestratorservice.model.response.IntelligenceAnalyzeResponse;

public interface AnalysisResultsService {

  void saveAnalysisResult(Long audioId, IntelligenceAnalyzeResponse response);
}
