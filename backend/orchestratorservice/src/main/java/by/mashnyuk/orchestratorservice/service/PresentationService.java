package by.mashnyuk.orchestratorservice.service;

import by.mashnyuk.orchestratorservice.model.Presentation;
import by.mashnyuk.orchestratorservice.model.request.PresentationRequest;

public interface PresentationService {

  Presentation createPresentation(PresentationRequest presentationRequest);

  void deletePresentation(Long id);
}
