package by.mashnyuk.transcriptservice.model;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class AudioAnalysisReport {
  List<PitchesData> pitches = new ArrayList<>();
  List<VolumeData> volumes = new ArrayList<>();
}
