package by.mashnyuk.orchestratorservice.client;

import by.mashnyuk.orchestratorservice.model.request.IntelligenceAnalyzeRequest;
import by.mashnyuk.orchestratorservice.model.response.IntelligenceAnalyzeResponse;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = "intelligenceService",
        path = "${intelligence-service.url}"
)
public interface IntelligenceAnalysisClient {
  IntelligenceAnalyzeResponse analyze(IntelligenceAnalyzeRequest request);

  IntelligenceAnalyzeResponse summarize(String transcription);
}
