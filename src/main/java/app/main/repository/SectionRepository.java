package app.main.repository;

import app.main.entity.Section;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    Section getSectionsById(Long id);

    @Query(value =
            "SELECT DISTINCT s.* " +
            "FROM section s " +
            "JOIN geological_class gc ON s.id = gc.section_id " +
            "WHERE gc.code = :code", nativeQuery = true)
    List<Section> findAllByGeologicalClasses(String code);

    @Query(value = "SELECT s.* FROM section s" +
            "    INNER JOIN job_to_section jts ON s.id = jts.section_id " +
            "WHERE job_id = :jobId", nativeQuery = true)
    List<Section> findAllByJobType(Long jobId);
}