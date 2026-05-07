package by.mashnyuk.orchestratorservice.client;

import by.mashnyuk.orchestratorservice.model.Language;
import by.mashnyuk.orchestratorservice.model.response.TranscriptionResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "transcriptionService",
        url = "${transcription-service.url}"
)
public interface AudioAnalysisClient {

  @PostMapping("/api/v1/transcription")
  TranscriptionResult transcribe(float[] audioData, Language language);
}
