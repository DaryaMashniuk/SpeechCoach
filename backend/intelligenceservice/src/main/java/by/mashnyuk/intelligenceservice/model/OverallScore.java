package by.mashnyuk.intelligenceservice.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OverallScore {
  private double overall;
  private double delivery;
  private double clarity;
  private double engagement;
}
