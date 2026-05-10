package by.mashnyuk.orchestratorservice.model.response;

import by.mashnyuk.orchestratorservice.model.request.AudioMetricsDto;
import by.mashnyuk.orchestratorservice.model.request.TranscriptionSegment;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AudioAnalysisResult {
  List<TranscriptionSegment> segments;
  String transcription;
  AudioMetricsDto audioMetrics;
}
