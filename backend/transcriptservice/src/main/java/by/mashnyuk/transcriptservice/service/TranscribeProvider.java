package by.mashnyuk.transcriptservice.service;

import by.mashnyuk.transcriptservice.model.dto.request.TranscriptionRequest;
import by.mashnyuk.transcriptservice.model.dto.response.TranscriptionResult;

public interface TranscribeProvider {
  TranscriptionResult transcribe(float[] pcmData, String language, boolean translate);
  String providerName();
}
