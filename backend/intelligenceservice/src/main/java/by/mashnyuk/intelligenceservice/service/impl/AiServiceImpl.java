package by.mashnyuk.intelligenceservice.service.impl;

import by.mashnyuk.intelligenceservice.model.metrics.BehavioralMetrics;
import by.mashnyuk.intelligenceservice.model.metrics.LexicalMetrics;
import by.mashnyuk.intelligenceservice.model.metrics.PitchDynamicsMetrics;
import by.mashnyuk.intelligenceservice.model.metrics.ProsodyMetrics;
import by.mashnyuk.intelligenceservice.model.metrics.RhythmMetrics;
import by.mashnyuk.intelligenceservice.model.metrics.StructureMetrics;
import by.mashnyuk.intelligenceservice.service.AiService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

import by.mashnyuk.intelligenceservice.model.*;
import by.mashnyuk.intelligenceservice.model.dto.request.IntelligenceAnalyzeRequest;


@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

  private final OllamaChatModel chatModel;
  private static final Logger log = LogManager.getLogger();

  @Override
  public String generateCoachingFeedback(
          IntelligenceAnalyzeRequest request,
          LexicalMetrics lexical,
          ProsodyMetrics prosody,
          RhythmMetrics rhythm,
          PitchDynamicsMetrics pitch,
          BehavioralMetrics behavior,
          StructureMetrics structure,
          OverallScore score
  ) {

    String prompt = buildPrompt(
            request,
            lexical,
            prosody,
            rhythm,
            pitch,
            behavior,
            structure,
            score
    );

    return chatModel.call(prompt);
    //return "";
  }

  private String buildPrompt(
          IntelligenceAnalyzeRequest request,
          LexicalMetrics lexical,
          ProsodyMetrics prosody,
          RhythmMetrics rhythm,
          PitchDynamicsMetrics pitch,
          BehavioralMetrics behavior,
          StructureMetrics structure,
          OverallScore score
  ) {

    log.info("""
                Ты профессиональный speech coach.

                Проанализируй выступление.

                Язык выступления: %s

                === Лексика ===
                - Темп речи: %.1f WPM
                - Слова-паразиты: %d
                - Лексическое разнообразие: %.2f
                - Лексическая плотность: %.2f
                - Повторы мыслей: %d

                === Голос и ритм ===
                - Диапазон интонации: %.2f
                - Монотонность: %s
                - Стабильность ритма: %.2f
                - Нервозность: %.2f
                - Уверенность: %.2f

                === Структура ===
                - Связность речи: %.2f
                - Аргументация: %.2f
                - Переходы между мыслями: %.2f
                - Скачки между темами: %d
                - Есть вступление: %s
                - Есть заключение: %s

                === Общая оценка ===
                - Итоговый score: %.1f

                Текст выступления:
                %s

                Задача:
                1. Проанализируй качество выступления.
                2. Объясни слабые места.
                3. Объясни что звучит хорошо.
                4. Дай рекомендации по улучшению структуры.
                5. Дай рекомендации по уверенности и подаче.
                6. НЕ повторяй метрики буквально.
                7. Отвечай как живой профессиональный coach.

                Формат:
                - Краткий общий вывод
                - 3-5 рекомендаций
                - Позитивное завершение
                """.formatted(
            request.language(),

            lexical.getWpm(),
            lexical.getFillerCount(),
            lexical.getLexicalVariety(),
            lexical.getLexicalDensity(),
            lexical.getRepetitionsCount(),

            pitch.getPitchRange(),
            pitch.isMonotone(),
            rhythm.getRhythmStability(),
            behavior.getNervousnessScore(),
            behavior.getConfidenceScore(),

            structure.getCoherenceScore(),
            structure.getArgumentationScore(),
            structure.getTransitionScore(),
            structure.getTopicJumps(),
            structure.isHasIntroduction(),
            structure.isHasConclusion(),

            score.getOverall(),

            request.transcriptText()
    ));
    String structureNote = structure.isSupportedByHeuristics()
            ? "Данные структуры рассчитаны на основе лингвистических маркеров."
            : "Внимание: Детальный структурный анализ для этого языка недоступен. Пожалуйста, проанализируй структуру текста самостоятельно на основе семантики.";

    return """
                Ты профессиональный speech coach.

                Проанализируй выступление.

                Язык выступления: %s

                === Заметки анализатора ===
                    %s
                === Лексика ===
                - Темп речи: %.1f WPM
                - Слова-паразиты: %d
                - Лексическое разнообразие: %.2f
                - Лексическая плотность: %.2f
                - Повторы мыслей: %d

                === Голос и ритм ===
                - Диапазон интонации: %.2f
                - Монотонность: %s
                - Стабильность ритма: %.2f
                - Нервозность: %.2f
                - Уверенность: %.2f

                === Структура ===
                - Связность речи: %.2f
                - Аргументация: %.2f
                - Переходы между мыслями: %.2f
                - Скачки между темами: %d
                - Есть вступление: %s
                - Есть заключение: %s

                === Общая оценка ===
                - Итоговый score: %.1f

                Текст выступления:
                %s

                Задача:
                1. Проанализируй качество выступления.
                2. Объясни слабые места.
                3. Объясни что звучит хорошо.
                4. Дай рекомендации по улучшению структуры.
                5. Дай рекомендации по уверенности и подаче.
                6. НЕ повторяй метрики буквально.
                7. Отвечай как живой профессиональный coach.

                Формат:
                - Краткий общий вывод
                - 3-5 рекомендаций
                - Позитивное завершение
                """.formatted(
            request.language(),
            structureNote,
            lexical.getWpm(),
            lexical.getFillerCount(),
            lexical.getLexicalVariety(),
            lexical.getLexicalDensity(),
            lexical.getRepetitionsCount(),

            pitch.getPitchRange(),
            pitch.isMonotone(),
            rhythm.getRhythmStability(),
            behavior.getNervousnessScore(),
            behavior.getConfidenceScore(),

            structure.getCoherenceScore(),
            structure.getArgumentationScore(),
            structure.getTransitionScore(),
            structure.getTopicJumps(),
            structure.isHasIntroduction(),
            structure.isHasConclusion(),

            score.getOverall(),

            request.transcriptText()
    );
  }
}
