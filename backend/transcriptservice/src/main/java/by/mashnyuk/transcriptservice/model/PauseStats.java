package by.mashnyuk.transcriptservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PauseStats {

  private int count;
  private double totalMs;
  private double averageMs;
  private double maxMs;
}
