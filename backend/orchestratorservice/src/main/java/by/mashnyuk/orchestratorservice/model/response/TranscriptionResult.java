package by.mashnyuk.orchestratorservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TranscriptionResult {
  List<TranscriptionSegment> segments;
  String transcription;
  List<TranscriptionSegment> translatedSegments;
  String translatedText;
}
