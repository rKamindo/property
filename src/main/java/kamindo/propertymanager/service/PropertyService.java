package kamindo.propertymanager.service;

import kamindo.propertymanager.exception.BadRequestException;
import kamindo.propertymanager.model.Property;
import kamindo.propertymanager.model.PropertyType;
import kamindo.propertymanager.model.Unit;
import kamindo.propertymanager.repository.PropertyRepository;
import kamindo.propertymanager.request.CreatePropertyRequest;
import kamindo.propertymanager.request.CreateUnitRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PropertyService {
    private final PropertyRepository propertyRepository;

    public Long createProperty(CreatePropertyRequest request, String owner) {
        if (request.units().isEmpty()) {
            throw new BadRequestException("Property must have at least one unit");
        }

        if (request.propertyType() == PropertyType.SINGLE_UNIT &&
        request.units().size() > 1) {
            throw new BadRequestException("Single unit property cannot have more than one unit");
        }

        Set<String> unitNumbers = new HashSet<>();
        for (CreateUnitRequest unitRequest: request.units()) {
            if (unitNumbers.contains(unitRequest.unitNumber())) {
                throw new BadRequestException("Property unit numbers must be unique");
            }
            unitNumbers.add(unitRequest.unitNumber());
        }

        Property property = Property.builder()
                .address(request.address())
                .propertyType(request.propertyType())
                .propertyOwner(owner)
                .build();

        request.units().stream().map(unitRequest -> Unit
                .builder()
                .unitNumber(unitRequest.unitNumber())
                .build())
                .toList()
                .forEach(unit -> {
                    unit.setProperty(property);
                    property.addUnit(unit);
                });

        Property newProperty = propertyRepository.save(property);
        return newProperty.getId();
    }
}
