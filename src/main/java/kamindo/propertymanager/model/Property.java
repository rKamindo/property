package kamindo.propertymanager.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "units")
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String address;
    @Enumerated(EnumType.STRING)
    private PropertyType propertyType;
    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "property")
    @Builder.Default
    private Set<Unit> units = new HashSet<>();
    private String propertyOwner; // unique identifier from auth0

    public void addUnit(Unit unit) {
        units.add(unit);
    }
}

