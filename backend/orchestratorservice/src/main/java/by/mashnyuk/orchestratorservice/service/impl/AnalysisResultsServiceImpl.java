package by.mashnyuk.orchestratorservice.service.impl;

import by.mashnyuk.orchestratorservice.exceptions.ResultDoesNotExistException;
import by.mashnyuk.orchestratorservice.model.AnalysisResults;
import by.mashnyuk.orchestratorservice.model.AnalysisStatus;
import by.mashnyuk.orchestratorservice.model.response.IntelligenceAnalyzeResponse;
import by.mashnyuk.orchestratorservice.model.response.MeetingTranscriptionResult;
import by.mashnyuk.orchestratorservice.model.response.UserProgressDto;
import by.mashnyuk.orchestratorservice.repository.AnalysisJobsRepository;
import by.mashnyuk.orchestratorservice.repository.AnalysisResultsRepository;
import by.mashnyuk.orchestratorservice.service.AnalysisResultsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AnalysisResultsServiceImpl implements AnalysisResultsService {

  private final AnalysisResultsRepository analysisResultsRepository;
  private final MinIoServiceImpl minIoService;
  private final AnalysisJobsRepository analysisJobsRepository;
  @Value("${minio.bucket}")
  private String minioBucket;

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
            .translatedText(transcriptionResult.getTranslatedText())
            .analysisJobId(jobId)
            .meetingTranscriptionResult(transcriptionResult)
            .modelVersion("llama3.2-speech-v1")
            .build();
    analysisResultsRepository.save(result);
  }

  @Override
  @Transactional(readOnly = true)
  public Object getResultByJobId(UUID jobId) {
    AnalysisResults result = analysisResultsRepository.findByAnalysisJobId(jobId)
            .orElseThrow(() -> new ResultDoesNotExistException("Result not ready or not found"));

    if (result.getFullReport() != null) {
      IntelligenceAnalyzeResponse report = result.getFullReport();
      String fileId = report.audioUrl();
      String presignedUrl = minIoService.getPresignedUrl(minioBucket, fileId);
      return report.withAudioUrl(presignedUrl);
    }

    if (result.getMeetingTranscriptionResult() != null) {
      MeetingTranscriptionResult meeting = result.getMeetingTranscriptionResult();
      String presignedUrl = minIoService.getPresignedUrl(minioBucket, meeting.getAudioUrl());
      meeting.setAudioUrl(presignedUrl);
      return meeting;
    }

    throw new ResultDoesNotExistException("Job exists but no report data found");
  }

  @Override
  public List<UserProgressDto> getUserProgress(Long userId) {
      List<AnalysisResults> results = analysisResultsRepository
              .findAllByUserIdAndStatus(userId, AnalysisStatus.DONE);

      if (results.isEmpty()) {
        return List.of();
      }

      return results.stream()
              .filter(res -> res.getFullReport() != null)
              .map(result -> {
                IntelligenceAnalyzeResponse report = result.getFullReport();

                return new UserProgressDto(
                        result.getCreatedAt(),
                        report.title(),
                        report.overallScore(),
                        report.scoreConfidence(),
                        report.scoreClarity(),
                        report.lexical() != null ? report.lexical().getWpm() : 0.0,
                        report.lexical() != null ? (double) report.lexical().getFillerCount() : 0.0,
                        report.prosody() != null ? report.prosody().getPitchRange() : 0.0,
                        report.prosody() != null ? report.prosody().getRhythmStability() : 0.0,
                        report.lexical() != null ? report.lexical().getLexicalVariety() : 0.0,
                        report.structure() != null ? report.structure().getCoherenceScore() : 0.0
                );
              })
              .collect(Collectors.toList());
    }
}
