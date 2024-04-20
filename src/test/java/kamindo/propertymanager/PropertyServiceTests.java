package kamindo.propertymanager;

import kamindo.propertymanager.exception.BadRequestException;
import kamindo.propertymanager.model.Property;
import kamindo.propertymanager.model.PropertyType;
import kamindo.propertymanager.repository.PropertyRepository;
import kamindo.propertymanager.request.CreatePropertyRequest;
import kamindo.propertymanager.request.CreateUnitRequest;
import kamindo.propertymanager.service.PropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;

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
}
