package by.mashnyuk.intelligenceservice.controller;

import by.mashnyuk.intelligenceservice.model.dto.request.IntelligenceAnalyzeRequest;
import by.mashnyuk.intelligenceservice.model.dto.response.IntelligenceAnalyzeResponse;
import by.mashnyuk.intelligenceservice.model.dto.response.MeetingTranscriptionResult;
import by.mashnyuk.intelligenceservice.service.IntelligenceAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analysis/")
@RequiredArgsConstructor
public class AnalysisController {

  private final IntelligenceAnalysisService analysisService;


  @PostMapping("/report")
  public IntelligenceAnalyzeResponse analyze(@RequestBody IntelligenceAnalyzeRequest request) {

    return analysisService.fullAnalysis(request);
  }

  @PostMapping("/summary")
  public MeetingTranscriptionResult summarize(@RequestBody String text) {
    //TODO write logic for this option
    return null;
  }
}
