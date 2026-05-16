package by.mashnyuk.orchestratorservice.client;

import by.mashnyuk.orchestratorservice.model.request.IntelligenceAnalyzeRequest;
import by.mashnyuk.orchestratorservice.model.response.IntelligenceAnalyzeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "intelligenceService",
        url = "${intelligence-service.url}"
)
public interface IntelligenceAnalysisClient {
  @PostMapping("/api/v1/analysis/report")
  IntelligenceAnalyzeResponse analyze(@RequestBody IntelligenceAnalyzeRequest request);

  @PostMapping("/api/v1/analysis/summary")
  String summarize(
          @RequestParam("text") String text,
          @RequestParam("language") String language
  );
}
