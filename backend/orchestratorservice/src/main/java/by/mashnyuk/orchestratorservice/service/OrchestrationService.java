package by.mashnyuk.orchestratorservice.service;

import by.mashnyuk.orchestratorservice.model.Language;
import by.mashnyuk.orchestratorservice.model.Presentation;

import java.util.UUID;

public interface OrchestrationService {

  UUID startAnalysisForTraining(Presentation presentation, float[] audioData);

  UUID startMeetingTranscription(Presentation presentation, float[] audioData);
}
