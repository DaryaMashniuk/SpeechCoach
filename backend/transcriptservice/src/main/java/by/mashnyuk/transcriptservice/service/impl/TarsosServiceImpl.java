package by.mashnyuk.transcriptservice.service.impl;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.SilenceDetector;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetector;
import be.tarsos.dsp.pitch.PitchProcessor;
import by.mashnyuk.transcriptservice.exceptions.AudioProcessingException;
import by.mashnyuk.transcriptservice.model.AudioAnalysisReport;
import by.mashnyuk.transcriptservice.model.PitchesData;
import by.mashnyuk.transcriptservice.model.VolumeData;
import by.mashnyuk.transcriptservice.service.DigitalSignalProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.util.ArrayList;
import java.util.List;

@Component
public class TarsosServiceImpl implements DigitalSignalProcessor{

  private static final Logger log = LogManager.getLogger();
  private static final float PROBABILITY = 0.8f;
  private static final int SAMPLE_RATE = 44100;
  private static final int BUFFER_SIZE = 512;
  private static final int OVERLAP = 0;
  private static final double THRESHOLD = -70.0F;


  public AudioAnalysisReport analyze(float[] audioData){
    List<PitchesData> pitches = new ArrayList<>();
    List<VolumeData> volumes = new ArrayList<>();

    try {
      AudioDispatcher dispatcher = AudioDispatcherFactory
              .fromFloatArray(audioData,SAMPLE_RATE,BUFFER_SIZE,OVERLAP);

      dispatcher.addAudioProcessor(getPitchProcessor(pitches));
      dispatcher.addAudioProcessor(getEnergyProcessor(volumes));
      dispatcher.addAudioProcessor(getSilenceProcessor());
      dispatcher.run();

    } catch (UnsupportedAudioFileException e) {
      throw new AudioProcessingException("Something went wrong");
    }


    return AudioAnalysisReport.builder()
            .pitches(pitches)
            .volumes(volumes)
            .build();
  }

  private AudioProcessor getPitchProcessor(List<PitchesData> pitchList) {
    PitchDetectionHandler handler = (res,event) -> {
      float pitch = res.getPitch();
      float probability = res.getProbability();

      if (probability > PROBABILITY){
        pitchList.add(new PitchesData(event.getTimeStamp(),pitch));
        log.info("Pitch at:" + event.getTimeStamp() + ", " + pitch + "pitch" + event.getdBSPL() + " dB SPL\n");
        //TODO Standard Deviation
      }
    };
    return new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.YIN,SAMPLE_RATE,BUFFER_SIZE,handler);
  }

  private AudioProcessor getSilenceProcessor() {
    SilenceDetector silenceDetector = new SilenceDetector(THRESHOLD,false);
    return new AudioProcessor() {
      @Override
      public boolean process(AudioEvent audioEvent) {
        if (silenceDetector.isSilence(audioEvent.getFloatBuffer(),THRESHOLD)){
          log.info("Silence detected at:" + audioEvent.getTimeStamp() + ", " + silenceDetector.currentSPL() + " dB SPL\n");
        }

        return true;
      }

      @Override
      public void processingFinished() {
        log.info("Silence processing finished");
      }
    };
  }

  private AudioProcessor getEnergyProcessor(List<VolumeData> volumeList) {
    return new AudioProcessor() {

      @Override
      public boolean process(AudioEvent audioEvent) {
        double rms = audioEvent.getRMS();
        double db = (rms > 0) ? 20 * Math.log10(rms) : -100;
        volumeList.add(new VolumeData(audioEvent.getTimeStamp(),db));
        return true;
      }

      @Override
      public void processingFinished() {

      }
    };

  }

}
