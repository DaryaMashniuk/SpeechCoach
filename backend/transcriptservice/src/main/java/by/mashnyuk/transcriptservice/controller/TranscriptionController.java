package by.mashnyuk.transcriptservice.controller;

import by.mashnyuk.transcriptservice.model.Language;
import by.mashnyuk.transcriptservice.model.dto.response.AudioMetricsDto;
import by.mashnyuk.transcriptservice.model.dto.request.TranscriptionRequest;
import by.mashnyuk.transcriptservice.model.dto.response.AudioAnalysisResult;
import by.mashnyuk.transcriptservice.model.dto.response.TranscriptionResult;
import by.mashnyuk.transcriptservice.service.DigitalSignalProcessor;
import by.mashnyuk.transcriptservice.service.TranscribeProvider;
import by.mashnyuk.transcriptservice.util.AudioConverter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/transcription")
@RequiredArgsConstructor
public class TranscriptionController {

  private final TranscribeProvider transcribeProvider;
  private final DigitalSignalProcessor digitalSignalProcessor;
  private final AudioConverter audioConverter;

  @PostMapping()
  public AudioAnalysisResult training(@RequestBody @Valid TranscriptionRequest request) {
    AudioMetricsDto audioResult = digitalSignalProcessor.analyze(request.getPcmData());
    TranscriptionResult transcriptionResult = transcribeProvider.transcribe(request);

    return new AudioAnalysisResult(
            transcriptionResult.getSegments(),
            transcriptionResult.getTranscription(),
            audioResult
    );
  }

  @PostMapping("/transcribe")
  public TranscriptionResult transcribe(@RequestBody @Valid TranscriptionRequest request) {
    return transcribeProvider.transcribe(request);
  }

  @PostMapping(
          path = "/training",
          consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
  )
  public AudioAnalysisResult transcribeDemo (@RequestPart MultipartFile file, Language language) throws IOException {
    float[] audioData = audioConverter.convertToWhisperFormat(file);
    AudioMetricsDto audioResult = digitalSignalProcessor.analyze(audioData);
    TranscriptionRequest request = new TranscriptionRequest(audioData, language, "");
    TranscriptionResult transcriptionResult = transcribeProvider.transcribe(request);

    return new AudioAnalysisResult(
            transcriptionResult.getSegments(),
            transcriptionResult.getTranscription(),
            audioResult
    );
  }
}
