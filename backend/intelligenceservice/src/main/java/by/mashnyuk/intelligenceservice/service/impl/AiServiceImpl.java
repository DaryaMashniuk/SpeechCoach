package by.mashnyuk.intelligenceservice.service.impl;

import by.mashnyuk.intelligenceservice.model.metrics.BehavioralMetrics;
import by.mashnyuk.intelligenceservice.model.metrics.LexicalMetrics;
import by.mashnyuk.intelligenceservice.model.metrics.ProsodyMetrics;
import by.mashnyuk.intelligenceservice.model.metrics.StructureMetrics;
import by.mashnyuk.intelligenceservice.service.AiService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

import by.mashnyuk.intelligenceservice.model.*;
import by.mashnyuk.intelligenceservice.model.dto.request.IntelligenceAnalyzeRequest;


@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

  private final OllamaChatModel chatModel;
  private static final Logger log = LogManager.getLogger();

  @Override
  public String generateCoachingFeedback(
          IntelligenceAnalyzeRequest request,
          LexicalMetrics lexical,
          ProsodyMetrics prosody,
          BehavioralMetrics behavior,
          StructureMetrics structure,
          OverallScore score
  ) {

    String prompt = buildPrompt(
            request,
            lexical,
            prosody,
            behavior,
            structure,
            score
    );

    return chatModel.call(prompt);
    //return "";
  }

  @Override
  public String generateSummary(String transcript,String language) {

    String prompt = buildGeneralSummaryPrompt(transcript,language);

    try {
      return chatModel.call(prompt);
    } catch (Exception e) {
      log.error("Summary generation failed", e);
      return "Не удалось создать краткое описание.";
    }
  }

  private String buildPrompt(
          IntelligenceAnalyzeRequest request,
          LexicalMetrics lexical,
          ProsodyMetrics prosody,
          BehavioralMetrics behavior,
          StructureMetrics structure,
          OverallScore score
  ) {
    String structureNote = structure.isSupportedByHeuristics()
            ? "Structural signals are based on detected linguistic markers."
            : "Detailed heuristic structure analysis is unavailable for this language; infer structure from the transcript itself.";

    String language = request.language();
    String audioLanguageName = switch (language.toLowerCase()) {
      case "ru" -> "Russian";
      case "en" -> "English";
      case "de" -> "German";
      default -> language;
    };

    return """
You are a professional speech coach.

IMPORTANT RULES:
- Reply strictly in %s.
- Do not mix languages.
- Do not repeat metrics verbatim.
- Use the metrics as evidence, but explain them in natural language.
- Be specific, confident, and constructive.
- Focus on the speaker's delivery, structure, clarity, and confidence.

SPEECH CONTEXT:
- Language: %s

ANALYSIS SIGNALS:
- Lexical:
  - WPM: %.1f
  - Filler words: %d
  - Lexical variety: %.2f
  - Lexical density: %.2f
  - Repetitions: %d
- Voice and rhythm:
  - Pitch range: %.2f
  - Monotone: %s
  - Rhythm stability: %.2f
  - Nervousness: %.2f
  - Confidence: %.2f
- Structure:
  - Coherence: %.2f
  - Argumentation: %.2f
  - Transitions: %.2f
  - Topic jumps: %d
  - Has introduction: %s
  - Has conclusion: %s
- Overall score: %.1f

STRUCTURE NOTE:
%s

TRANSCRIPT:
%s

TASK:
1. Give a short overall verdict.
2. Explain the strongest points.
3. Explain the weakest points.
4. Give 3-5 actionable improvements.
5. End with an encouraging note.

OUTPUT FORMAT:
**Verdict**
...one short paragraph...

**What works well**
- ...
- ...

**What to improve**
- ...
- ...

**Recommendations**
1. ...
2. ...
3. ...

**Closing**
...short encouraging ending...
""".formatted(
            audioLanguageName,
            audioLanguageName,
            lexical.getWpm(),
            lexical.getFillerCount(),
            lexical.getLexicalVariety(),
            lexical.getLexicalDensity(),
            lexical.getRepetitionsCount(),
            prosody.getPitchRange(),
            prosody.isMonotone(),
            prosody.getRhythmStability(),
            behavior.getNervousnessScore(),
            behavior.getConfidenceScore(),
            structure.getCoherenceScore(),
            structure.getArgumentationScore(),
            structure.getTransitionScore(),
            structure.getTopicJumps(),
            structure.isHasIntroduction(),
            structure.isHasConclusion(),
            score.getOverall(),
            structureNote,
            request.transcriptText()
    );
  }

  public String buildGeneralSummaryPrompt(String transcript, String language) {
    String outputLanguage = switch (language.toLowerCase()) {
      case "ru" -> "Russian";
      case "en" -> "English";
      case "de" -> "German";
      default -> language;
    };

    return """
You are a concise summarization assistant.

IMPORTANT RULES:
- Reply strictly in %s.
- Do not mix languages.
- Summarize only the meaningful content.
- Ignore filler words, repetitions, false starts, and transcription noise.
- Do not quote the transcript.
- Do not add information that is not present in the text.
- Keep the answer short, clear, and natural.

TRANSCRIPT:
%s

TASK:
1. State the main topic in one sentence.
2. List 3-5 key ideas or facts.
3. Describe the overall tone or context in 1 short sentence.

OUTPUT FORMAT:
**Main topic:** ...
**Key ideas:**
- ...
- ...
- ...
**Tone / context:** ...
""".formatted(outputLanguage, transcript);
  }
}
