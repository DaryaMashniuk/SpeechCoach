package by.mashnyuk.orchestratorservice.service.impl;

import by.mashnyuk.orchestratorservice.client.AudioAnalysisClient;
import by.mashnyuk.orchestratorservice.client.IntelligenceAnalysisClient;
import by.mashnyuk.orchestratorservice.model.AnalysisStatus;
import by.mashnyuk.orchestratorservice.model.Language;
import by.mashnyuk.orchestratorservice.model.request.IntelligenceAnalyzeRequest;
import by.mashnyuk.orchestratorservice.model.request.TranscriptionRequest;
import by.mashnyuk.orchestratorservice.model.response.IntelligenceAnalyzeResponse;
import by.mashnyuk.orchestratorservice.model.response.AudioAnalysisResult;
import by.mashnyuk.orchestratorservice.service.AnalysisJobsService;
import by.mashnyuk.orchestratorservice.service.AnalysisResultsService;
import by.mashnyuk.orchestratorservice.service.OrchestrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrchestrationServiceImpl implements OrchestrationService {

  private final AudioAnalysisClient audioAnalysisClient;
  private final IntelligenceAnalysisClient intelligenceAnalysisClient;
  private final AnalysisJobsService analysisJobsService;
  private final AnalysisResultsService analysisResultsService;

  @Override
  @Async
  public void startAnalysisForTraining(Long audioId, float[] audioData, Language language, String meetingContext) {
    try {
      analysisJobsService.updateJobStatus(audioId, AnalysisStatus.WAITING_AUDIO_SERVICE,null);

      TranscriptionRequest audioRequest = new TranscriptionRequest(audioData,language,meetingContext);

      AudioAnalysisResult audioAnalysisResult = audioAnalysisClient.transcribe(audioRequest);

      analysisJobsService.updateJobStatus(audioId,AnalysisStatus.WAITING_INTELLIGENCE_SERVICE,null);

      //TODO think if intelligence service needs user and audioId
      IntelligenceAnalyzeRequest request = new IntelligenceAnalyzeRequest(
              audioId,
              null,
              language.toString(),
              audioAnalysisResult.getTranscription(),
              audioAnalysisResult.getSegments(),
              audioAnalysisResult.getAudioMetrics()
      );

      IntelligenceAnalyzeResponse finalResponse = intelligenceAnalysisClient.analyze(request);

      analysisResultsService.saveAnalysisResult(audioId,finalResponse);
      analysisJobsService.updateJobStatus(audioId,AnalysisStatus.DONE,null);
    } catch (Exception e) {
      //TODO implement retry logic
      //TODO custom exceptions
      analysisJobsService.updateJobStatus(audioId,AnalysisStatus.FAILED, e.getMessage());
    }
  }
}
