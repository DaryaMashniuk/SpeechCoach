package by.mashnyuk.transcriptservice.controller;

import by.mashnyuk.transcriptservice.model.Language;
import by.mashnyuk.transcriptservice.model.dto.TranscriptionRequest;
import by.mashnyuk.transcriptservice.model.dto.TranscriptionResult;
import by.mashnyuk.transcriptservice.service.DigitalSignalProcessor;
import by.mashnyuk.transcriptservice.service.TranscribeProvider;
import by.mashnyuk.transcriptservice.util.AudioConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/transcription/api/v1")
@RequiredArgsConstructor
public class TranscriptionController {

  private final TranscribeProvider transcribeProvider;
  private final AudioConverter audioConverter;
  private final DigitalSignalProcessor digitalSignalProcessor;

  @PostMapping(
          path = "/",
          consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
  )
  public TranscriptionResult transcribe(@RequestPart MultipartFile file, Language language) throws IOException {
    float[] audioData = audioConverter.convertToWhisperFormat(file);
    TranscriptionRequest request = new TranscriptionRequest(
            audioData,
            language
    );

    digitalSignalProcessor.analyze(audioData);
    return transcribeProvider.transcribe(request);
  }
}
