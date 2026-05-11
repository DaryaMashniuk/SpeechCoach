package by.mashnyuk.intelligenceservice.util;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class SemanticSimilarityCalculator {

  public double similarity(String a, String b) {

    Set<String> set1 = tokenize(a);
    Set<String> set2 = tokenize(b);

    if (set1.isEmpty() || set2.isEmpty()) {
      return 0;
    }

    Set<String> intersection = new HashSet<>(set1);
    intersection.retainAll(set2);

    Set<String> union = new HashSet<>(set1);
    union.addAll(set2);

    return (double) intersection.size() / union.size();
  }

  private Set<String> tokenize(String text) {

    return Arrays.stream(
                    text.toLowerCase()
                            .replaceAll("[^a-zа-яё\\s]", "")
                            .split("\\s+"))
            .filter(w -> w.length() > 2)
            .collect(Collectors.toSet());
  }
}
