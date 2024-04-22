package kamindo.propertymanager.dto;

import kamindo.propertymanager.model.PropertyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
@AllArgsConstructor
public class PropertyDTO {
    private Long id;
    private String address;
    private PropertyType propertyType;
    private Long unitCount;
}