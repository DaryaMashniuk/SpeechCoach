package by.mashnyuk.orchestratorservice.repository;

import by.mashnyuk.orchestratorservice.model.Presentation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PresentationRepository extends JpaRepository<Presentation,Long> {
}
