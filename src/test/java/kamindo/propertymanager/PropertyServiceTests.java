package kamindo.propertymanager;

import kamindo.propertymanager.dto.PropertyDTO;
import kamindo.propertymanager.dto.UnitDTO;
import kamindo.propertymanager.exception.BadRequestException;
import kamindo.propertymanager.exception.ResourceNotFoundException;
import kamindo.propertymanager.model.Property;
import kamindo.propertymanager.model.PropertyType;
import kamindo.propertymanager.model.Unit;
import kamindo.propertymanager.repository.PropertyRepository;
import kamindo.propertymanager.request.CreatePropertyRequest;
import kamindo.propertymanager.request.CreateUnitRequest;
import kamindo.propertymanager.service.PropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.web.client.ResourceAccessException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PropertyServiceTests {
    private PropertyService propertyService;
    private PropertyRepository propertyRepository;

    @BeforeEach
    public void setUp() {
        // Create a mock instance of PropertyRepository
        propertyRepository = mock(PropertyRepository.class);
        // Initialize PropertyService with the mock repository
        propertyService = new PropertyService(propertyRepository);
    }

    @Test
    public void createSingleUnitProperty_ShouldSave_AndReturnPropertyId() {
        String owner = "owner1";
        CreateUnitRequest unitRequest = new CreateUnitRequest("101");

        CreatePropertyRequest propertyRequest = new CreatePropertyRequest(
                "123 Main St",
                PropertyType.SINGLE_UNIT,
                List.of(unitRequest)
        );

        // Mock the behavior of PropertyRepository to return the saved property
        when(propertyRepository.save(any(Property.class))).thenAnswer(invocation -> {
            Property savedProperty = invocation.getArgument(0);
            savedProperty.setId(1L); // Simulate setting the ID
            return savedProperty;
        });

        // Call the service method to create the property
        Long propertyId = propertyService.createProperty(propertyRequest, owner);

        ArgumentCaptor<Property> propertyCaptor = ArgumentCaptor.forClass(Property.class);
        verify(propertyRepository).save(propertyCaptor.capture());

        // Assertions
        assertEquals(1, propertyCaptor.getValue().getUnits().size());
        assertEquals(owner, propertyCaptor.getValue().getPropertyOwner());
        assertNotNull(propertyId); // Ensure propertyId returned is not null
    }

    @Test
    public void createMultiUnitProperty_ShouldSave_AndReturnPropertyId() {
        String owner = "owner1";
        CreateUnitRequest unitRequest1 = new CreateUnitRequest("101");
        CreateUnitRequest unitRequest2 = new CreateUnitRequest("102");

        CreatePropertyRequest propertyRequest = new CreatePropertyRequest(
                "123 Main St",
                PropertyType.MULTI_UNIT,
                List.of(unitRequest1, unitRequest2)
        );

        when(propertyRepository.save(any(Property.class))).thenAnswer(invocation -> {
            Property savedProperty = invocation.getArgument(0);
            savedProperty.setId(1L); // Simulate setting the ID
            return savedProperty;
        });

        Long propertyId = propertyService.createProperty(propertyRequest, owner);

        ArgumentCaptor<Property> propertyCaptor = ArgumentCaptor.forClass(Property.class);
        verify(propertyRepository).save(propertyCaptor.capture());

        assertEquals(2, propertyCaptor.getValue().getUnits().size());
        assertEquals(owner, propertyCaptor.getValue().getPropertyOwner());
        assertNotNull(propertyId); // Ensure propertyId returned is not null

    }

    @Test
    public void testCreateProperty_WithNoUnits_ShouldNotSave() {
        String owner = "owner1";
        CreatePropertyRequest request = new CreatePropertyRequest(
                "123 Main St",
                PropertyType.SINGLE_UNIT,
                List.of()
        );

        // Mock the behavior of PropertyRepository to return a property with an ID when saved
        when(propertyRepository.save(Mockito.any(Property.class))).thenThrow(RuntimeException.class);

        // Use assertThrows to verify that the service method throws BadRequestException
        assertThrows(BadRequestException.class, () -> propertyService.createProperty(request, owner));

        // Verify that no property is saved by ensuring that the save method is never called
        verify(propertyRepository, never()).save(Mockito.any(Property.class));
    }
    @Test
    public void testCreateMultiUnitPropertyWithTypeSingleUnit_ShouldNotGoThrough() {
        String owner = "owner1";
        CreateUnitRequest unitRequest1 = new CreateUnitRequest("101");
        CreateUnitRequest unitRequest2 = new CreateUnitRequest("102");
        CreatePropertyRequest request = new CreatePropertyRequest(
                "123 Main St",
                PropertyType.SINGLE_UNIT,
                List.of(unitRequest1, unitRequest2)
        );

        // Mock the behavior of PropertyRepository to return a property with an ID when saved
        when(propertyRepository.save(Mockito.any(Property.class))).thenThrow(RuntimeException.class);

        // Use assertThrows to verify that the service method throws BadRequestException
        assertThrows(BadRequestException.class, () -> propertyService.createProperty(request, owner));

        // Verify that no property is saved by ensuring that the save method is never called
        verify(propertyRepository, never()).save(Mockito.any(Property.class));
    }

    @Test
    public void createMultiUnitProperty_WithDuplicateUnitNumbers_ShouldNotSave() {
        String owner = "owner1";
        CreateUnitRequest unitRequest1 = new CreateUnitRequest("101");
        CreateUnitRequest unitRequest2 = new CreateUnitRequest("101");
        CreatePropertyRequest request = new CreatePropertyRequest(
                "123 Main St",
                PropertyType.MULTI_UNIT,
                List.of(unitRequest1, unitRequest2)
        );

        when(propertyRepository.save(Mockito.any(Property.class))).thenThrow(RuntimeException.class);

        assertThrows(BadRequestException.class, () -> propertyService.createProperty(request, owner));
        verify(propertyRepository, never()).save(Mockito.any(Property.class));
    }



    @Test
    public void addUnitToProperty_ShouldSave_AndReturnUnitId() {
        String owner = "owner1";

        Unit unit1 = Unit
                .builder()
                .unitNumber("101")
                .build();

        Property property = Property.builder()
                .address("123 Main St")
                .propertyType(PropertyType.MULTI_UNIT)
                .propertyOwner(owner)
                .build();
        property.addUnit(unit1);

        unit1.setProperty(property);

        CreateUnitRequest unitRequest = new CreateUnitRequest("102");

        when(propertyRepository.findById(anyLong())).thenReturn(Optional.of(property));

        propertyService.addUnit(unitRequest, 1L, owner);

        ArgumentCaptor<Property> propertyCaptor = ArgumentCaptor.forClass(Property.class);
        verify(propertyRepository).save(propertyCaptor.capture());

        Property savedProperty = propertyCaptor.getValue();
        Unit newUnit = savedProperty.getUnits().stream()
                .filter(unit -> unit.getUnitNumber().equals("102"))
                .findFirst()
                .orElse(null);

        assertEquals(2, propertyCaptor.getValue().getUnits().size());
        assertNotNull(newUnit);
    }

    @Test
    public void addUnitTo_SingleUnitProperty_ShouldThrow_BadRequestException() {
        String owner = "owner1";

        Unit unit1 = Unit
                .builder()
                .unitNumber("101")
                .build();

        Property property = Property.builder()
                .address("123 Main St")
                .propertyType(PropertyType.SINGLE_UNIT)
                .propertyOwner(owner)
                .build();
        property.addUnit(unit1);

        CreateUnitRequest unitRequest = new CreateUnitRequest("102");

        when(propertyRepository.findById(anyLong())).thenReturn(Optional.of(property));

        assertThrows(BadRequestException.class, () -> propertyService.addUnit(unitRequest, 1L, owner));

        verify(propertyRepository, never()).save(Mockito.any(Property.class));
    }

    @Test
    public void addUnit_NotOwner_ShouldThrow_BadRequestException() {
        String owner = "owner1";

        Unit unit1 = Unit
                .builder()
                .unitNumber("101")
                .build();

        Property property = Property.builder()
                .address("123 Main St")
                .propertyType(PropertyType.MULTI_UNIT)
                .propertyOwner(owner)
                .build();
        property.addUnit(unit1);

        unit1.setProperty(property);

        CreateUnitRequest unitRequest = new CreateUnitRequest("102");

        when(propertyRepository.findById(anyLong())).thenReturn(Optional.of(property));

        assertThrows(ResourceAccessException.class,
                () -> propertyService.addUnit(unitRequest, 1L, "notOwner"));
        verify(propertyRepository, never()).save(Mockito.any(Property.class));
    }

    @Test
    public void getProperties_ShouldReturn_PropertiesForUser() {
        List<PropertyDTO> expectedProperties = List.of(
                PropertyDTO.builder()
                        .id(1L)
                        .address("123 Main St")
                        .propertyType(PropertyType.MULTI_UNIT)
                        .unitCount(2L)
                        .build(),
                PropertyDTO.builder()
                        .id(2L)
                        .address("313 Main St")
                        .propertyType(PropertyType.SINGLE_UNIT)
                        .unitCount(1L)
                        .build()
        );

        when(propertyRepository.getPropertiesForUser(anyString())).thenReturn(expectedProperties);

        List<PropertyDTO> actualProperties = propertyService.getPropertiesForUser("owner1");

        assertNotNull(actualProperties);
        assertEquals(2, actualProperties.size());
        assertEquals("123 Main St", actualProperties.get(0).getAddress());
        assertTrue(actualProperties.stream().anyMatch(p -> p.getPropertyType().equals(PropertyType.SINGLE_UNIT)));
    }

    @Test
    public void getUnitsForProperty_ShouldReturn_AllUnitsForProperty() {
        List<UnitDTO> expectedUnits = List.of(
                UnitDTO.builder()
                        .id(1L)
                        .unitNumber("101")
                        .propertyId(1L)
                        .build(),
                UnitDTO.builder()
                        .id(2L)
                        .unitNumber("102")
                        .propertyId(1L)
                        .build()
        );

        when(propertyRepository.getUnitsByProperty(anyLong())).thenReturn(expectedUnits);

        List<UnitDTO> actualUnits = propertyService.getUnitsForProperty(1L);

        assertNotNull(actualUnits);
        assertEquals(2, actualUnits.size());
        assertEquals("101", actualUnits.get(0).getUnitNumber());
        assertEquals(1L, actualUnits.get(1).getPropertyId());
    }

    @Test
    public void getUnitsFor_NonExistingProperty_ShouldThrow404Exception() {
        when(propertyRepository.getUnitsByProperty(anyLong())).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> {
            propertyService.getUnitsForProperty(1L);
        });
    }
}
