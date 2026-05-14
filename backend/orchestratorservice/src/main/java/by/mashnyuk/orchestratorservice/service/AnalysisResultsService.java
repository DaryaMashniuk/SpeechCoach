package by.mashnyuk.orchestratorservice.service;

import by.mashnyuk.orchestratorservice.model.response.IntelligenceAnalyzeResponse;
import by.mashnyuk.orchestratorservice.model.response.MeetingTranscriptionResult;

import java.util.UUID;

public interface AnalysisResultsService {

  void saveAnalysisResult(UUID jobId, IntelligenceAnalyzeResponse response);

  void saveTranscriptionResult(UUID jobId, MeetingTranscriptionResult transcriptionResult);
}
