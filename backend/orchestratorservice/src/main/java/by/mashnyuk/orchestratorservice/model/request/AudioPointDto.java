package by.mashnyuk.orchestratorservice.model.request;

public record AudioPointDto(
        double timeSec,
        Double pitchHz,
        Double rms,
        Boolean speech
) {}
