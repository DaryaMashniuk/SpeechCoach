package by.mashnyuk.orchestratorservice.service;

import by.mashnyuk.orchestratorservice.model.AnalysisStatus;
import by.mashnyuk.orchestratorservice.model.AnalysisType;
import by.mashnyuk.orchestratorservice.model.response.JobStatusResponse;

import java.util.UUID;

public interface AnalysisJobsService {
  UUID createJob(Long presentationId,Long userId, AnalysisType type);

  void updateJobStatus(UUID jobId, AnalysisStatus status, String errorMessage);

  JobStatusResponse getJobStatus(UUID jobId);
}
