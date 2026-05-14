package by.mashnyuk.orchestratorservice.service.impl;

import by.mashnyuk.orchestratorservice.model.AnalysisResults;
import by.mashnyuk.orchestratorservice.model.response.IntelligenceAnalyzeResponse;
import by.mashnyuk.orchestratorservice.model.response.MeetingTranscriptionResult;
import by.mashnyuk.orchestratorservice.repository.AnalysisResultsRepository;
import by.mashnyuk.orchestratorservice.service.AnalysisResultsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalysisResultsServiceImpl implements AnalysisResultsService {

  private final AnalysisResultsRepository analysisResultsRepository;

  @Override
  public void saveAnalysisResult(UUID jobId, IntelligenceAnalyzeResponse response) {
    AnalysisResults result = AnalysisResults.builder()
            .analysisJobId(jobId)
            .fullReport(response)
            .modelVersion("llama3.2-speech-v1")
            .build();
    analysisResultsRepository.save(result);
  }

  @Override
  public void saveTranscriptionResult(UUID jobId, MeetingTranscriptionResult transcriptionResult) {
    AnalysisResults result = AnalysisResults.builder()
            .analysisJobId(jobId)
            .meetingTranscriptionResult(transcriptionResult)
            .modelVersion("llama3.2-speech-v1")
            .build();
    analysisResultsRepository.save(result);
  }
}
