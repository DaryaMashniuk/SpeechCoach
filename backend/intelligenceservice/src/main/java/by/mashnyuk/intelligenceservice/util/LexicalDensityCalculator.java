package by.mashnyuk.intelligenceservice.util;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Set;

@UtilityClass
public class LexicalDensityCalculator {

  private static final Set<String> STOP_WORDS = Set.of(
          "и", "в", "на", "с", "что", "это",
          "the", "a", "an", "is", "are"
  );

  public double calculate(String[] words) {

    if (words.length == 0) {
      return 0;
    }

    long meaningful =
            Arrays.stream(words)
                    .filter(word -> !STOP_WORDS.contains(word))
                    .count();

    return (double) meaningful / words.length;
  }
}
