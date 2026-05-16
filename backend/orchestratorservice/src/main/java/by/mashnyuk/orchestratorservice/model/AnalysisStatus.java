package by.mashnyuk.orchestratorservice.model;

public enum AnalysisStatus {
  PENDING,
  IN_PROGRESS,
  WAITING_AUDIO_SERVICE,
  WAITING_INTELLIGENCE_SERVICE,
  DONE,
  FAILED,
}
