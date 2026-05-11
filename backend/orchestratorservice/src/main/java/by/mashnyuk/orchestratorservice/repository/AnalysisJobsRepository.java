package by.mashnyuk.orchestratorservice.repository;

import by.mashnyuk.orchestratorservice.model.AnalysisJobs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalysisJobsRepository extends JpaRepository<AnalysisJobs, Long> {
}
