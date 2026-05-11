package by.mashnyuk.intelligenceservice.service.impl;

import by.mashnyuk.intelligenceservice.model.metrics.BehavioralMetrics;
import by.mashnyuk.intelligenceservice.model.metrics.LexicalMetrics;
import by.mashnyuk.intelligenceservice.model.OverallScore;
import by.mashnyuk.intelligenceservice.model.metrics.PitchDynamicsMetrics;
import by.mashnyuk.intelligenceservice.model.metrics.ProsodyMetrics;
import by.mashnyuk.intelligenceservice.model.metrics.RhythmMetrics;
import by.mashnyuk.intelligenceservice.model.metrics.StructureMetrics;
import by.mashnyuk.intelligenceservice.model.dto.request.IntelligenceAnalyzeRequest;
import by.mashnyuk.intelligenceservice.model.dto.response.IntelligenceAnalyzeResponse;
import by.mashnyuk.intelligenceservice.service.AiService;
import by.mashnyuk.intelligenceservice.service.IntelligenceAnalysisService;
import by.mashnyuk.intelligenceservice.service.analyzers.BehaviorAnalyzer;
import by.mashnyuk.intelligenceservice.service.analyzers.LexicalAnalyzer;
import by.mashnyuk.intelligenceservice.service.analyzers.PitchDynamicsAnalyzer;
import by.mashnyuk.intelligenceservice.service.analyzers.ProsodyAnalyzer;
import by.mashnyuk.intelligenceservice.service.analyzers.RhythmAnalyzer;
import by.mashnyuk.intelligenceservice.service.analyzers.ScoreEngine;
import by.mashnyuk.intelligenceservice.service.analyzers.StructureAnalyzer;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IntelligenceAnalysisServiceImpl implements IntelligenceAnalysisService {

  private final LexicalAnalyzer lexicalAnalyzer;
  private final ProsodyAnalyzer prosodyAnalyzer;
  private final RhythmAnalyzer rhythmAnalyzer;
  private final PitchDynamicsAnalyzer pitchDynamicsAnalyzer;
  private final BehaviorAnalyzer behaviorAnalyzer;
  private final StructureAnalyzer structureAnalyzer;
  private final ScoreEngine scoreEngine;
  private final AiService aiService;

  @Override
  public IntelligenceAnalyzeResponse fullAnalysis(IntelligenceAnalyzeRequest request) {

    String[] words = request.transcriptText().trim().split("\\s+");
    int wordCount = (request.transcriptText().isEmpty()) ? 0 : words.length;
    double durationSec = request.audioMetrics().durationMs();

    double wpm = (durationSec > 0) ? (wordCount / durationSec) * 60 : 0;
    LexicalMetrics lexical =
            lexicalAnalyzer.analyze(
                    request.transcriptText(),
                    request.language(),
                    wpm
            );

    ProsodyMetrics prosody =
            prosodyAnalyzer.analyze(
                    request.audioMetrics()
            );

    RhythmMetrics rhythm =
            rhythmAnalyzer.analyze(
                    request.audioMetrics().timeline()
            );

    PitchDynamicsMetrics pitch =
            pitchDynamicsAnalyzer.analyze(
                    request.audioMetrics()
            );

    BehavioralMetrics behavior =
            behaviorAnalyzer.analyze(
                    lexical,
                    prosody
//                    , rhythm,
//                    pitch
            );


    StructureMetrics structure =
            structureAnalyzer.analyze(
                    request.transcriptText(),
                    request.language()
            );

    OverallScore score =
            scoreEngine.calculate(
                    lexical,
                    prosody,
//                    rhythm,
//                    pitch,
                    behavior
//                    structure
            );

    String aiFeedback =
            aiService.generateCoachingFeedback(
                    request,
                    lexical,
                    prosody,
                    rhythm,
                    pitch,
                    behavior,
                    structure,
                    score
            );

    return IntelligenceAnalyzeResponse.builder()
            .presentationId(request.presentationId())
            .scoreLogic(score.getOverall())
            .scoreClarity(score.getClarity())
            .scoreConfidence(score.getDelivery())
            .tips(List.of(aiFeedback.split("\n")))
            .build();
  }


}
