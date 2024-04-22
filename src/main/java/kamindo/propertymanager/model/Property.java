package kamindo.propertymanager.model;

import jakarta.persistence.*;
import kamindo.propertymanager.exception.BadRequestException;
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
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Unit> units;
    private String propertyOwner; // unique identifier from auth0

    public void addUnit(Unit unit) {
        if (units == null) {
            units = new ArrayList<>();
        }
        if (!this.units.isEmpty() && this.getPropertyType() == PropertyType.SINGLE_UNIT) {
            throw new BadRequestException("Single unit property cannot have more than one unit");
        }
        units.add(unit);
    }
}

