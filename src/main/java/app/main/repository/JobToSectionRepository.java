package app.main.repository;

import app.main.entity.JobToSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobToSectionRepository extends JpaRepository<JobToSection, Long> {

}
