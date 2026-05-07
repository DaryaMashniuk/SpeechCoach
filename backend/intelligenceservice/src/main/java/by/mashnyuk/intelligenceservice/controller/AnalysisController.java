package by.mashnyuk.intelligenceservice.controller;

import by.mashnyuk.intelligenceservice.model.dto.request.IntelligenceAnalyzeRequest;
import by.mashnyuk.intelligenceservice.model.dto.response.IntelligenceAnalyzeResponse;
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

  @GetMapping("/generate")
  public Map<String,String> generate(@RequestParam(value = "message") String message) {
    return Map.of("generation", this.chatModel.call(message));
  }

  @PostMapping("/report")
  public IntelligenceAnalyzeResponse analyze(@RequestBody IntelligenceAnalyzeRequest request) {
    //TODO: fill in report data
    return null;
  }
}
