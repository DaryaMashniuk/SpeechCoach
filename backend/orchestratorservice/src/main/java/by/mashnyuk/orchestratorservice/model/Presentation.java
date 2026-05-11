package by.mashnyuk.orchestratorservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "audios"
)
public class Presentation extends Auditable{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id",nullable = false)
  private Long userId;

  private String title;

  private String description;

  @Column(name="duration_ms",nullable = false)
  private Long durationMs;

  @Column(name="source_file_id",nullable = false)
  private String sourceFileId;

  private boolean training = Boolean.TRUE;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Language language;
}
