package by.mashnyuk.orchestratorservice.service;

import by.mashnyuk.orchestratorservice.model.Presentation;
import by.mashnyuk.orchestratorservice.model.request.PresentationRequest;
import by.mashnyuk.orchestratorservice.model.response.PresentationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PresentationService {

  Presentation createPresentation(PresentationRequest presentationRequest,Long durationMs);

  Page<PresentationResponse> getUserPresentations(Long userId, Boolean training, Pageable pageable);

  void deletePresentation(Long id);
}
