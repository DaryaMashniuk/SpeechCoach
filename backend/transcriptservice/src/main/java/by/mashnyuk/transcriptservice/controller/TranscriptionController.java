package by.mashnyuk.transcriptservice.controller;

import by.mashnyuk.transcriptservice.model.dto.response.AudioMetricsDto;
import by.mashnyuk.transcriptservice.model.dto.request.TranscriptionRequest;
import by.mashnyuk.transcriptservice.model.dto.response.AudioAnalysisResult;
import by.mashnyuk.transcriptservice.model.dto.response.TranscriptionResult;
import by.mashnyuk.transcriptservice.service.DigitalSignalProcessor;
import by.mashnyuk.transcriptservice.service.TranscribeProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transcription")
@RequiredArgsConstructor
public class TranscriptionController {

  private final TranscribeProvider transcribeProvider;
  private final DigitalSignalProcessor digitalSignalProcessor;

  @PostMapping()
  public AudioAnalysisResult transcribe(@RequestBody @Valid TranscriptionRequest request) {
    AudioMetricsDto audioResult = digitalSignalProcessor.analyze(request.getPcmData());
    TranscriptionResult transcriptionResult = transcribeProvider.transcribe(request);

    return new AudioAnalysisResult(
            transcriptionResult.getSegments(),
            transcriptionResult.getTranscription(),
            audioResult
    );
  }
}
