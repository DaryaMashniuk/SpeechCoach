package by.mashnyuk.orchestratorservice.model.request;

import java.util.List;

public record IntelligenceAnalyzeRequest(
        Long presentationId,
        Long userId,
        String title,
        String language,
        String transcriptText,
        List<TranscriptionSegment> transcriptSegments,
        AudioMetricsDto audioMetrics
) {}