package kamindo.propertymanager.request;

import kamindo.propertymanager.model.PropertyType;

import java.util.List;

public record CreatePropertyRequest(
        String address,
        PropertyType propertyType,
        List<CreateUnitRequest> units
) {}
