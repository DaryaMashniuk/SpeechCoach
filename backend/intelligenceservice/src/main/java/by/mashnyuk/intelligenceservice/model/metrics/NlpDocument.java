package by.mashnyuk.intelligenceservice.model.metrics;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class NlpDocument {

  private List<String> sentences;

  private List<String> tokens;

  private List<String> lemmas;

  private List<String> posTags;

  private List<String> nounPhrases;

  private List<String> namedEntities;
}
