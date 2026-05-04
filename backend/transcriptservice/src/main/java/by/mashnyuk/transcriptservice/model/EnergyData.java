package by.mashnyuk.transcriptservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EnergyData {
  private long energyTimeMillis;
  private double energyRMS;
}
