package by.mashnyuk.orchestratorservice.service.impl;

import by.mashnyuk.orchestratorservice.model.AnalysisResults;
import by.mashnyuk.orchestratorservice.model.response.IntelligenceAnalyzeResponse;
import by.mashnyuk.orchestratorservice.repository.AnalysisResultsRepository;
import by.mashnyuk.orchestratorservice.service.AnalysisResultsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalysisResultsServiceImpl implements AnalysisResultsService {

  private final AnalysisResultsRepository analysisResultsRepository;

  @Override
  public void saveAnalysisResult(UUID jobId, IntelligenceAnalyzeResponse response) {
    // TODO sync dto from Intelligence service to the one saved in the result
    AnalysisResults result = AnalysisResults.builder()
            .analysisJobId(jobId)
            .transcript(response.transcription())
            .tips(response.tips())
            .scores(Map.of(
                 "logic",response.scoreLogic(),
                 "clarity",response.scoreClarity(),
                 "confidence",response.scoreConfidence()
            ))
            .build();
    analysisResultsRepository.save(result);
  }
}
