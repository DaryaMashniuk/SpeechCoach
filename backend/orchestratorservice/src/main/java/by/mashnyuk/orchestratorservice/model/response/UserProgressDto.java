package by.mashnyuk.orchestratorservice.model.response;

import java.time.LocalDateTime;

public record UserProgressDto(
        LocalDateTime date,
        String presentationTitle,

        double overallScore,
        double confidenceScore,
        double clarityScore,

        double wpm,
        double fillerRatio,
        double pitchRange,
        double rhythmStability,
        double lexicalVariety,
        double coherence
) {}