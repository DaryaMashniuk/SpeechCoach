package by.mashnyuk.transcriptservice.model.dto;

public record AudioPointDto(
        double timeSec,
        Double pitchHz,
        Double rms,
        Boolean speech
) {}
