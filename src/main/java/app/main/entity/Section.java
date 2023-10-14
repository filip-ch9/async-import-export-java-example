package app.main.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    // we might want to remove EAGER if the list is very big
    @OneToMany(mappedBy = "section", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<GeologicalClass> geologicalClasses;

    public Section(String name, List<GeologicalClass> geologicalClasses) {
        this.name = name;
        this.geologicalClasses = geologicalClasses;
    }

    public Section(String name) {
        this.name = name;
    }

}
