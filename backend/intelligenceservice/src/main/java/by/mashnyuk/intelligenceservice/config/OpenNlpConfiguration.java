package by.mashnyuk.intelligenceservice.config;


import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class OpenNlpConfiguration {

  @Bean
  public SentenceDetectorME sentenceDetector() throws IOException {

    try (InputStream is = load("opennlp-en-ud-ewt-sentence-1.3-2.5.4.bin")) {

      return new SentenceDetectorME(
              new SentenceModel(is)
      );
    }
  }

  @Bean
  public TokenizerME tokenizer() throws IOException {

    try (InputStream is = load("opennlp-en-ud-ewt-tokens-1.3-2.5.4.bin")) {

      return new TokenizerME(
              new TokenizerModel(is)
      );
    }
  }

  @Bean
  public POSTaggerME posTagger() throws IOException {

    try (InputStream is = load("opennlp-en-ud-ewt-pos-1.3-2.5.4.bin")) {

      return new POSTaggerME(
              new POSModel(is)
      );
    }
  }

  @Bean
  public LemmatizerME lemmatizer() throws IOException {

    try (InputStream is = load("opennlp-en-ud-ewt-lemmas-1.3-2.5.4.bin")) {

      return new LemmatizerME(
              new LemmatizerModel(is)
      );
    }
  }

  private InputStream load(String model) {

    InputStream stream =
            Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(model);

    if (stream == null) {
      throw new RuntimeException(
              "OpenNLP model not found: " + model
      );
    }

    return stream;
  }
}