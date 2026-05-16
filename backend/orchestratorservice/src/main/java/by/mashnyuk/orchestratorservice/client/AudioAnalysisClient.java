package by.mashnyuk.orchestratorservice.client;

import by.mashnyuk.orchestratorservice.model.response.AudioAnalysisResult;
import by.mashnyuk.orchestratorservice.model.response.TranscriptionResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(
        name = "transcriptionService",
        url = "${transcription-service.url}"
)
public interface AudioAnalysisClient {

  @PostMapping(value = "/api/v1/transcription", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  AudioAnalysisResult audioAnalysis(
          @RequestPart("file") MultipartFile file,
          @RequestParam("language") String language
  );

  @PostMapping(value = "/api/v1/transcription/meeting", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  TranscriptionResult transcribeMeeting(
          @RequestPart("file") MultipartFile file,
          @RequestParam("language") String language,
          @RequestParam("translate") boolean translate
  );
}
