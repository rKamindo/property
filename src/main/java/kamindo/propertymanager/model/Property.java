package kamindo.propertymanager.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
    private List<Unit> units;
    private String propertyOwner; // unique identifier from auth0

    public void addUnit(Unit unit) {
        if (units == null) {
            units = new ArrayList<>();
            units.add(unit);
        } else {
            units.add(unit);
        }
    }
}

