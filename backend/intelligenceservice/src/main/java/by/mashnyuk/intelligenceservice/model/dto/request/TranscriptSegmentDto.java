package by.mashnyuk.intelligenceservice.model.dto.request;

public record TranscriptSegmentDto(
        String speakerId,
        double startSec,
        double endSec,
        String text,
        double confidence
) {}