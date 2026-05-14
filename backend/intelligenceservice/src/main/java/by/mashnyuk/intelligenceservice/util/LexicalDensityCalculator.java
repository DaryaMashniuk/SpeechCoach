package by.mashnyuk.intelligenceservice.util;

import by.mashnyuk.intelligenceservice.model.metrics.NlpDocument;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;

@Slf4j
@UtilityClass
public class LexicalDensityCalculator {

  public static final Set<String> CONTENT_TAGS = Set.of(
          "NN", "NNS", "NNP", "NNPS",
          "VB", "VBD", "VBG", "VBN", "VBP", "VBZ",
          "JJ", "JJR", "JJS",
          "RB", "RBR", "RBS"
  );

  private static final Set<String> FUNCTION_TAGS = Set.of(
          "PUNCT", "SYM", "X",
          "DT", "PDT",
          "PRP", "PRP$",
          "IN",
          "CC", "SCONJ",
          "RP",
          "UH",
          "TO",
          "CD"
  );

  public double calculate(NlpDocument doc) {
    if (doc == null || doc.getTokens().isEmpty() || doc.getPosTags().isEmpty()) {
      log.warn("Empty document for lexical density calculation");
      return 0.0;
    }

    int totalTokens = doc.getTokens().size();
    int contentWords = 0;
    int punctuationCount = 0;

    log.debug("Calculating lexical density for {} tokens", totalTokens);

    for (int i = 0; i < Math.min(doc.getPosTags().size(), totalTokens); i++) {
      String tag = doc.getPosTags().get(i);
      String token = doc.getTokens().get(i);

      if (tag == null) continue;

      if (tag.equals("PUNCT")) {
        punctuationCount++;
        continue;
      }

      if (CONTENT_TAGS.contains(tag)) {
        contentWords++;
        log.trace("Content word: '{}' ({})", token, tag);
      }
    }

    int meaningfulTokens = totalTokens - punctuationCount;
    double density = meaningfulTokens > 0 ? (double) contentWords / meaningfulTokens : 0.0;

    log.info("Lexical density: {:.4f} ({} content words / {} meaningful tokens, {} punctuation excluded)",
            density, contentWords, meaningfulTokens, punctuationCount);
    if (density == 0.0) {
      return calculateHeuristicDensity(doc.getTokens());
    }
    return density;
  }

  private double calculateHeuristicDensity(List<String> tokens) {
    Set<String> functionWords = Set.of(
            "the", "a", "an", "is", "are", "was", "were", "be", "been", "being",
            "have", "has", "had", "do", "does", "did", "will", "would", "shall",
            "should", "may", "might", "must", "can", "could", "i", "you", "he",
            "she", "it", "we", "they", "me", "him", "her", "us", "them", "my",
            "your", "his", "its", "our", "their", "this", "that", "these", "those",
            "and", "but", "or", "if", "because", "as", "until", "while", "of",
            "at", "by", "for", "with", "about", "to", "from", "in", "on", "off",
            "over", "under", "again", "then", "once", "here", "there", "when",
            "where", "why", "how", "all", "both", "each", "few", "more", "most",
            "other", "some", "such", "no", "nor", "not", "only", "same", "so",
            "than", "too", "very", "s", "t", "just", "don", "now", "up", "down"
    );

    int contentCount = 0;
    for (String token : tokens) {
      String lower = token.toLowerCase().replaceAll("[^a-z]", "");
      if (lower.length() > 1 && !functionWords.contains(lower)) {
        contentCount++;
      }
    }

    double density = tokens.size() > 0 ? (double) contentCount / tokens.size() : 0.0;
    log.info("Heuristic density: {:.2f}", density);
    return density;
  }
}