package by.mashnyuk.orchestratorservice.model.response;

import by.mashnyuk.orchestratorservice.model.request.TranscriptionSegment;
import lombok.Builder;

import java.util.List;


@Builder
public record IntelligenceAnalyzeResponse(
        String presentationId,
        double scoreLogic,
        double scoreClarity,
        double scoreConfidence,
        double scoreTopicAdherence,
        List<String> keyErrors,
        List<String> tips,
        String summary,
        String structuredAnalysisJson,
        List<TranscriptionSegment> transcription,
        String transcriptionText
) {}
