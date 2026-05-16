package by.mashnyuk.intelligenceservice.service.impl;

import by.mashnyuk.intelligenceservice.model.metrics.BehavioralMetrics;
import by.mashnyuk.intelligenceservice.model.metrics.LexicalMetrics;
import by.mashnyuk.intelligenceservice.model.OverallScore;
import by.mashnyuk.intelligenceservice.model.metrics.ProsodyMetrics;
import by.mashnyuk.intelligenceservice.model.metrics.StructureMetrics;
import by.mashnyuk.intelligenceservice.model.dto.request.IntelligenceAnalyzeRequest;
import by.mashnyuk.intelligenceservice.model.dto.response.IntelligenceAnalyzeResponse;
import by.mashnyuk.intelligenceservice.service.AiService;
import by.mashnyuk.intelligenceservice.service.IntelligenceAnalysisService;
import by.mashnyuk.intelligenceservice.service.analyzers.BehaviorAnalyzer;
import by.mashnyuk.intelligenceservice.service.analyzers.LexicalAnalyzer;
import by.mashnyuk.intelligenceservice.service.analyzers.ProsodyAnalyzer;
import by.mashnyuk.intelligenceservice.service.analyzers.ScoreEngine;
import by.mashnyuk.intelligenceservice.service.analyzers.StructureAnalyzer;
import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IntelligenceAnalysisServiceImpl implements IntelligenceAnalysisService {

  private final LexicalAnalyzer lexicalAnalyzer;
  private final ProsodyAnalyzer prosodyAnalyzer;
  private final BehaviorAnalyzer behaviorAnalyzer;
  private final StructureAnalyzer structureAnalyzer;
  private final ScoreEngine scoreEngine;
  private final AiService aiService;
  private static final Logger log = LogManager.getLogger();

  @Override
  public IntelligenceAnalyzeResponse fullAnalysis(IntelligenceAnalyzeRequest request) {

    String[] words = request.transcriptText().trim().split("\\s+");
    int wordCount = (request.transcriptText().isEmpty()) ? 0 : words.length;
    double duration = request.audioMetrics().durationMs();

    double wpm = (duration > 0) ? (wordCount / duration) * 60 : 0;
    LexicalMetrics lexical =
            lexicalAnalyzer.analyze(
                    request.transcriptText(),
                    request.language(),
                    wpm
            );
    log.info(lexical.toString());

    ProsodyMetrics prosody =
            prosodyAnalyzer.analyze(
                    request.audioMetrics()
            );
    log.info(prosody.toString());

    BehavioralMetrics behavior =
            behaviorAnalyzer.analyze(
                    lexical,
                    prosody
            );
    log.info(behavior.toString());

    StructureMetrics structure =
            structureAnalyzer.analyze(
                    request.transcriptText()
//                    ,request.language()
            );
    log.info(structure.toString());

    OverallScore score =
            scoreEngine.calculate(
                    lexical,
                    prosody,
                    behavior,
                    structure
            );

    String aiFeedback =
            aiService.generateCoachingFeedback(
                    request,
                    lexical,
                    prosody,
                    behavior,
                    structure,
                    score
            );

    return IntelligenceAnalyzeResponse.builder()
            .presentationId(request.presentationId())
            .scoreLogic(score.getClarity())
            .scoreClarity(lexical.getLexicalDensity())
            .scoreConfidence(score.getDelivery())
            .overallScore(score.getOverall())

            .lexical(lexical)
            .prosody(prosody)
            .behavior(behavior)
            .structure(structure)

            .transcriptSegments(request.transcriptSegments())
            .audioMetrics(request.audioMetrics())

            .tips(List.of(aiFeedback.split("\n")))
            .transcript(request.transcriptText())
            .title(request.title())
            .build();
  }

  @Override
  public String summary(String text,String language) {
    return aiService.generateSummary(text,language);
  }
}
