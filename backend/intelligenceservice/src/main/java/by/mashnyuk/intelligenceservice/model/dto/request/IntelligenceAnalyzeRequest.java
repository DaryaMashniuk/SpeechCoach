package by.mashnyuk.intelligenceservice.model.dto.request;

import java.util.List;

public record IntelligenceAnalyzeRequest(
        String presentationId,
        String userId,
        String title,
        String language,
        String transcriptText,
        List<TranscriptionSegment> transcriptSegments,
        AudioMetricsDto audioMetrics
) {}