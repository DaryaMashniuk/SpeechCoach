package by.mashnyuk.orchestratorservice.repository;

import by.mashnyuk.orchestratorservice.model.AnalysisStatus;
import by.mashnyuk.orchestratorservice.model.Presentation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PresentationRepository extends JpaRepository<Presentation,Long> {
  Page<Presentation> findAllByUserId(Long userId, Pageable pageable);

  Page<Presentation> findAllByUserIdAndTraining(Long userId, boolean training, Pageable pageable);

}
