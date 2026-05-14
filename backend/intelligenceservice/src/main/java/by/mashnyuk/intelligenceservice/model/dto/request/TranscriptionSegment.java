package by.mashnyuk.intelligenceservice.model.dto.request;

import lombok.Builder;

@Builder
public record TranscriptionSegment(
        String text,
        double start,
        double end,
        float confidence,
        float silenceProbability
) {}