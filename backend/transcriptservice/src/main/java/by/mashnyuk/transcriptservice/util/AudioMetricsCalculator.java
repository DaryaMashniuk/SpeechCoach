package by.mashnyuk.transcriptservice.util;

import by.mashnyuk.transcriptservice.model.PauseStats;
import by.mashnyuk.transcriptservice.model.PitchesData;
import by.mashnyuk.transcriptservice.model.VolumeData;
import by.mashnyuk.transcriptservice.model.dto.response.AudioMetricsDto;
import by.mashnyuk.transcriptservice.model.dto.response.AudioPointDto;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;

@UtilityClass
public class AudioMetricsCalculator {

  private static final double SILENCE_THRESHOLD = -60.0;
  private static final double TIME_FOR_SILENCE_THRESHOLD = 0.3;
  private static final double MIN_VOICE_HZ = 50.0;
  private static final double MAX_VOICE_HZ = 600.0;

  public AudioMetricsDto calculate(List<PitchesData> pitches, List<VolumeData> volumes) {
    if (volumes.isEmpty()) return AudioMetricsDto.builder().build();

    List<Double> filteredPitches = pitches.stream()
            .map(PitchesData::getPitchSPL)
            .filter(p -> p >= MIN_VOICE_HZ && p <= MAX_VOICE_HZ)
            .toList();

    double duration = volumes.get(volumes.size() - 1).getVolumeTimeMillis();

    DoubleSummaryStatistics pitchStats = filteredPitches.stream()
            .mapToDouble(d -> d)
            .summaryStatistics();

    double avgPitch = pitchStats.getAverage();
    double pitchVariance = calculateVariance(filteredPitches.stream().mapToDouble(d -> d).toArray(),avgPitch);

    DoubleSummaryStatistics rmsStats = volumes.stream()
            .mapToDouble(VolumeData::getVolumeRMS)
            .summaryStatistics();

    double avgVolume = rmsStats.getAverage();
    double rmsVariance = calculateVariance(volumes.stream().mapToDouble(VolumeData::getVolumeRMS).toArray(),avgVolume);

    PauseStats pauseStats = calculatePauses(volumes);

    return AudioMetricsDto.builder()
            .avgPitchHz(avgPitch)
            .minPitchHz(pitchStats.getMin() == Double.POSITIVE_INFINITY ? 0 : pitchStats.getMin())
            .maxPitchHz(pitchStats.getMax() == Double.NEGATIVE_INFINITY ? 0 : pitchStats.getMax())
            .pitchVariance(pitchVariance)
            .avgRms(avgVolume)
            .maxRms(pitchStats.getMax())
            .rmsVariance(rmsVariance)
            .pauseCount(pauseStats.getCount())
            .avgPauseMs(pauseStats.getAverageMs())
            .maxPauseMs(pauseStats.getMaxMs())
            .durationMs(duration)
            .silenceRatio(pauseStats.getTotalMs() / (duration))
            .speechActivityRatio(1.0 - (pauseStats.getTotalMs() / (duration)))
            .timeline(buildTimeline(pitches,volumes))
            .build();
  }

  private PauseStats calculatePauses(List<VolumeData> volumes) {
    int count = 0;
    double totalMs = 0;
    double maxMs = 0;

    Double pauseStart = null;

    for (VolumeData v : volumes) {
      if (v.getVolumeRMS() < SILENCE_THRESHOLD) {
        if (pauseStart == null) pauseStart = v.getVolumeTimeMillis();
      } else {
        if (pauseStart != null) {
          double duration = v.getVolumeTimeMillis() - pauseStart;
          if (duration > TIME_FOR_SILENCE_THRESHOLD) {
            count++;
            totalMs += (duration* 1000);
            if (duration * 1000 > maxMs) maxMs = duration * 1000;
          }
          pauseStart = null;
        }
      }
    }
    double avg = count > 0 ? totalMs/count : 0;
    return new PauseStats(count, totalMs, avg, maxMs);
  }

  private double calculateVariance(double[] values, double avgPitch) {
    if (values == null || values.length == 0) {
      return 0;
    }
    double sum = 0;
    for (double v : values) sum += Math.pow(v-avgPitch,2);
    return sum / values.length;
  }

  private List<AudioPointDto> buildTimeline(List<PitchesData> pitches, List<VolumeData> volumes){
    List<AudioPointDto> audioPoints = new ArrayList<>();

    for (VolumeData v : volumes) {
      double time = v.getVolumeTimeMillis();
      Double pitch = findClosestPitch(pitches,time);

      audioPoints.add(new AudioPointDto(
              time,
              pitch,
              v.getVolumeRMS(),
              pitch != null && v.getVolumeRMS() > -60
      ));
    }
    return audioPoints;
  }

  private Double findClosestPitch(List<PitchesData> pitches, double time) {
    if (pitches == null || pitches.isEmpty()){
      return null;
    }

    final double maxTimeDiff = 0.1;

    int low = 0;
    int high = pitches.size() - 1;

    while (low <= high){
      int mid = (low + high)/2;
      double midTime = pitches.get(mid).getPitchTimeMillis();

      if (midTime < time){
        low = mid + 1;
      } else if (midTime > time){
        high = mid - 1;
      } else {
        return pitches.get(mid).getPitchSPL();
      }
    }

    PitchesData closest;

    if (low >= pitches.size()){
      closest = pitches.get(high);
    } else if (high < 0) {
      closest = pitches.get(low);
    } else {
      double lowDiff = Math.abs(pitches.get(low).getPitchTimeMillis() - time);
      double highDiff = Math.abs(pitches.get(high).getPitchTimeMillis() - time);
      closest = (lowDiff < highDiff) ? pitches.get(low) : pitches.get(high);
    }

    double finalDiff = Math.abs(closest.getPitchTimeMillis() - time);
    if (finalDiff <= maxTimeDiff){
      return closest.getPitchSPL();
    }

    return null;
  }
}
