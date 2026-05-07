package by.mashnyuk.orchestratorservice.service.impl;

import by.mashnyuk.orchestratorservice.model.Presentation;
import by.mashnyuk.orchestratorservice.model.request.PresentationRequest;
import by.mashnyuk.orchestratorservice.repository.PresentationRepository;
import by.mashnyuk.orchestratorservice.service.PresentationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PresentationServiceImpl implements PresentationService {

  private final PresentationRepository presentationRepository;
  private final MinIoServiceImpl minIoService;

  @Override
  public void createPresentation(PresentationRequest presentationRequest) {

    String fileId = UUID.randomUUID().toString();

    minIoService.uploadFile("audio-bucket", fileId, presentationRequest.getPcmData());

    Presentation presentation = Presentation.builder()
            .description(presentationRequest.getDescription())
            .title(presentationRequest.getTitle())
            .userId(presentationRequest.getUserId())
            .sourceFileId(fileId)
            .build();

    presentationRepository.save(presentation);
  }
}
