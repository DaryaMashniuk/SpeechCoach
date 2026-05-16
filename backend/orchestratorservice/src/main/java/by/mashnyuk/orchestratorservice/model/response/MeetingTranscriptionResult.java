package by.mashnyuk.orchestratorservice.model.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MeetingTranscriptionResult {
  List<TranscriptionSegment> segments;
  String transcription;
  String summary;
  String translatedText;
  List<TranscriptionSegment> translatedSegments;
  private String audioUrl;
}
