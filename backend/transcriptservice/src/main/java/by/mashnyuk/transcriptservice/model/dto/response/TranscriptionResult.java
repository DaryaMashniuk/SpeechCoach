package by.mashnyuk.transcriptservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TranscriptionResult {
  List<TranscriptionSegment> segments;
  String transcription;
}
