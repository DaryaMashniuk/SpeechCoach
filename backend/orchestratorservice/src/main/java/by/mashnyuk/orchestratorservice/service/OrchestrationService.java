package by.mashnyuk.orchestratorservice.service;

import by.mashnyuk.orchestratorservice.model.Language;
import by.mashnyuk.orchestratorservice.model.Presentation;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface OrchestrationService {

  UUID startAnalysisForTraining(Presentation presentation, MultipartFile audioData);

  UUID startMeetingTranscription(Presentation presentation, MultipartFile audioData, boolean isTranslated, Language languageTranslate);
}
