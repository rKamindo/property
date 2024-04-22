package kamindo.propertymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UnitDTO {
    private Long id;
    private String unitNumber;
    private Long propertyId;
}
