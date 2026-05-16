package by.mashnyuk.orchestratorservice.model.response;

import by.mashnyuk.orchestratorservice.model.AnalysisStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record JobStatusResponse(
        UUID jobId,
        AnalysisStatus status,
        String errorMessage,
        boolean isDone
) {}