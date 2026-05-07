package by.mashnyuk.orchestratorservice.controller;

import by.mashnyuk.orchestratorservice.model.Language;
import by.mashnyuk.orchestratorservice.model.request.PresentationRequest;
import by.mashnyuk.orchestratorservice.service.AnalysisJobsService;
import by.mashnyuk.orchestratorservice.service.PresentationService;
import by.mashnyuk.orchestratorservice.util.AudioConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/orchestrator/api/v1")
@RequiredArgsConstructor
public class AudioController {

  private final AudioConverter audioConverter;
  private final PresentationService presentationService;
  private final AnalysisJobsService analysisJobsService;

  @PostMapping(
          path = "/",
          consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
  )
  public void transcribe(@RequestPart MultipartFile file, PresentationRequest presentationRequest) throws IOException {
    float[] audioData = audioConverter.convertToWhisperFormat(file);
    presentationRequest.setPcmData(audioData);
    presentationService.createPresentation(presentationRequest);

   // return transcribeProvider.transcribe(request);
  }
}
