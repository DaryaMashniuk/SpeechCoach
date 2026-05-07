package by.mashnyuk.orchestratorservice.service.impl;

import by.mashnyuk.orchestratorservice.model.AnalysisJobs;
import by.mashnyuk.orchestratorservice.model.AnalysisStatus;
import by.mashnyuk.orchestratorservice.model.AnalysisType;
import by.mashnyuk.orchestratorservice.repository.AnalysisJobsRepository;
import by.mashnyuk.orchestratorservice.service.AnalysisJobsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AnalysisJobsServiceImpl implements AnalysisJobsService {

  private final AnalysisJobsRepository analysisJobsRepository;

  @Override
  public void createJob(Long audioId, AnalysisType type) {
    AnalysisJobs analysisJobs = AnalysisJobs.builder()
            .audioId(audioId)
            .analysisType(type)
            .analysisStatus(AnalysisStatus.PENDING)
            .build();
    analysisJobsRepository.save(analysisJobs);
  }

  @Override
  public void updateJobStatus(Long audioId, AnalysisStatus status, String errorMessage) {
    analysisJobsRepository.findById(audioId).ifPresent(job -> {
      job.setAnalysisStatus(status);
      if (errorMessage != null) job.setErrorMessage(errorMessage);
      if (status == AnalysisStatus.DONE) job.setFinishedAt(LocalDateTime.now());
      analysisJobsRepository.save(job);
    });
  }
}
