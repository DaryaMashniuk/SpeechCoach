package by.mashnyuk.intelligenceservice.service.analyzers;

import by.mashnyuk.intelligenceservice.model.FillerDictionary;
import by.mashnyuk.intelligenceservice.model.Language;
import by.mashnyuk.intelligenceservice.model.metrics.LexicalMetrics;
import by.mashnyuk.intelligenceservice.util.LexicalDensityCalculator;
import by.mashnyuk.intelligenceservice.util.SemanticSimilarityCalculator;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class LexicalAnalyzer {

  public LexicalMetrics analyze(String text, String langCode, double wpm) {
    String[] words = text.toLowerCase().replaceAll("[^a-zа-яё\\s]", "").split("\\s+");


    Set<String> dictionary = FillerDictionary.getForLanguage(Language.valueOf(langCode));
    List<String> fillers = Arrays.stream(words).filter(dictionary::contains).toList();

    double density = LexicalDensityCalculator.calculate(words);

    String[] sentences = text.split("[.!?]+");
    int semanticReps = 0;
    for (int i = 0; i < sentences.length - 1; i++) {
      if (SemanticSimilarityCalculator.similarity(sentences[i], sentences[i+1]) > 0.7) {
        semanticReps++;
      }
    }

    return LexicalMetrics.builder()
            .totalWords(words.length)
            .wpm(wpm)
            .fillerCount(fillers.size())
            .fillerWords(fillers.stream().distinct().toList())
            .lexicalVariety((double) Arrays.stream(words).distinct().count() / words.length)
            .lexicalDensity(density)
            .repetitionsCount(semanticReps)
            .build();
  }
}
