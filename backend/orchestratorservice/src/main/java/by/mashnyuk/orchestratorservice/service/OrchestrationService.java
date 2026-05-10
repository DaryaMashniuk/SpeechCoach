package by.mashnyuk.orchestratorservice.service;

import by.mashnyuk.orchestratorservice.model.Language;

public interface OrchestrationService {

  void startAnalysisForTraining(Long audioId, float[] audioData, Language language, String meetingContext);
}
