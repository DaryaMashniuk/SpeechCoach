package by.mashnyuk.orchestratorservice.repository;

import by.mashnyuk.orchestratorservice.model.AnalysisJobs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnalysisJobsRepository extends JpaRepository<AnalysisJobs, UUID> {
  Optional<AnalysisJobs> findById(UUID audioId);
}
