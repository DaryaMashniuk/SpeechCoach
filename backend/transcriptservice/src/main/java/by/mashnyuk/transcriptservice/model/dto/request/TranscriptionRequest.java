package by.mashnyuk.transcriptservice.model.dto.request;

import by.mashnyuk.transcriptservice.model.Language;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class TranscriptionRequest {

  private MultipartFile file;

  private Language language;

  private String meetingContext;

}
