package by.mashnyuk.orchestratorservice.service.impl;

import by.mashnyuk.orchestratorservice.client.AudioAnalysisClient;
import by.mashnyuk.orchestratorservice.client.IntelligenceAnalysisClient;
import by.mashnyuk.orchestratorservice.model.AnalysisStatus;
import by.mashnyuk.orchestratorservice.model.AnalysisType;
import by.mashnyuk.orchestratorservice.model.Language;
import by.mashnyuk.orchestratorservice.model.Presentation;
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

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class OrchestrationServiceImpl implements OrchestrationService {

  private final AudioAnalysisClient audioAnalysisClient;
  private final IntelligenceAnalysisClient intelligenceAnalysisClient;
  private final AnalysisJobsService analysisJobsService;
  private final AnalysisResultsService analysisResultsService;

  @Override
  @Async
  public UUID startAnalysisForTraining(Presentation presentation, float[] audioData) {
    UUID jobId = analysisJobsService.createJob(presentation.getId(), AnalysisType.FULL_SPEECH_COACH);

    runTrainingPipeline(jobId,presentation,audioData,presentation.getLanguage());
    return jobId;
  }

  @Async
  protected void runTrainingPipeline(UUID jobId, Presentation presentation, float[] audioData, Language language) {
    try {
      analysisJobsService.updateJobStatus(jobId, AnalysisStatus.WAITING_AUDIO_SERVICE, null);

      TranscriptionRequest audioRequest = new TranscriptionRequest(
              audioData, language, presentation.getDescription());
      AudioAnalysisResult audioResult = audioAnalysisClient.transcribe(audioRequest);

      analysisJobsService.updateJobStatus(jobId, AnalysisStatus.WAITING_INTELLIGENCE_SERVICE, null);

      IntelligenceAnalyzeRequest aiRequest = new IntelligenceAnalyzeRequest(
              presentation.getId(),
              presentation.getUserId(),
              language.toString(),
              audioResult.getTranscription(),
              audioResult.getSegments(),
              audioResult.getAudioMetrics()
      );

      IntelligenceAnalyzeResponse aiResponse = intelligenceAnalysisClient.analyze(aiRequest);

      analysisResultsService.saveAnalysisResult(jobId, aiResponse);
      analysisJobsService.updateJobStatus(jobId, AnalysisStatus.DONE, null);
    } catch (Exception e) {
      analysisJobsService.updateJobStatus(jobId, AnalysisStatus.FAILED, e.getMessage());
    }
  }

  @Override
  public UUID startMeetingTranscription(Presentation presentation, float[] audioData) {
    UUID jobId = analysisJobsService.createJob(presentation.getId(), AnalysisType.MEETING_TRANSCRIPTION);

    CompletableFuture.runAsync(() -> {
      try {
        analysisJobsService.updateJobStatus(jobId, AnalysisStatus.WAITING_AUDIO_SERVICE, null);

        TranscriptionRequest audioRequest = new TranscriptionRequest(audioData, presentation.getLanguage(), "Meeting Summary Mode");
        AudioAnalysisResult audioResult = audioAnalysisClient.transcribe(audioRequest);

        analysisJobsService.updateJobStatus(jobId, AnalysisStatus.WAITING_INTELLIGENCE_SERVICE, null);

        IntelligenceAnalyzeResponse aiResponse = intelligenceAnalysisClient.summarize(audioResult.getTranscription());

        analysisResultsService.saveAnalysisResult(jobId, aiResponse);
        analysisJobsService.updateJobStatus(jobId, AnalysisStatus.DONE, null);
      } catch (Exception e) {
        analysisJobsService.updateJobStatus(jobId, AnalysisStatus.FAILED, e.getMessage());
      }
    });

    return jobId;
  }
}
}
