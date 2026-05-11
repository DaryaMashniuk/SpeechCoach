package by.mashnyuk.transcriptservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class AudioAnalysisResult {
  List<TranscriptionSegment> segments;
  String transcription;
  AudioMetricsDto audioMetrics;
}
