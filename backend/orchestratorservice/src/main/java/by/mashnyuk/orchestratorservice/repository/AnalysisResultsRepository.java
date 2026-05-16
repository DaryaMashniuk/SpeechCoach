package by.mashnyuk.orchestratorservice.repository;

import by.mashnyuk.orchestratorservice.model.AnalysisResults;
import by.mashnyuk.orchestratorservice.model.AnalysisStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnalysisResultsRepository extends JpaRepository<AnalysisResults, Long> {
  @Query("SELECT r FROM AnalysisResults r " +
          "JOIN AnalysisJobs j ON r.analysisJobId = j.id " +
          "WHERE j.userId = :userId " +
          "AND j.analysisStatus = :status " +
          "ORDER BY r.createdAt ASC")
  List<AnalysisResults> findAllByUserIdAndStatus(
          @Param("userId") Long userId,
          @Param("status") AnalysisStatus status
  );

  Optional<AnalysisResults> findByAnalysisJobId(UUID jobId);
}
