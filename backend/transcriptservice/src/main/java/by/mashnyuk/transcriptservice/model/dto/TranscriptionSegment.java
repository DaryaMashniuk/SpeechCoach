package by.mashnyuk.transcriptservice.model.dto;

import lombok.Builder;

@Builder
public record TranscriptionSegment(
        String text,
        double start,
        double end,
        float confidence,
        float silenceProbability
) {}