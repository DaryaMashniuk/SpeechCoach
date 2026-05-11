package by.mashnyuk.orchestratorservice.service.impl;

import by.mashnyuk.orchestratorservice.model.Presentation;
import by.mashnyuk.orchestratorservice.model.request.PresentationRequest;
import by.mashnyuk.orchestratorservice.repository.PresentationRepository;
import by.mashnyuk.orchestratorservice.service.PresentationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PresentationServiceImpl implements PresentationService {

  private final PresentationRepository presentationRepository;
  private final MinIoServiceImpl minIoService;
  @Value("${minio.bucket}")
  private String minioBucket;

  @Override
  public Presentation createPresentation(PresentationRequest presentationRequest) {

    String fileId = UUID.randomUUID().toString();

    minIoService.uploadFile(minioBucket, fileId, presentationRequest.getFile());

    Presentation presentation = Presentation.builder()
            .description(presentationRequest.getDescription())
            .title(presentationRequest.getTitle())
            .language(presentationRequest.getLanguage())
            .training(presentationRequest.isTraining())
            .userId(presentationRequest.getUserId())
            .sourceFileId(fileId)
            .build();

    return presentationRepository.save(presentation);
  }

  @Override
  @Transactional
  public void deletePresentation(Long id) {
    Presentation presentation = presentationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Presentation not found"));


    minIoService.deleteFile(minioBucket, presentation.getSourceFileId());

    presentationRepository.delete(presentation);
  }
}
