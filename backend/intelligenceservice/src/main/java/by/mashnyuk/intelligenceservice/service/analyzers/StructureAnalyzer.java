package by.mashnyuk.intelligenceservice.service.analyzers;

import by.mashnyuk.intelligenceservice.model.metrics.StructureMetrics;
import by.mashnyuk.intelligenceservice.util.SemanticSimilarityCalculator;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StructureAnalyzer {

  private static final Map<String, List<String>> TRANSITIONS =
          Map.of(
                  "ru", List.of("во-первых", "во-вторых", "таким образом", "подводя итог", "например", "далее", "итак"),
                  "en", List.of("firstly", "however", "therefore", "for example", "in conclusion", "finally"),
                  "de", List.of("erstens", "zum beispiel", "abschließend", "deshalb")
          );

  private static final Map<String, List<String>> INTRO_MARKERS =
          Map.of(
                  "ru", List.of("сегодня", "я расскажу", "тема", "приветствую", "здравствуйте"),
                  "en", List.of("today i will", "i want to talk", "topic", "hello", "hi everyone", "welcome")
          );

  private static final Map<String, List<String>> CONCLUSION_MARKERS =
          Map.of(
                  "ru", List.of("подводя итог", "в заключение", "итак", "спасибо за внимание"),
                  "en", List.of("in conclusion", "to summarize", "finally", "thank you for")
          );

  public StructureMetrics analyze(String text, String language) {
    String lower = text.toLowerCase().trim();
    String[] words = lower.split("\\s+");

    boolean isSupported = TRANSITIONS.containsKey(language);

    List<String> sentences = Arrays.stream(text.split("[.!?]+"))
            .map(String::trim)
            .filter(s -> !s.isBlank())
            .toList();

    if (!isSupported) {
      return buildAiReliantStructure(sentences);
    }

    List<String> transitions = detectTransitions(lower, language);

    boolean intro = containsMarker(lower, INTRO_MARKERS.getOrDefault(language, List.of()));
    boolean conclusion = containsMarker(lower, CONCLUSION_MARKERS.getOrDefault(language, List.of()));

    if (words.length < 25 && !intro) {
      intro = checkCommonGreetings(lower);
    }

    double coherence = calculateCoherence(sentences);
    int topicJumps = detectTopicJumps(sentences);
    double argumentation = calculateArgumentation(sentences, language);

    double transitionScore = Math.min(1.0, transitions.size() / 5.0);
    double topicConsistency = Math.max(0, 1 - (topicJumps * 0.15));

    return StructureMetrics.builder()
            .hasIntroduction(intro)
            .hasConclusion(conclusion)
            .supportedByHeuristics(true)
            .coherenceScore(coherence)
            .transitionScore(transitionScore)
            .argumentationScore(argumentation)
            .topicConsistencyScore(topicConsistency)
            .topicJumps(topicJumps)
            .detectedTransitions(transitions)
            .missingParts(buildMissingParts(intro, conclusion, words.length))
            .build();
  }


  private StructureMetrics buildAiReliantStructure(List<String> sentences) {
    return StructureMetrics.builder()
            .supportedByHeuristics(false)
            .hasIntroduction(true)
            .hasConclusion(true)
            .coherenceScore(calculateCoherence(sentences))
            .topicJumps(0)
            .argumentationScore(0.5)
            .missingParts(Collections.emptyList())
            .build();
  }

  private boolean checkCommonGreetings(String text) {
    return text.contains("hello") || text.contains(" hi ") ||
            text.contains("привет") || text.contains("здравствуй") ||
            text.contains("welcome") || text.contains("добрый день");
  }

  private List<String> detectTransitions(String text, String language) {
    return TRANSITIONS.getOrDefault(language, List.of()).stream()
            .filter(text::contains)
            .toList();
  }

  private boolean containsMarker(String text, List<String> markers) {
    return markers.stream().anyMatch(text::contains);
  }

  private double calculateCoherence(List<String> sentences) {
    if (sentences.size() < 2) return 1.0;
    double total = 0;
    for (int i = 0; i < sentences.size() - 1; i++) {
      total += SemanticSimilarityCalculator.similarity(sentences.get(i), sentences.get(i + 1));
    }
    return total / (sentences.size() - 1);
  }

  private int detectTopicJumps(List<String> sentences) {
    int jumps = 0;
    for (int i = 0; i < sentences.size() - 1; i++) {
      if (SemanticSimilarityCalculator.similarity(sentences.get(i), sentences.get(i + 1)) < 0.15) {
        jumps++;
      }
    }
    return jumps;
  }

  private double calculateArgumentation(List<String> sentences, String language) {
    List<String> markers = switch (language) {
      case "ru" -> List.of("потому что", "например", "следовательно", "поэтому", "так как");
      case "de" -> List.of("weil", "deshalb", "zum beispiel", "daher");
      default -> List.of("because", "therefore", "for example", "consequently");
    };

    long count = sentences.stream()
            .map(String::toLowerCase)
            .filter(s -> markers.stream().anyMatch(s::contains))
            .count();

    return Math.min(1.0, (double) count / Math.max(1, sentences.size() / 2.0));
  }

  private List<String> buildMissingParts(boolean intro, boolean conclusion, int wordCount) {
    List<String> missing = new ArrayList<>();

    if (wordCount < 15) return missing;

    if (!intro) missing.add("INTRODUCTION");
    if (!conclusion) missing.add("CONCLUSION");
    return missing;
  }
}