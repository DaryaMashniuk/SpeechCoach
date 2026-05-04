package by.mashnyuk.transcriptservice.model.dto;

import by.mashnyuk.transcriptservice.model.Language;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TranscriptionRequest {

  private float[] pcmData;

  private Language language;

}
