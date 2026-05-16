package by.mashnyuk.orchestratorservice.service.impl;

import by.mashnyuk.orchestratorservice.client.AudioAnalysisClient;
import by.mashnyuk.orchestratorservice.client.IntelligenceAnalysisClient;
import by.mashnyuk.orchestratorservice.model.AnalysisStatus;
import by.mashnyuk.orchestratorservice.model.AnalysisType;
import by.mashnyuk.orchestratorservice.model.CustomMultipartFile;
import by.mashnyuk.orchestratorservice.model.Language;
import by.mashnyuk.orchestratorservice.model.Presentation;
import by.mashnyuk.orchestratorservice.model.request.IntelligenceAnalyzeRequest;
import by.mashnyuk.orchestratorservice.model.response.IntelligenceAnalyzeResponse;
import by.mashnyuk.orchestratorservice.model.response.AudioAnalysisResult;
import by.mashnyuk.orchestratorservice.model.response.MeetingTranscriptionResult;
import by.mashnyuk.orchestratorservice.model.response.TranscriptionResult;
import by.mashnyuk.orchestratorservice.service.AnalysisJobsService;
import by.mashnyuk.orchestratorservice.service.AnalysisResultsService;
import by.mashnyuk.orchestratorservice.service.OrchestrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class OrchestrationServiceImpl implements OrchestrationService {

  private final AudioAnalysisClient audioAnalysisClient;
  private final IntelligenceAnalysisClient intelligenceAnalysisClient;
  private final AnalysisJobsService analysisJobsService;
  private final AnalysisResultsService analysisResultsService;
  private final MinIoServiceImpl minIoService;
  @Value("${minio.bucket}")
  private String minioBucket;
//
  @Override
  public UUID startAnalysisForTraining(Presentation presentation, MultipartFile audioData) {
    UUID jobId = analysisJobsService.createJob(presentation.getId(),presentation.getUserId(), AnalysisType.FULL_SPEECH_COACH);
    byte[] audioBytes = minIoService.getFileById(minioBucket, presentation.getSourceFileId());
    MultipartFile multipartFile = new CustomMultipartFile(audioBytes, presentation.getTitle());
    CompletableFuture.runAsync(() -> {
      runTrainingPipeline(jobId, presentation, multipartFile, presentation.getLanguage());
    });

    return jobId;
  }


  protected void runTrainingPipeline(UUID jobId, Presentation presentation,MultipartFile audioData, Language language) {
    try {
      analysisJobsService.updateJobStatus(jobId, AnalysisStatus.WAITING_AUDIO_SERVICE, null);

      AudioAnalysisResult audioResult = audioAnalysisClient.audioAnalysis(audioData,language.name());

      analysisJobsService.updateJobStatus(jobId, AnalysisStatus.WAITING_INTELLIGENCE_SERVICE, null);

      IntelligenceAnalyzeRequest aiRequest = new IntelligenceAnalyzeRequest(
              presentation.getId(),
              presentation.getUserId(),
              presentation.getTitle(),
              language.toString(),
              audioResult.getTranscription(),
              audioResult.getSegments(),
              audioResult.getAudioMetrics()
      );

      IntelligenceAnalyzeResponse aiResponse = intelligenceAnalysisClient.analyze(aiRequest);
      IntelligenceAnalyzeResponse enrichedResponse = aiResponse.withAudioUrl(presentation.getSourceFileId());
      analysisResultsService.saveAnalysisResult(jobId, enrichedResponse);
      analysisJobsService.updateJobStatus(jobId, AnalysisStatus.DONE, null);
    } catch (Exception e) {
      analysisJobsService.updateJobStatus(jobId, AnalysisStatus.FAILED, e.getMessage());
    }
  }

  @Override
  public UUID startMeetingTranscription(Presentation presentation, MultipartFile audioData, boolean isTranslated, Language languageTranslate) {
    UUID jobId = analysisJobsService.createJob(presentation.getId(),presentation.getUserId(), AnalysisType.MEETING_TRANSCRIPTION);

    String fileId = presentation.getSourceFileId();

    CompletableFuture.runAsync(() -> {
      try {
        analysisJobsService.updateJobStatus(jobId, AnalysisStatus.WAITING_AUDIO_SERVICE, null);
        byte[] audioBytes = minIoService.getFileById(minioBucket, fileId);
        MultipartFile multipartFile = new CustomMultipartFile(audioBytes, presentation.getTitle());
        TranscriptionResult audioResult = audioAnalysisClient.transcribeMeeting(
                multipartFile,
                presentation.getLanguage().name(),
                isTranslated);

        analysisJobsService.updateJobStatus(jobId, AnalysisStatus.WAITING_INTELLIGENCE_SERVICE, null);

        String aiResponse = intelligenceAnalysisClient.summarize(audioResult.getTranscription(), String.valueOf(presentation.getLanguage()));
        MeetingTranscriptionResult result = MeetingTranscriptionResult.builder()
                .transcription(audioResult.getTranscription())
                .segments(audioResult.getSegments())
                .translatedText(audioResult.getTranslatedText())
                .translatedSegments(audioResult.getSegments())
                .summary(aiResponse)
                .audioUrl(presentation.getSourceFileId())
                .build();

        analysisResultsService.saveTranscriptionResult(jobId, result);
        analysisJobsService.updateJobStatus(jobId, AnalysisStatus.DONE, null);
      } catch (Exception e) {
        analysisJobsService.updateJobStatus(jobId, AnalysisStatus.FAILED, e.getMessage());
      }
    });

    return jobId;
  }
}
