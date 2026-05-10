package by.mashnyuk.transcriptservice.model.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record AudioMetricsDto(
        double avgPitchHz,
        double minPitchHz,
        double maxPitchHz,
        double pitchVariance,
        double avgRms,
        double maxRms,
        double rmsVariance,
        double speechRateWpm,
        double speechActivityRatio,
        double silenceRatio,
        int pauseCount,
        double avgPauseMs,
        double maxPauseMs,
        List<AudioPointDto> timeline
) {}
