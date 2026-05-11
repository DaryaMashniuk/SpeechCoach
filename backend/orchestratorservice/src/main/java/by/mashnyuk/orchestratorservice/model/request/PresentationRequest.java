package by.mashnyuk.orchestratorservice.model.request;


import by.mashnyuk.orchestratorservice.model.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PresentationRequest {

  private Language language;

  private Long userId;

  private String title;

  private String description;

  private MultipartFile file;

  private boolean training = Boolean.TRUE;

}
