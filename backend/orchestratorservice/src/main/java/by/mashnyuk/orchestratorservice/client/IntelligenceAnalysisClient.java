package by.mashnyuk.orchestratorservice.client;

import by.mashnyuk.orchestratorservice.model.request.IntelligenceAnalyzeRequest;
import by.mashnyuk.orchestratorservice.model.response.IntelligenceAnalyzeResponse;
import by.mashnyuk.orchestratorservice.model.response.MeetingTranscriptionResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "intelligenceService",
        url = "${intelligence-service.url}"
)
public interface IntelligenceAnalysisClient {
  @PostMapping("/api/v1/analysis/report")
  IntelligenceAnalyzeResponse analyze(@RequestBody IntelligenceAnalyzeRequest request);

  @PostMapping("/api/v1/analysis/summary")
  MeetingTranscriptionResult summarize(@RequestBody String transcription);
}
