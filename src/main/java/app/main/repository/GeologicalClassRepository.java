package app.main.repository;

import app.main.entity.GeologicalClass;
import app.main.entity.Section;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface GeologicalClassRepository extends JpaRepository<GeologicalClass, Long> {

    List<GeologicalClass> findGeologicalClassBySection(Section section);

    GeologicalClass getGeologicalClassById(Long id);

}
