package by.mashnyuk.intelligenceservice.model.metrics;

import by.mashnyuk.intelligenceservice.model.Language;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LexicalMetrics {
  private int totalWords;
  private double wpm;
  private int fillerCount;
  private List<String> fillerWords;
  private double lexicalVariety;
  private double lexicalDensity;
  private int repetitionsCount;
  private double avgSentenceLength;
  private Language language;
}