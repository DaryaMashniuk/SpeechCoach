package by.mashnyuk.orchestratorservice.service;

import by.mashnyuk.orchestratorservice.model.response.IntelligenceAnalyzeResponse;

import java.util.UUID;

public interface AnalysisResultsService {

  void saveAnalysisResult(UUID jobId, IntelligenceAnalyzeResponse response);
}
