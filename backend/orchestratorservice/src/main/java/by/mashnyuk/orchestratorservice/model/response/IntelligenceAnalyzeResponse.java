package by.mashnyuk.orchestratorservice.model.response;

import by.mashnyuk.orchestratorservice.model.metrics.BehavioralMetrics;
import by.mashnyuk.orchestratorservice.model.metrics.LexicalMetrics;
import by.mashnyuk.orchestratorservice.model.metrics.ProsodyMetrics;
import by.mashnyuk.orchestratorservice.model.metrics.StructureMetrics;
import by.mashnyuk.orchestratorservice.model.request.AudioMetricsDto;
import by.mashnyuk.orchestratorservice.model.request.TranscriptionSegment;

import java.util.List;


public record IntelligenceAnalyzeResponse(
        String presentationId,

        double scoreLogic,
        double scoreClarity,
        double scoreConfidence,
        double scoreTopicAdherence,

        LexicalMetrics lexical,
        ProsodyMetrics prosody,
        BehavioralMetrics behavior,
        StructureMetrics structure,

        List<TranscriptionSegment> transcriptSegments,
        AudioMetricsDto audioMetrics,

        List<String> keyErrors,
        List<String> tips,
        String summary,
        String transcript
) {}
