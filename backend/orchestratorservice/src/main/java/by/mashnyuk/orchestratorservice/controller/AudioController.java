package by.mashnyuk.orchestratorservice.controller;

import by.mashnyuk.orchestratorservice.model.Presentation;
import by.mashnyuk.orchestratorservice.model.request.PresentationRequest;
import by.mashnyuk.orchestratorservice.service.OrchestrationService;
import by.mashnyuk.orchestratorservice.service.PresentationService;
import by.mashnyuk.orchestratorservice.util.AudioConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orchestrator")
@RequiredArgsConstructor
public class AudioController {

  private final AudioConverter audioConverter;
  private final PresentationService presentationService;
  private final OrchestrationService orchestrationService;

  @PostMapping(
          path = "/training",
          consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
  )
  public UUID startTraining(@ModelAttribute PresentationRequest request) throws IOException {
    float[] audioData = audioConverter.convertToWhisperFormat(request.getFile());
    long durationMs = (long) ((audioData.length / 16000.0) * 1000);

    Presentation presentation = presentationService.createPresentation(request, durationMs);


    return orchestrationService.startAnalysisForTraining(
            presentation,
            audioData
    );
  }

  @PostMapping(path = "/meeting", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public UUID startMeeting(@ModelAttribute PresentationRequest request) throws IOException {
    float[] pcmData = audioConverter.convertToWhisperFormat(request.getFile());
    long durationMs = (long) ((pcmData.length / 16000.0) * 1000);

    Presentation presentation = presentationService.createPresentation(request,durationMs);
    return orchestrationService.startMeetingTranscription(presentation, pcmData);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    presentationService.deletePresentation(id);
  }
}
