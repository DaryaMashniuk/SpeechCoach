package by.mashnyuk.transcriptservice.service.impl;

import by.mashnyuk.transcriptservice.exceptions.TranscriptionException;
import by.mashnyuk.transcriptservice.model.dto.request.TranscriptionRequest;
import by.mashnyuk.transcriptservice.model.dto.response.TranscriptionResult;
import by.mashnyuk.transcriptservice.model.dto.response.TranscriptionSegment;
import by.mashnyuk.transcriptservice.nativelib.internal.whisper_full_params;
import by.mashnyuk.transcriptservice.service.TranscribeProvider;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static by.mashnyuk.transcriptservice.nativelib.WhisperLib.*;

@Service
public class WhisperTranscribeProvider implements TranscribeProvider {

  private static final Logger logger = LogManager.getLogger(WhisperTranscribeProvider.class);

  private static final String MODEL_PATH = "backend/infrastructure/models/whisper/ggml-base.bin";
  private static final int STRATEGY_GREEDY = 0;

  private MemorySegment ctx;
  private Arena serviceArena;

  @PostConstruct
  void init() {
    try {
      serviceArena = Arena.ofShared();

      Path modelFilePath = Path.of(MODEL_PATH).toAbsolutePath();
      if (!modelFilePath.toFile().exists()) {
        throw new IllegalStateException("Model file not found at: " + modelFilePath);
      }

      MemorySegment cModelPath = serviceArena.allocateFrom(modelFilePath.toString());

      ctx = whisper_init_from_file(cModelPath);

      if (ctx == null || ctx.equals(MemorySegment.NULL)) {
        throw new IllegalStateException("Failed to initialize Whisper context. Check if model is valid.");
      }

      logger.info("Whisper initialized successfully with model: {}", MODEL_PATH);
    } catch (Throwable t) {
      logger.error("Failed to initialize Whisper provider", t);
      throw new RuntimeException(t);
    }
  }

  public TranscriptionResult transcribe(float[] pcmData, String language, boolean needTranslation) {
    if (ctx == null || ctx.equals(MemorySegment.NULL)) {
      throw new TranscriptionException("Whisper engine is not initialized");
    }

    try (Arena arena = Arena.ofConfined()) {
      MemorySegment params = whisper_full_default_params(arena, STRATEGY_GREEDY);

      if (language != null && !language.equalsIgnoreCase("auto")) {
        MemorySegment lan = arena.allocateFrom(language.toLowerCase());
        whisper_full_params.language(params, lan);
      }
      whisper_full_params.n_threads(params, Math.max(1, Runtime.getRuntime().availableProcessors() - 1));
      whisper_full_params.token_timestamps(params, true);
      whisper_full_params.split_on_word(params, true);
      MemorySegment audioBuffer = arena.allocateFrom(ValueLayout.JAVA_FLOAT, pcmData);

      whisper_full_params.translate(params, false);
      if (whisper_full(ctx, params, audioBuffer, pcmData.length) != 0) {
        throw new TranscriptionException("Original transcription failed");
      }

      List<TranscriptionSegment> originalSegments = extractSegments();
      String originalText = assembleText(originalSegments);

      List<TranscriptionSegment> translatedSegments = null;
      String translatedText = null;

      if (needTranslation) {
        whisper_full_params.translate(params, true);
        if (whisper_full(ctx, params, audioBuffer, pcmData.length) == 0) {
          translatedSegments = extractSegments();
          translatedText = assembleText(translatedSegments);
        }
      }

      return new TranscriptionResult(originalSegments, originalText,translatedSegments, translatedText);
    }
  }

  private List<TranscriptionSegment> extractSegments() {
    int nSegments = whisper_full_n_segments(ctx);
    List<TranscriptionSegment> list = new ArrayList<>(nSegments);
    for (int i = 0; i < nSegments; i++) {
      String text = whisper_full_get_segment_text(ctx, i).getString(0).trim();
      long t0 = whisper_full_get_segment_t0(ctx, i) * 10;
      long t1 = whisper_full_get_segment_t1(ctx, i) * 10;
      float silence = whisper_full_get_segment_no_speech_prob(ctx, i);
      float conf = countConfidence(i);

      list.add(new TranscriptionSegment(text, t0, t1, conf, silence));
    }
    return list;
  }

  private String assembleText(List<TranscriptionSegment> segments) {
    return segments.stream()
            .map(TranscriptionSegment::text)
            .collect(Collectors.joining(" "));
  }

  @Override
  public String providerName() {
    return "Whisper";
  }

  private float countConfidence (int i){
    float sumProb = 0;
    int nTokens = whisper_full_n_tokens(ctx, i);

    for (int j = 0; j < nTokens; j++) {
      sumProb += whisper_full_get_token_p(ctx, i, j);
    }

    return (nTokens > 0) ? (sumProb / nTokens) : 0.0f;
  }

  @PreDestroy
  void shutdown() {
    try {
      if (ctx != null && !ctx.equals(MemorySegment.NULL)) {
        whisper_free(ctx);
        logger.info("Whisper native context freed");
      }
      if (serviceArena != null) {
        serviceArena.close();
        logger.info("Whisper service arena closed");
      }
    } catch (Exception e) {
      logger.error("Error during Whisper shutdown", e);
    }
  }
}