package by.mashnyuk.orchestratorservice.service;

import by.mashnyuk.orchestratorservice.model.AnalysisStatus;
import by.mashnyuk.orchestratorservice.model.AnalysisType;

import java.util.UUID;

public interface AnalysisJobsService {
  UUID createJob(Long audioId, AnalysisType type);

  void updateJobStatus(UUID jobId, AnalysisStatus status, String errorMessage);
}
