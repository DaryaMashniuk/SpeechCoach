package by.mashnyuk.orchestratorservice.repository;

import by.mashnyuk.orchestratorservice.model.AnalysisResults;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalysisResultsRepository extends JpaRepository<AnalysisResults, Long> {
}
