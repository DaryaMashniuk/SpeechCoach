package by.mashnyuk.intelligenceservice.model.dto.response;

import java.util.List;

public class MeetingTranscriptionResult {
  List<TranscriptionSegment> segments;
  String transcription;
  String summary;
}
