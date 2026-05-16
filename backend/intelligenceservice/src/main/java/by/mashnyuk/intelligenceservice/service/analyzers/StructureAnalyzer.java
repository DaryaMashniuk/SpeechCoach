package by.mashnyuk.intelligenceservice.service.analyzers;

import by.mashnyuk.intelligenceservice.model.metrics.NlpDocument;
import by.mashnyuk.intelligenceservice.model.metrics.StructureMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StructureAnalyzer {

  private static final double TOPIC_JUMP_THRESHOLD = 0.18;
  private static final double ENTITY_WEIGHT = 0.5;
  private static final double SUBJECT_WEIGHT = 0.3;
  private static final double PRONOUN_WEIGHT = 0.2;
  private static final int INTRO_WINDOW = 3;
  private static final int CONCLUSION_WINDOW = 3;

  private final NlpAnalysisService nlpService;
  private final TopicAnalyzer topicAnalyzer;

  public StructureMetrics analyze(String text) {
    NlpDocument doc = nlpService.process(text);
    List<String> sentences = doc.getSentences();

    if (sentences.isEmpty()) {
      return empty();
    }

    boolean intro = detectIntroduction(sentences);
    boolean conclusion = detectConclusion(sentences);

    Map<String, Double> globalWordWeights = calculateGlobalWordWeights(doc);

    double coherence = calculateCoherence(sentences, doc, globalWordWeights);
    int topicJumps = detectTopicJumps(sentences);

    double consistency;
    if (sentences.size() < 3) {
      consistency = 1.0;
    } else {
      double jumpRatio = (double) topicJumps / (sentences.size() - 1);
      consistency = Math.max(0, 1 - Math.log1p(jumpRatio * 5) / Math.log1p(5));

      log.debug("Consistency: {} (jumps={}, ratio={})", consistency, topicJumps, jumpRatio);
    }
    List<String> transitions = detectTransitions(sentences);
    double transitionScore = Math.min(1, transitions.size() / 5.0);
    double argumentation = calculateArgumentation(sentences);
    List<String> topics = topicAnalyzer.extractTopics(doc);

    log.debug("Structure analysis: coherence={}, topicJumps={}, transitions={}",
            coherence, topicJumps, transitions.size());

    return StructureMetrics.builder()
            .hasIntroduction(intro)
            .hasConclusion(conclusion)
            .supportedByHeuristics(true)
            .coherenceScore(coherence)
            .transitionScore(transitionScore)
            .argumentationScore(argumentation)
            .topicConsistencyScore(consistency)
            .topicJumps(topicJumps)
            .detectedTransitions(transitions)
            .detectedTopics(topics)
            .missingParts(buildMissing(intro, conclusion))
            .build();
  }


  private Map<String, Double> calculateGlobalWordWeights(NlpDocument fullDoc) {
    Map<String, Double> weights = new HashMap<>();
    List<String> sentences = fullDoc.getSentences();

    if (sentences.isEmpty()) {
      return weights;
    }

    Map<String, Integer> wordSentenceCount = new HashMap<>();

    for (String sentence : sentences) {
      Set<String> uniqueWords = extractSignificantWords(sentence);
      for (String word : uniqueWords) {
        wordSentenceCount.merge(word, 1, Integer::sum);
      }
    }

    int totalSentences = sentences.size();
    for (Map.Entry<String, Integer> entry : wordSentenceCount.entrySet()) {
      double idf = Math.log((double) totalSentences / entry.getValue());
      weights.put(entry.getKey(), idf);
    }

    log.debug("Calculated global weights for {} unique significant words", weights.size());
    return weights;
  }

  private double calculateCoherence(
          List<String> sentences,
          NlpDocument fullDoc,
          Map<String, Double> globalWeights) {

    if (sentences.size() < 2) {
      return 1.0;
    }

    double total = 0;
    int comparisons = 0;

    List<String> globalTopics = topicAnalyzer.extractTopics(fullDoc);
    double globalTopicBonus = globalTopics.size() > 3 ? 0.15 : 0.0;

    for (int i = 0; i < sentences.size() - 1; i++) {
      double coherence = calculateSentencePairCoherence(
              sentences.get(i),
              sentences.get(i + 1),
              fullDoc,
              globalWeights,
              i,
              sentences.size()
      );

      int topicsInFirst = countTopicMatches(sentences.get(i), globalTopics);
      int topicsInSecond = countTopicMatches(sentences.get(i + 1), globalTopics);

      if (topicsInFirst > 0 && topicsInSecond > 0) {
        coherence += globalTopicBonus * Math.min(topicsInFirst, topicsInSecond);
      }

      total += Math.min(1.0, coherence);
      comparisons++;

      log.trace("Coherence between sentence {} and {}: {:.4f} (topics: {}/{})",
              i, i+1, coherence, topicsInFirst, topicsInSecond);
    }

    double average = comparisons > 0 ? total / comparisons : 1.0;

    double dialogueAdjustment = detectDialoguePattern(sentences) ? 0.1 : 0;

    double adjustedAverage = Math.min(1.0, average + dialogueAdjustment);

    log.info("Coherence: {:.4f} (raw: {:.4f}, dialogue boost: {:.4f})",
            adjustedAverage, average, dialogueAdjustment);

    return adjustedAverage;
  }

  private boolean detectDialoguePattern(List<String> sentences) {
    int questionCount = 0;
    int shortSentenceCount = 0;

    for (String sentence : sentences) {
      String trimmed = sentence.trim();
      if (trimmed.endsWith("?")) {
        questionCount++;
      }
      if (trimmed.split("\\s+").length < 5) {
        shortSentenceCount++;
      }
    }

    double questionRatio = (double) questionCount / sentences.size();
    double shortRatio = (double) shortSentenceCount / sentences.size();

    boolean isDialogue = questionRatio > 0.1 || shortRatio > 0.3;
    log.debug("Dialogue detection: questions={}, short={}, isDialogue={}",
            questionCount, shortSentenceCount, isDialogue);

    return isDialogue;
  }

  private double calculateSentencePairCoherence(
          String first,
          String second,
          NlpDocument fullDoc,
          Map<String, Double> globalWeights,
          int position,
          int totalSentences) {

    Set<String> wordsFirst = extractSignificantWords(first);
    Set<String> wordsSecond = extractSignificantWords(second);

    if (wordsFirst.isEmpty() || wordsSecond.isEmpty()) {
      return 0.5;
    }

    double weightedSimilarity = calculateWeightedSimilarity(
            wordsFirst, wordsSecond, globalWeights);

    double specificityScore = calculateSpecificityBoost(
            wordsFirst, wordsSecond, globalWeights, fullDoc);

    double transitionBoost = hasDiscourseMarker(second) ? 0.2 :
            hasDiscourseMarker(first) ? 0.1 : 0;

    double pronounScore = calculatePronounResolution(first, second);

    double positionBonus = 0.1;

    double topicalCoherence = calculateTopicalCoherence(
            first, second, fullDoc);

    double coherence =
            weightedSimilarity * 0.35 +
                    specificityScore * 0.15 +
                    pronounScore * 0.20 +
                    transitionBoost * 0.10 +
                    topicalCoherence * 0.15 +
                    positionBonus * 0.05;

    return Math.min(1.0, coherence);
  }

  private double calculateWeightedSimilarity(
          Set<String> wordsFirst,
          Set<String> wordsSecond,
          Map<String, Double> globalWeights) {

    Set<String> intersection = new HashSet<>(wordsFirst);
    intersection.retainAll(wordsSecond);

    if (intersection.isEmpty()) {
      return 0.0;
    }

    double weightedIntersection = intersection.stream()
            .mapToDouble(word -> {
              double weight = globalWeights.getOrDefault(word, 1.0);
              return weight;
            })
            .sum();

    double normalization = Math.min(wordsFirst.size(), wordsSecond.size());

    return Math.min(1.0, weightedIntersection / normalization);
  }

  private double calculateSpecificityBoost(
          Set<String> wordsFirst,
          Set<String> wordsSecond,
          Map<String, Double> globalWeights,
          NlpDocument fullDoc) {

    Set<String> intersection = new HashSet<>(wordsFirst);
    intersection.retainAll(wordsSecond);

    if (intersection.isEmpty()) {
      return 0.0;
    }

    double avgSpecificity = intersection.stream()
            .mapToDouble(word -> globalWeights.getOrDefault(word, 0.0))
            .average()
            .orElse(0.0);

    return Math.min(1.0, avgSpecificity / 3.0);
  }

  private double calculateTopicalCoherence(
          String first,
          String second,
          NlpDocument fullDoc) {

    List<String> globalTopics = topicAnalyzer.extractTopics(fullDoc);

    if (globalTopics.isEmpty()) {
      return 0.5;
    }

    int firstTopicMatches = countTopicMatches(first, globalTopics);
    int secondTopicMatches = countTopicMatches(second, globalTopics);

    if (firstTopicMatches > 0 && secondTopicMatches > 0) {
      return Math.min(1.0, (firstTopicMatches + secondTopicMatches) /
              (2.0 * Math.max(1, globalTopics.size())));
    }

    return firstTopicMatches == secondTopicMatches ? 0.7 : 0.3;
  }

  private int countTopicMatches(String sentence, List<String> topics) {
    String lower = sentence.toLowerCase();
    return (int) topics.stream()
            .filter(topic -> lower.contains(topic.toLowerCase()))
            .count();
  }

  private Set<String> extractSignificantWords(String sentence) {
    NlpDocument doc = nlpService.process(sentence);
    Set<String> significant = new HashSet<>();

    for (int i = 0; i < doc.getPosTags().size(); i++) {
      String tag = doc.getPosTags().get(i);
      if (tag != null && (tag.startsWith("NN") || tag.startsWith("VB") ||
              tag.startsWith("JJ") || tag.startsWith("RB"))) {
        String lemma = doc.getLemmas().get(i);
        if (!lemma.equals("O") && lemma.length() > 1) {
          significant.add(lemma.toLowerCase());
        }
      }
    }

    return significant;
  }

  private boolean hasDiscourseMarker(String sentence) {
    Set<String> markers = Set.of(
            "however", "therefore", "moreover", "furthermore", "consequently",
            "nevertheless", "meanwhile", "accordingly", "thus", "hence",
            "although", "because", "while", "whereas", "despite"
    );

    String lower = sentence.toLowerCase();
    return markers.stream().anyMatch(lower::contains);
  }

  private double calculatePronounResolution(String first, String second) {
    Set<String> thirdPersonPronouns = Set.of("he", "she", "it", "they", "them");
    Set<String> demonstratives = Set.of("this", "that", "these", "those");

    boolean secondHasPronoun = thirdPersonPronouns.stream()
            .anyMatch(p -> second.toLowerCase().matches(".*\\b" + p + "\\b.*")) ||
            demonstratives.stream()
                    .anyMatch(d -> second.toLowerCase().matches(".*\\b" + d + "\\b.*"));

    if (!secondHasPronoun) {
      return 0.5;
    }

    NlpDocument firstDoc = nlpService.process(first);
    long nounCount = firstDoc.getPosTags().stream()
            .filter(tag -> tag != null && tag.startsWith("NN"))
            .count();

    boolean hasPluralNoun = firstDoc.getPosTags().stream()
            .anyMatch(tag -> "NNS".equals(tag) || "NNPS".equals(tag));

    boolean hasPluralPronoun = second.toLowerCase().matches(".*\\b(they|them|these|those)\\b.*");

    double numberAgreementBonus = (hasPluralNoun == hasPluralPronoun) ? 0.2 : 0;

    return Math.min(1.0, (nounCount > 0 ? 0.6 : 0.2) + numberAgreementBonus);
  }

  private int detectTopicJumps(List<String> sentences) {
    if (sentences.size() < 2) {
      return 0;
    }

    double adjustedThreshold = 0.20;

    String fullText = String.join(" ", sentences);
    NlpDocument fullDoc = nlpService.process(fullText);
    Map<String, Double> globalWeights = calculateGlobalWordWeights(fullDoc);

    int jumps = 0;
    double totalCoherence = 0;
    List<Double> coherenceScores = new ArrayList<>();

    for (int i = 0; i < sentences.size() - 1; i++) {
      double coherence = calculateSentencePairCoherence(
              sentences.get(i),
              sentences.get(i + 1),
              fullDoc,
              globalWeights,
              i,
              sentences.size()
      );

      totalCoherence += coherence;
      coherenceScores.add(coherence);

      if (coherence < adjustedThreshold) {
        jumps++;
        log.trace("Topic jump at sentence {} -> {}: coherence={:.4f}",
                i, i+1, coherence);
      }
    }

    double avgCoherence = coherenceScores.stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0.5);

    double medianCoherence = coherenceScores.stream()
            .sorted()
            .skip(coherenceScores.size() / 2)
            .findFirst()
            .orElse(0.5);

    long lowCoherenceCount = coherenceScores.stream()
            .filter(c -> c < 0.3)
            .count();


    if (lowCoherenceCount > sentences.size() * 0.6) {
      jumps = (int) (jumps * 0.5);
      log.info("Detected conversational style, reducing topic jumps by 50%");
    }

    log.info("Topic jumps: {} (adjusted from raw count), avg coherence: {:.4f}, median: {:.4f}, threshold: {}",
            jumps, avgCoherence, medianCoherence, adjustedThreshold);

    return Math.min(jumps, sentences.size() / 3);
  }
  private boolean detectIntroduction(
          List<String> sentences
  ) {

    Set<String> introWords =
            Set.of(
                    "today",
                    "welcome",
                    "topic",
                    "presentation",
                    "discuss"
            );

    int limit =
            Math.min(
                    INTRO_WINDOW,
                    sentences.size()
            );

    for (int i = 0; i < limit; i++) {

      String lower =
              sentences.get(i)
                      .toLowerCase();

      if (introWords.stream()
              .anyMatch(lower::contains)) {

        return true;
      }
    }

    return false;
  }

  private boolean detectConclusion(
          List<String> sentences
  ) {

    Set<String> conclusionWords =
            Set.of(
                    "finally",
                    "overall",
                    "in conclusion",
                    "thank you",
                    "to summarize"
            );

    int start =
            Math.max(
                    0,
                    sentences.size()
                            - CONCLUSION_WINDOW
            );

    for (int i = start;
         i < sentences.size();
         i++) {

      String lower =
              sentences.get(i)
                      .toLowerCase();

      if (conclusionWords.stream()
              .anyMatch(lower::contains)) {

        return true;
      }
    }

    return false;
  }

  private List<String> detectTransitions(
          List<String> sentences
  ) {

    Set<String> markers =
            Set.of(
                    "however",
                    "therefore",
                    "for example",
                    "moreover",
                    "finally",
                    "because",
                    "thus"
            );

    Set<String> found =
            new HashSet<>();

    for (String sentence : sentences) {

      String lower =
              sentence.toLowerCase();

      markers.stream()
              .filter(lower::contains)
              .forEach(found::add);
    }

    return found.stream().toList();
  }

  private double calculateArgumentation(
          List<String> sentences
  ) {

    int claims = 0;
    int evidence = 0;
    int conclusions = 0;

    for (String sentence : sentences) {

      String lower =
              sentence.toLowerCase();

      if (lower.contains("i think")
              || lower.contains("i believe")) {

        claims++;
      }

      if (lower.contains("because")
              || lower.contains("for example")) {

        evidence++;
      }

      if (lower.contains("therefore")
              || lower.contains("overall")) {

        conclusions++;
      }
    }

    double raw =
            claims * 0.3
                    + evidence * 0.5
                    + conclusions * 0.2;

    return Math.min(1, raw / 5.0);
  }


  private List<String> buildMissing(
          boolean intro,
          boolean conclusion
  ) {

    List<String> missing =
            new ArrayList<>();

    if (!intro) {
      missing.add("INTRODUCTION");
    }

    if (!conclusion) {
      missing.add("CONCLUSION");
    }

    return missing;
  }

  private StructureMetrics empty() {

    return StructureMetrics.builder()
            .hasIntroduction(false)
            .hasConclusion(false)
            .supportedByHeuristics(false)
            .coherenceScore(0)
            .transitionScore(0)
            .argumentationScore(0)
            .topicConsistencyScore(0)
            .topicJumps(0)
            .detectedTransitions(
                    Collections.emptyList()
            )
            .detectedTopics(
                    Collections.emptyList()
            )
            .missingParts(
                    Collections.emptyList()
            )
            .build();
  }
}