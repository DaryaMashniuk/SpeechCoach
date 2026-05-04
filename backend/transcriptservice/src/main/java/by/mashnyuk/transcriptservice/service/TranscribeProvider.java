package by.mashnyuk.transcriptservice.service;

import by.mashnyuk.transcriptservice.model.dto.TranscriptionRequest;
import by.mashnyuk.transcriptservice.model.dto.TranscriptionResult;

public interface TranscribeProvider {
  TranscriptionResult transcribe(TranscriptionRequest request);
  String providerName();
}
