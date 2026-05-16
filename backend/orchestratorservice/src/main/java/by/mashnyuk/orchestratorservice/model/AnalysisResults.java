package by.mashnyuk.orchestratorservice.model;

import by.mashnyuk.orchestratorservice.model.request.AudioMetricsDto;
import by.mashnyuk.orchestratorservice.model.request.TranscriptionSegment;
import by.mashnyuk.orchestratorservice.model.response.IntelligenceAnalyzeResponse;
import by.mashnyuk.orchestratorservice.model.response.MeetingTranscriptionResult;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "analysis_results")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResults extends Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="analysis_job_id",unique = true, nullable = false)
  private UUID analysisJobId;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "full_report", columnDefinition = "jsonb")
  private IntelligenceAnalyzeResponse fullReport;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "transcription_report", columnDefinition = "jsonb")
  private MeetingTranscriptionResult meetingTranscriptionResult;

  private String modelVersion;

  private String translatedText;
}