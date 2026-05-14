package by.mashnyuk.intelligenceservice.service.analyzers;

import by.mashnyuk.intelligenceservice.model.FillerDictionary;
import by.mashnyuk.intelligenceservice.model.Language;
import by.mashnyuk.intelligenceservice.model.metrics.LexicalMetrics;
import by.mashnyuk.intelligenceservice.model.metrics.NlpDocument;
import by.mashnyuk.intelligenceservice.util.LexicalDensityCalculator;
import by.mashnyuk.intelligenceservice.util.SemanticSimilarityCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class LexicalAnalyzer {

  private final NlpAnalysisService nlpService;

  public LexicalMetrics analyze(String text, String langCode, double wpm) {

    NlpDocument doc =
            nlpService.process(text);
    List<String> sentences = doc.getSentences();
    Set<String> fillers = FillerDictionary.getForLanguage(Language.valueOf(langCode));
    List<String> detectedFillers =
            doc.getTokens()
                    .stream()
                    .map(String::toLowerCase)
                    .filter(fillers::contains)
                    .toList();

    double density = LexicalDensityCalculator.calculate(doc);

    long unique =
            doc.getTokens()
                    .stream()
                    .map(String::toLowerCase)
                    .filter(token -> token.length() > 1)
                    .distinct()
                    .count();

    long totalMeaningfulTokens = doc.getTokens()
            .stream()
            .filter(token -> token.length() > 1)
            .count();

    double lexicalVariety = totalMeaningfulTokens > 0 ?
            (double) unique / totalMeaningfulTokens : 0;

    int repetitionsCount = countRepetitions(doc);
    double avgSentenceLength = sentences.isEmpty() ? 0 :
            (double) doc.getTokens().size() / sentences.size();

    log.info("Lexical analysis: density={}, variety={}, repetitions={}, avgSentLen={}",
            density, lexicalVariety, repetitionsCount, avgSentenceLength);
    return LexicalMetrics.builder()
            .totalWords(doc.getTokens().size())
            .wpm(wpm)
            .fillerCount(detectedFillers.size())
            .fillerWords(detectedFillers)
            .lexicalVariety(lexicalVariety)
            .lexicalDensity(density)
            .repetitionsCount(repetitionsCount)
            .avgSentenceLength(avgSentenceLength)
            .repetitionsCount(repetitionsCount)
            .avgSentenceLength(avgSentenceLength)
            .build();
  }

  private int countRepetitions(NlpDocument doc) {
    List<String> tokens = doc.getTokens();
    int repetitions = 0;

    Map<String, Integer> bigrams = new HashMap<>();
    for (int i = 0; i < tokens.size() - 1; i++) {
      String bigram = tokens.get(i).toLowerCase() + " " + tokens.get(i + 1).toLowerCase();
      bigrams.merge(bigram, 1, Integer::sum);
    }

    repetitions = (int) bigrams.values().stream()
            .filter(count -> count > 2)
            .count();

    return repetitions;
  }
}
