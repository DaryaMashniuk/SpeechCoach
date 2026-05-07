package by.mashnyuk.intelligenceservice.model.dto.request;

import java.util.List;

public record IntelligenceAnalyzeRequest(
        String presentationId,
        String userId,
        String language,
        String transcriptText,
        List<TranscriptSegmentDto> transcriptSegments,
        AudioMetricsDto audioMetrics
) {}