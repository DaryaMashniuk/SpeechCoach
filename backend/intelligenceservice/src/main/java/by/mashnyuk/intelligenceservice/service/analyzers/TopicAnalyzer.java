package by.mashnyuk.intelligenceservice.service.analyzers;

import by.mashnyuk.intelligenceservice.model.metrics.NlpDocument;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TopicAnalyzer {

  private static final int MAX_TOPICS = 8;

  private static final int MIN_PHRASE_LENGTH = 3;

  public List<String> extractTopics(
          NlpDocument doc
  ) {

    Map<String, Double> scores =
            new HashMap<>();

    List<String> phrases =
            doc.getNounPhrases();

    for (int i = 0; i < phrases.size(); i++) {

      String phrase =
              phrases.get(i)
                      .toLowerCase();

      if (phrase.length()
              < MIN_PHRASE_LENGTH) {

        continue;
      }

      double score =
              calculateScore(
                      phrase,
                      i,
                      phrases.size()
              );

      scores.merge(
              phrase,
              score,
              Double::sum
      );
    }

    return scores.entrySet()
            .stream()
            .sorted((a, b) ->
                    Double.compare(
                            b.getValue(),
                            a.getValue()
                    ))
            .limit(MAX_TOPICS)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
  }

  private double calculateScore(
          String phrase,
          int position,
          int total
  ) {

    double phraseLengthWeight =
            Math.min(
                    phrase.split("\\s+").length * 0.35,
                    2.0
            );

    double positionWeight =
            1.0 -
                    ((double) position / total);

    return 1.0
            + phraseLengthWeight
            + positionWeight;
  }
}