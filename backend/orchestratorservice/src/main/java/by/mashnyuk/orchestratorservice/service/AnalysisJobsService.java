package by.mashnyuk.orchestratorservice.service;

import by.mashnyuk.orchestratorservice.model.AnalysisStatus;
import by.mashnyuk.orchestratorservice.model.AnalysisType;

public interface AnalysisJobsService {
  void createJob(Long audioId, AnalysisType type);

  void updateJobStatus(Long audioId, AnalysisStatus status, String errorMessage);
}
