package by.mashnyuk.intelligenceservice.model.dto.request;

public record AudioPointDto(
        double timeSec,
        Double pitchHz,
        Double rms,
        Boolean speech
) {}
