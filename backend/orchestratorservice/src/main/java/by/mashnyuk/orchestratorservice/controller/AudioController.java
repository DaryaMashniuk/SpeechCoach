package by.mashnyuk.orchestratorservice.controller;

import by.mashnyuk.orchestratorservice.model.Presentation;
import by.mashnyuk.orchestratorservice.model.request.PresentationRequest;
import by.mashnyuk.orchestratorservice.model.response.IntelligenceAnalyzeResponse;
import by.mashnyuk.orchestratorservice.model.response.JobStatusResponse;
import by.mashnyuk.orchestratorservice.model.response.MeetingTranscriptionResult;
import by.mashnyuk.orchestratorservice.model.response.PresentationResponse;
import by.mashnyuk.orchestratorservice.model.response.UserProgressDto;
import by.mashnyuk.orchestratorservice.service.AnalysisJobsService;
import by.mashnyuk.orchestratorservice.service.AnalysisResultsService;
import by.mashnyuk.orchestratorservice.service.OrchestrationService;
import by.mashnyuk.orchestratorservice.service.PresentationService;
import by.mashnyuk.orchestratorservice.util.AudioConverter;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orchestrator")
@RequiredArgsConstructor
public class AudioController {

  private final AudioConverter audioConverter;
  private final PresentationService presentationService;
  private final OrchestrationService orchestrationService;
  private final AnalysisJobsService jobsService;
  private final AnalysisResultsService resultsService;

  @PostMapping(
          path = "/training",
          consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
  )
  public ResponseEntity<UUID> startTraining(@ModelAttribute PresentationRequest request) throws IOException {
    long durationMs = audioConverter.getDuration(request.getFile());
    Presentation presentation = presentationService.createPresentation(request, durationMs);

    return ResponseEntity.ok(orchestrationService.startAnalysisForTraining(
            presentation,
            request.getFile()
    ));
  }

  @PostMapping(path = "/meeting", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UUID> startMeeting(@ModelAttribute PresentationRequest request) throws IOException {
    long durationMs = audioConverter.getDuration(request.getFile());

    Presentation presentation = presentationService.createPresentation(request,durationMs);
    return ResponseEntity.ok(orchestrationService.startMeetingTranscription(presentation, request.getFile(), request.isTranslated(),request.getTranslatingLanguage())) ;
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    presentationService.deletePresentation(id);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/jobs/{jobId}/status")
  public ResponseEntity<JobStatusResponse> getJobStatus(@PathVariable UUID jobId) {
    return ResponseEntity.ok(jobsService.getJobStatus(jobId));
  }

  @GetMapping("/results/{jobId}")
  public ResponseEntity<IntelligenceAnalyzeResponse>  getReport(@PathVariable UUID jobId) {
    IntelligenceAnalyzeResponse response = (IntelligenceAnalyzeResponse) resultsService.getResultByJobId(jobId);
    return ResponseEntity.ok(response) ;
  }

  @GetMapping("/meetings/{jobId}")
  public ResponseEntity<MeetingTranscriptionResult>  getMeetingReport(@PathVariable UUID jobId) {
    MeetingTranscriptionResult response = (MeetingTranscriptionResult) resultsService.getResultByJobId(jobId);
    return ResponseEntity.ok(response) ;
  }

  @GetMapping("/analytics/progress")
  public ResponseEntity<List<UserProgressDto>>  getProgress(@RequestParam Long userId) {
    return ResponseEntity.ok(resultsService.getUserProgress(userId)) ;
  }

  @GetMapping("/presentations/user/{userId}")
  public ResponseEntity<Page<PresentationResponse>> getPresentations(
          @PathVariable Long userId,
          @RequestParam(required = false) Boolean training,
          @ParameterObject Pageable pageable) {
    return ResponseEntity.ok(presentationService.getUserPresentations(userId,training, pageable));
  }
}
