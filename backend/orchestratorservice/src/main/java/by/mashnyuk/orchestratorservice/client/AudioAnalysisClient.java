package by.mashnyuk.orchestratorservice.client;

import by.mashnyuk.orchestratorservice.model.request.TranscriptionRequest;
import by.mashnyuk.orchestratorservice.model.response.AudioAnalysisResult;
import by.mashnyuk.orchestratorservice.model.response.TranscriptionResult;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "transcriptionService",
        url = "${transcription-service.url}"
)
public interface AudioAnalysisClient {

  @PostMapping("/api/v1/transcription")
  AudioAnalysisResult audioAnalysis(TranscriptionRequest transcriptionRequest);

  @PostMapping("/api/v1/transcription/transcribe")
  TranscriptionResult transcribe(@RequestBody @Valid TranscriptionRequest request);
}
