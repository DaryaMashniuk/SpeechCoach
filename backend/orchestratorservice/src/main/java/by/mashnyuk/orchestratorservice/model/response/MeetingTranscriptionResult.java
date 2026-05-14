package by.mashnyuk.orchestratorservice.model.response;

import java.util.List;

public class MeetingTranscriptionResult {
  List<TranscriptionSegment> segments;
  String transcription;
  String summary;
}
