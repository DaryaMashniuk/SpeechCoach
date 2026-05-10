package by.mashnyuk.transcriptservice.model.dto.response;

public record AudioPointDto(
        double timeSec,
        Double pitchHz,
        Double rms,
        Boolean speech
) {}
