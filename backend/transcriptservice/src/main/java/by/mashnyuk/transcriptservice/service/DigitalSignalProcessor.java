package by.mashnyuk.transcriptservice.service;

import by.mashnyuk.transcriptservice.model.dto.response.AudioMetricsDto;

public interface DigitalSignalProcessor {
  AudioMetricsDto analyze(float[] audioData);
}
