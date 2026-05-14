package by.mashnyuk.intelligenceservice.service.analyzers;

import by.mashnyuk.intelligenceservice.model.metrics.NlpDocument;
import by.mashnyuk.intelligenceservice.service.impl.NlpProcessor;
import by.mashnyuk.intelligenceservice.util.LexicalDensityCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NlpAnalysisService {

  private final NlpProcessor nlp;


  public NlpDocument process(String text) {
    String cleanedText = cleanText(text);

    if (cleanedText.isEmpty()) {
      return createEmptyDocument();
    }

    String[] sentences = nlp.sentences(cleanedText);
    String[] tokens = nlp.tokens(cleanedText);
    String[] pos = nlp.pos(tokens);
    String[] lemmas = nlp.lemmas(tokens, pos);

    String[] normalizedPos = normalizePosTags(pos);

    List<String> nounPhrases = extractNounPhrases(tokens, normalizedPos, lemmas);

    log.debug("Processed {} sentences, {} tokens, {} noun phrases",
            sentences.length, tokens.length, nounPhrases.size());
    log.debug("First 10 POS tags: {}",
            Arrays.stream(normalizedPos).limit(10).collect(Collectors.toList()));
    log.debug("Sample noun phrases: {}",
            nounPhrases.stream().limit(5).collect(Collectors.toList()));

    return NlpDocument.builder()
            .sentences(Arrays.asList(sentences))
            .tokens(Arrays.asList(tokens))
            .lemmas(Arrays.asList(lemmas))
            .posTags(Arrays.asList(normalizedPos))
            .nounPhrases(nounPhrases)
            .build();
  }

  private List<String> extractNounPhrases(
          String[] tokens,
          String[] pos,
          String[] lemmas
  ) {
    List<String> phrases = new ArrayList<>();
    StringBuilder current = new StringBuilder();
    int wordCount = 0;

    for (int i = 0; i < tokens.length; i++) {

      if (pos[i] != null && pos[i].equals("PUNCT")) {
        if (!current.isEmpty() && wordCount > 1) {
          String phrase = current.toString().trim();
          if (phrase.length() > 2) {
            phrases.add(phrase);
          }
        }
        current.setLength(0);
        wordCount = 0;
        continue;
      }

      boolean isNounLike = pos[i] != null && (
              pos[i].startsWith("NN") ||
                      pos[i].startsWith("JJ") ||
                      pos[i].equals("DT") ||
                      pos[i].equals("PRP$")
      );

      if (isNounLike) {
        String word = "O".equals(lemmas[i]) ?
                tokens[i].toLowerCase() :
                lemmas[i].toLowerCase();

        if (!isFunctionWord(word)) {
          current.append(word).append(" ");
          wordCount++;
        }
      } else {
        if (!current.isEmpty() && wordCount > 1) {
          String phrase = current.toString().trim();
          if (phrase.length() > 2 && phrase.split("\\s+").length > 1) {
            phrases.add(phrase);
          }
        }
        current.setLength(0);
        wordCount = 0;
      }
    }

    if (!current.isEmpty() && wordCount > 1) {
      String phrase = current.toString().trim();
      if (phrase.length() > 2 && phrase.split("\\s+").length > 1) {
        phrases.add(phrase);
      }
    }

    log.debug("Extracted {} noun phrases: {}", phrases.size(),
            phrases.stream().limit(5).collect(Collectors.toList()));

    return phrases;
  }

  private boolean isFunctionWord(String word) {
    Set<String> functionWords = Set.of(
            "the", "a", "an", "this", "that", "these", "those",
            "my", "your", "his", "her", "its", "our", "their",
            "some", "any", "no", "every", "each", "all", "both"
    );
    return functionWords.contains(word.toLowerCase());
  }

  private String cleanText(String text) {
    if (text == null) return "";

    return text
            .replace("\\\"", "\"")
            .replaceAll("\\s+", " ")
            .replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", "")
            .trim();
  }

  private String[] normalizePosTags(String[] pos) {
    log.debug("Original POS tags sample: {}",
            Arrays.stream(pos).limit(10).collect(Collectors.toList()));

    String[] normalized = new String[pos.length];
    for (int i = 0; i < pos.length; i++) {
      normalized[i] = normalizePosTag(pos[i]);
    }

    log.debug("Normalized POS tags sample: {}",
            Arrays.stream(normalized).limit(10).collect(Collectors.toList()));

    long contentWords = Arrays.stream(normalized)
            .filter(tag -> tag != null && (tag.startsWith("NN") || tag.startsWith("VB") || tag.startsWith("JJ")))
            .count();
    log.debug("Content words found: {} out of {}", contentWords, normalized.length);

    return normalized;
  }

  private String normalizePosTag(String tag) {
    if (tag == null) return "NN";

    String upperTag = tag.toUpperCase().trim();

    if (upperTag.equals("PUNCT") || upperTag.equals("PUNCTUATION") ||
            upperTag.equals("SYM") || upperTag.equals("X") || upperTag.equals(".")) {
      return "PUNCT";
    }

    if (upperTag.contains("+")) {

      String[] parts = upperTag.split("\\+");

      for (String part : parts) {
        String normalized = normalizePosTag(part);
        if (!normalized.equals("PUNCT")) {
          return normalized;
        }
      }
      return "NN";
    }

    return switch (upperTag) {
      case "NOUN", "N" -> "NN";
      case "NOUNS", "NNS" -> "NNS";
      case "PROPN", "NP", "NNP" -> "NNP";
      case "PROPNS", "NPS", "NNPS" -> "NNPS";

      case "VERB", "V", "VB" -> "VB";
      case "VERBS", "VS", "VBZ" -> "VBZ";
      case "VBD", "PAST" -> "VBD";
      case "VBG", "GERUND", "PARTICIPLE" -> "VBG";
      case "VBN", "PAST_PARTICIPLE" -> "VBN";
      case "VBP", "PRESENT" -> "VBP";
      case "AUX" -> "VB";

      case "ADJ", "J", "JJ" -> "JJ";
      case "ADJS", "JJS" -> "JJS";
      case "JJR", "COMPARATIVE" -> "JJR";

      case "ADV", "R", "RB" -> "RB";
      case "RBR", "SUPERLATIVE" -> "RBR";
      case "RBS" -> "RBS";

      case "PRON", "PRO", "PRP" -> "PRP";
      case "PRP$", "POSSESSIVE" -> "PRP$";

      case "ADP", "PREP", "IN" -> "IN";

      case "CONJ", "CCONJ", "CC" -> "CC";
      case "SCONJ", "SUBORDINATING" -> "SCONJ";

      case "DET", "DT" -> "DT";
      case "PDT", "PREDETERMINER" -> "PDT";

      case "PART", "RP" -> "RP";

      case "NUM", "CD" -> "CD";

      case "INTJ", "UH" -> "UH";

      default -> {
        if (upperTag.startsWith("N")) yield "NN";
        if (upperTag.startsWith("V")) yield "VB";
        if (upperTag.startsWith("J") || upperTag.startsWith("ADJ")) yield "JJ";
        if (upperTag.startsWith("R") || upperTag.startsWith("ADV")) yield "RB";
        if (upperTag.startsWith("P")) yield "PRP";
        yield "NN";
      }
    };
  }

  private NlpDocument createEmptyDocument() {
    return NlpDocument.builder()
            .sentences(Collections.emptyList())
            .tokens(Collections.emptyList())
            .lemmas(Collections.emptyList())
            .posTags(Collections.emptyList())
            .nounPhrases(Collections.emptyList())
            .build();
  }

  public void testPosTags(String text) {
    String[] tokens = nlp.tokens(text);
    String[] pos = nlp.pos(tokens);

    log.info("=== POS Tag Test ===");
    for (int i = 0; i < Math.min(20, tokens.length); i++) {
      String normalized = normalizePosTag(pos[i]);
      boolean isContent = LexicalDensityCalculator.CONTENT_TAGS.contains(normalized);
      log.info("Token: '{}' | Original POS: '{}' | Normalized: '{}' | Content: {}",
              tokens[i], pos[i], normalized, isContent);
    }
  }
}
