package kamindo.propertymanager.repository;

import kamindo.propertymanager.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property, Long> {
}
