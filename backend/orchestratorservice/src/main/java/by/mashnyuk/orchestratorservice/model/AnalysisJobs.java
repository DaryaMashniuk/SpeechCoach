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

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "analysis_jobs")
public class AnalysisJobs extends Auditable{

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "audio_id",nullable = false)
  private Long audioId;

  @Enumerated(EnumType.STRING)
  @Column(name = "analysis_status",nullable = false)
  private AnalysisStatus analysisStatus;

  private Long userId;

  @Enumerated(EnumType.STRING)
  @Column(name = "analysis_type",nullable = false)
  private AnalysisType analysisType;

  @Column(name = "error_message", columnDefinition = "TEXT")
  private String errorMessage;

  @Builder.Default
  private Integer retryCount = 0;

  private LocalDateTime startedAt;
  private LocalDateTime finishedAt;
}
