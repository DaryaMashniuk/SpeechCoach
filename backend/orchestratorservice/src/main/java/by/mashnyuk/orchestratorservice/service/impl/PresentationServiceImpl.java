package by.mashnyuk.orchestratorservice.service.impl;

import by.mashnyuk.orchestratorservice.model.AnalysisJobs;
import by.mashnyuk.orchestratorservice.model.Presentation;
import by.mashnyuk.orchestratorservice.model.request.PresentationRequest;
import by.mashnyuk.orchestratorservice.model.response.PresentationResponse;
import by.mashnyuk.orchestratorservice.repository.AnalysisJobsRepository;
import by.mashnyuk.orchestratorservice.repository.PresentationRepository;
import by.mashnyuk.orchestratorservice.service.PresentationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PresentationServiceImpl implements PresentationService {

  private final PresentationRepository presentationRepository;
  private final AnalysisJobsRepository analysisJobsRepository;
  private final MinIoServiceImpl minIoService;
  @Value("${minio.bucket}")
  private String minioBucket;

  @Override
  public Presentation createPresentation(PresentationRequest presentationRequest,Long durationMs) {

    String fileId = UUID.randomUUID().toString();

    minIoService.uploadFile(minioBucket, fileId, presentationRequest.getFile());

    Presentation presentation = Presentation.builder()
            .description(presentationRequest.getDescription())
            .title(presentationRequest.getTitle())
            .language(presentationRequest.getLanguage())
            .training(presentationRequest.isTraining())
            .userId(presentationRequest.getUserId())
            .durationMs(durationMs)
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

  @Override
  public Page<PresentationResponse> getUserPresentations(Long userId, Boolean training, Pageable pageable) {
    Page<Presentation> presentations;

    if (training != null) {
      presentations = presentationRepository.findAllByUserIdAndTraining(userId, training, pageable);
    } else {
      presentations = presentationRepository.findAllByUserId(userId, pageable);
    }

    return presentations.map(p -> {
      UUID currentJobId = analysisJobsRepository.findFirstByAudioIdOrderByCreatedAtDesc(p.getId())
              .map(AnalysisJobs::getId)
              .orElse(null);

      return new PresentationResponse(
              p.getId(),
              currentJobId,
              p.getTitle(),
              p.getDescription(),
              p.getDurationMs(),
              p.getLanguage(),
              p.getCreatedAt(),
              minIoService.getPresignedUrl(minioBucket, p.getSourceFileId())
      );
    });
  }
}
