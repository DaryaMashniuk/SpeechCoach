package by.mashnyuk.intelligenceservice.util;

import by.mashnyuk.intelligenceservice.model.metrics.NlpDocument;
import lombok.experimental.UtilityClass;

import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class SemanticSimilarityCalculator {

  public double similarity(
          NlpDocument a,
          NlpDocument b
  ) {

    Set<String> set1 =
            extractContentWords(a);

    Set<String> set2 =
            extractContentWords(b);

    if (set1.isEmpty() || set2.isEmpty()) {
      return 0;
    }

    Set<String> intersection =
            new HashSet<>(set1);

    intersection.retainAll(set2);

    Set<String> union =
            new HashSet<>(set1);

    union.addAll(set2);

    return (double)
            intersection.size()
            / union.size();
  }

  private Set<String> extractContentWords(
          NlpDocument doc
  ) {

    Set<String> result =
            new HashSet<>();

    for (int i = 0; i < doc.getTokens().size(); i++) {

      String tag =
              doc.getPosTags().get(i);

      if (
              tag.startsWith("NN")
                      || tag.startsWith("VB")
                      || tag.startsWith("JJ")
      ) {

        result.add(
                doc.getTokens()
                        .get(i)
                        .toLowerCase()
        );
      }
    }

    return result;
  }
}