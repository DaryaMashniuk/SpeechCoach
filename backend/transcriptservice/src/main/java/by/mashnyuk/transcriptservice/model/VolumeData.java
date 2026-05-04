package by.mashnyuk.transcriptservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VolumeData {
  private double volumeTimeMillis;
  private double volumeRMS;
}
