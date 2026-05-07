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
import java.util.concurrent.CompletableFuture;

@Component
public class AudioConverter {

  @Value("${ffmpeg-path}")
  private String ffmpegPath;

  public float[] convertToWhisperFormat(MultipartFile multipartFile) throws IOException {
    // Команда:
    // -i pipe:0  : читать входные данные из стандартного ввода (stdin)
    // -ar 16000  : частота 16кГц
    // -ac 1      : моно
    // -f f32le   : формат 32-bit float
    // pipe:1     : писать результат в стандартный вывод (stdout)
    System.out.println(ffmpegPath);
    File ffmpegFile = new File(ffmpegPath);

    if (!ffmpegPath.equals("ffmpeg") && !ffmpegFile.exists()) {
      throw new FileNotFoundException("FFmpeg not found on path: " + ffmpegPath);
    }
    ProcessBuilder pb = new ProcessBuilder(
            ffmpegPath,
            "-i", "pipe:0",
            "-ar", "16000",
            "-ac", "1",
            "-f", "f32le",
            "-loglevel", "error",
            "pipe:1"
    );

    Process process = pb.start();

    CompletableFuture<Void> writeTask = CompletableFuture.runAsync(() -> {
      try (OutputStream os = process.getOutputStream()) {
        os.write(multipartFile.getBytes());
        os.flush();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    float[] pcmData;
    try (BufferedInputStream bis = new BufferedInputStream(process.getInputStream())) {
      byte[] allBytes = bis.readAllBytes();
      pcmData = bytesToFloats(allBytes);
    }

    try {
      writeTask.join();
      int exitCode = process.waitFor();
      if (exitCode != 0) {
        throw new IOException("FFmpeg завершился с ошибкой: " + exitCode);
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("Процесс конвертации был прерван", e);
    }

    return pcmData;
  }

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
}