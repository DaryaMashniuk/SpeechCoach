package by.mashnyuk.orchestratorservice.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.MultimediaInfo;


@Component
public class AudioConverter {

  @Value("${ffmpeg-path}")
  private String ffmpegPath;

  private float[] bytesToFloats(byte[] bytes) {
    if (bytes.length % 4 != 0) {
      return new float[0];
    }

    float[] floats = new float[bytes.length / 4];
    ByteBuffer.wrap(bytes)
            .order(ByteOrder.LITTLE_ENDIAN)
            .asFloatBuffer()
            .get(floats);
    return floats;
  }

  public long getDuration(MultipartFile file) {
    try {

      Path tempFile = Files.createTempFile("upload-", file.getOriginalFilename());
      file.transferTo(tempFile);

      MultimediaObject instance = new MultimediaObject(tempFile.toFile());
      MultimediaInfo info = instance.getInfo();

      long durationMs = info.getDuration();

      Files.delete(tempFile);
      return durationMs;
    } catch (Exception e) {
      throw new RuntimeException("Ошибка при определении длины аудио", e);
    }
  }
}