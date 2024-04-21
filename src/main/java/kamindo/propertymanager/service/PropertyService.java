package kamindo.propertymanager.service;

import kamindo.propertymanager.exception.BadRequestException;
import kamindo.propertymanager.exception.ResourceNotFoundException;
import kamindo.propertymanager.model.Property;
import kamindo.propertymanager.model.Unit;
import kamindo.propertymanager.repository.PropertyRepository;
import kamindo.propertymanager.request.CreatePropertyRequest;
import kamindo.propertymanager.request.CreateUnitRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

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
                        .property(property)
                        .build())
                .toList()
                .forEach(property::addUnit);

        Property newProperty = propertyRepository.save(property);
        return newProperty.getId();
    }

    public Long addUnit(CreateUnitRequest unitRequest, Long propertyId, String requester) {
        // get property
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        // check if person making request is the owner
        if (!property.getPropertyOwner().equals(requester)) {
            throw new ResourceAccessException("Unauthorized access to this resource");
        }

        // check if duplicate unit number exists
        boolean containsDuplicate = property.getUnits()
                .stream()
                .anyMatch(unit -> unit.getUnitNumber().equals(unitRequest.unitNumber()));
        if (containsDuplicate) {
            throw new BadRequestException("Property unit numbers must be unique");
        }

        Unit newUnit = Unit
                .builder()
                .unitNumber(unitRequest.unitNumber())
                .property(property)
                .build();
        property.addUnit(newUnit);

        propertyRepository.save(property);
        return newUnit.getId();
    }
}
