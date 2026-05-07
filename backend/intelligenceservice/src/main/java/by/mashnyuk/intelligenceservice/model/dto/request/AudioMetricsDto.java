package by.mashnyuk.intelligenceservice.model.dto.request;

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
