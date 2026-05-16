package by.mashnyuk.transcriptservice.controller;

import by.mashnyuk.transcriptservice.model.dto.response.AudioMetricsDto;
import by.mashnyuk.transcriptservice.model.dto.response.AudioAnalysisResult;
import by.mashnyuk.transcriptservice.model.dto.response.TranscriptionResult;
import by.mashnyuk.transcriptservice.service.DigitalSignalProcessor;
import by.mashnyuk.transcriptservice.service.TranscribeProvider;
import by.mashnyuk.transcriptservice.util.AudioConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public AudioAnalysisResult training(
          @RequestPart("file") MultipartFile file,
          @RequestParam String language) throws IOException {
    float[] pcmData = audioConverter.convertToWhisperFormat(file);
    AudioMetricsDto audioResult = digitalSignalProcessor.analyze(pcmData);
    TranscriptionResult transcriptionResult = transcribeProvider.transcribe(pcmData, language, false);

    return new AudioAnalysisResult(
            transcriptionResult.getSegments(),
            transcriptionResult.getTranscription(),
            audioResult
    );
  }

  @PostMapping(value = "/meeting", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public TranscriptionResult transcribeMeeting(
          @RequestPart("file") MultipartFile file,
          @RequestParam String language,
          @RequestParam boolean translate) throws IOException {

    float[] pcmData = audioConverter.convertToWhisperFormat(file);

    return transcribeProvider.transcribe(pcmData, language, translate);
  }

}
