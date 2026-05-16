package by.mashnyuk.intelligenceservice.model.dto.response;

import by.mashnyuk.intelligenceservice.model.dto.request.AudioMetricsDto;
import by.mashnyuk.intelligenceservice.model.dto.request.TranscriptionSegment;
import by.mashnyuk.intelligenceservice.model.metrics.BehavioralMetrics;
import by.mashnyuk.intelligenceservice.model.metrics.LexicalMetrics;
import by.mashnyuk.intelligenceservice.model.metrics.ProsodyMetrics;
import by.mashnyuk.intelligenceservice.model.metrics.StructureMetrics;
import lombok.Builder;

import java.util.List;


@Builder
public record IntelligenceAnalyzeResponse(
        String presentationId,

        double scoreLogic,
        double scoreClarity,
        double scoreConfidence,
        double overallScore,

        LexicalMetrics lexical,
        ProsodyMetrics prosody,
        BehavioralMetrics behavior,
        StructureMetrics structure,

        List<TranscriptionSegment> transcriptSegments,
        AudioMetricsDto audioMetrics,

        List<String> keyErrors,
        List<String> tips,
        String summary,
        String transcript,
        String title
) {}
