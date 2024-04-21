package kamindo.propertymanager.controller;

import kamindo.propertymanager.request.CreatePropertyRequest;
import kamindo.propertymanager.request.CreateUnitRequest;
import kamindo.propertymanager.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/properties")
public class PropertyController {
    private final PropertyService propertyService;

    @PostMapping("")
    public ResponseEntity<Long> createProperty(
            @RequestBody CreatePropertyRequest request,
            Principal principal) {
        Long propertyId = propertyService.createProperty(request, principal.getName());
        return ResponseEntity.ok(propertyId);
    }

    @PostMapping("/{propertyId}/units")
    public ResponseEntity<Long> addUnit(
            @RequestBody CreateUnitRequest request,
            @PathVariable Long propertyId,
            Principal principal
    ) {
        Long unitId = propertyService.addUnit(request, propertyId, principal.getName());
        return ResponseEntity.ok(unitId);
    }
}
