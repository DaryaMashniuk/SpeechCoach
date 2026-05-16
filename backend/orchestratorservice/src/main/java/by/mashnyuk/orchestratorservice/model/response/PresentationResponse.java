package by.mashnyuk.orchestratorservice.model.response;

import by.mashnyuk.orchestratorservice.model.Language;

import java.time.LocalDateTime;
import java.util.UUID;

public record PresentationResponse(
        Long id,
        UUID jobId,
        String title,
        String description,
        Long durationMs,
        Language language,
        LocalDateTime createdAt,
        String audioUrl
) {}
