package by.mashnyuk.intelligenceservice.controller;

import by.mashnyuk.intelligenceservice.model.dto.request.IntelligenceAnalyzeRequest;
import by.mashnyuk.intelligenceservice.model.dto.response.IntelligenceAnalyzeResponse;
import by.mashnyuk.intelligenceservice.service.IntelligenceAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/analysis/api/v1")
@RequiredArgsConstructor
public class AnalysisController {

  private final OllamaChatModel chatModel;
  private final IntelligenceAnalysisService analysisService;


  @PostMapping("/report")
  public IntelligenceAnalyzeResponse analyze(@RequestBody IntelligenceAnalyzeRequest request) {

    return analysisService.fullAnalysis(request);
  }

  @PostMapping("/summary")
  public String summarize(@RequestBody String text) {
    //TODO write logic for this option
    return "Краткое содержание встречи...";
  }
}
