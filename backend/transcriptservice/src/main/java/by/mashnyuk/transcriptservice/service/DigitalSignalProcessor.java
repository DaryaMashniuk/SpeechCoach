package by.mashnyuk.transcriptservice.service;

import by.mashnyuk.transcriptservice.model.AudioAnalysisReport;

public interface DigitalSignalProcessor {
  AudioAnalysisReport analyze(float[] audioData);
}
