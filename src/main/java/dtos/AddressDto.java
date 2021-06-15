package dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddressDto {
    private String id;
    private String street;
    private String streetNumber;
    private String floor;
    private String floorDoor;
    private String zip;
    private String city;
    private String latitude;
    private String longitude;
    private String designation;
}
