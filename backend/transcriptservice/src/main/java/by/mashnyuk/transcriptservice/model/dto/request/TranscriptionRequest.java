package by.mashnyuk.transcriptservice.model.dto.request;

import by.mashnyuk.transcriptservice.model.Language;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TranscriptionRequest {

  private float[] pcmData;

  private Language language;

  private String meetingContext;

}
