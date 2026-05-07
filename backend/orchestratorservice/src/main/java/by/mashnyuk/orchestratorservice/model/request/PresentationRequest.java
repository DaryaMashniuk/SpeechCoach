package by.mashnyuk.orchestratorservice.model.request;


import by.mashnyuk.orchestratorservice.model.Language;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PresentationRequest {

  private float[] pcmData;

  private Language language;

  private Long userId;

  private String title;

  private String description;
}
