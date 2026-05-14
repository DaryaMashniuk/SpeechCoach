package by.mashnyuk.intelligenceservice.service.impl;

import lombok.RequiredArgsConstructor;
import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.tokenize.TokenizerME;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NlpProcessor {

  private final SentenceDetectorME sentenceDetector;
  private final TokenizerME tokenizer;
  private final POSTaggerME posTagger;
  private final LemmatizerME lemmatizer;

  public String[] sentences(String text) {

    return sentenceDetector.sentDetect(text);
  }

  public String[] tokens(String text) {

    return tokenizer.tokenize(text);
  }

  public String[] pos(String[] tokens) {

    return posTagger.tag(tokens);
  }

  public String[] lemmas(
          String[] tokens,
          String[] pos
  ) {

    return lemmatizer.lemmatize(tokens, pos);
  }
}