package by.mashnyuk.transcriptservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PitchesData {
  private double pitchTimeMillis;
  private double pitchSPL;
}
