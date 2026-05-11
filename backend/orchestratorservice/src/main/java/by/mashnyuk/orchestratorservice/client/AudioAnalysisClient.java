package by.mashnyuk.orchestratorservice.client;

import by.mashnyuk.orchestratorservice.model.request.TranscriptionRequest;
import by.mashnyuk.orchestratorservice.model.response.AudioAnalysisResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "transcriptionService",
        url = "${transcription-service.url}"
)
public interface AudioAnalysisClient {

  @PostMapping("/api/v1/transcription")
  AudioAnalysisResult transcribe(TranscriptionRequest transcriptionRequest);
}
