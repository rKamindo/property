package kamindo.propertymanager.repository;

import kamindo.propertymanager.dto.PropertyDTO;
import kamindo.propertymanager.dto.UnitDTO;
import kamindo.propertymanager.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    @Query("SELECT new " +
    "kamindo.propertymanager.dto.PropertyDTO(p.id, p.address, p.propertyType, COUNT(u.id)) " +
    "FROM Property p " +
    "LEFT JOIN p.units u " +
    "WHERE p.propertyOwner = :owner " +
            "GROUP BY p.id")
    List<PropertyDTO> getPropertiesForUser(@Param("owner")String owner);

    @Query("SELECT new " +
    "kamindo.propertymanager.dto.UnitDTO(u.id, u.unitNumber, p.id)" +
    "FROM Property p " +
    "LEFT JOIN p.units u " +
    "WHERE p.id = :propertyId ")
    List<UnitDTO> getUnitsByProperty(@Param("propertyId") Long propertyId);
}
