package dtos.people._private;

import dtos.AddressDto;
import dtos.external.DawaDto;
import entities.Owner;
import entities.Person;
import entities.Walker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
public class PrivatePersonDto {
    private long id;
    private String name;
    private String phone;
    private String addressId;
    private AddressDto address;

    public PrivatePersonDto(Person person) {
        this.id = person.getId();
        this.name = person.getName();
        this.addressId = person.getDawaAddressId();
        this.phone = person.getPhoneNumber();
        this.address = new AddressDto();
    }

    public PrivatePersonDto(Person person, DawaDto dawa) {
        this(person);

        // Is this super scuffed..?
        // Is there a better way to translate? Probably.
        // Yes...
        this.address.setId(dawa.getId());
        this.address.setStreet(dawa.getVejnavn());
        this.address.setStreetNumber(dawa.getHusnr());
        this.address.setFloor(dawa.getEtage());
        this.address.setZip(dawa.getPostnr());
        this.address.setCity(dawa.getPostnrnavn());
        this.address.setLongitude(String.valueOf(dawa.getX()));
        this.address.setLatitude(String.valueOf(dawa.getY()));
        this.address.setDesignation(dawa.getBetegnelse());
    }

    /**
     * We do this to avoid infinite pointers: dog -> owner -> [dogs ... -> owner -> [dogs ... ] ... ]
     */
    @Data
    public static class PersonDogDto {
        private long id;
        private String name;
        private String breed;
        private String imageUrl;
        private String gender;
        private long birthdate;

        public PersonDogDto(PrivateDogDto privateDogDto) {
            this.id = privateDogDto.getId();
            this.name = privateDogDto.getName();
            this.breed = privateDogDto.getBreed();
            this.gender = privateDogDto.getGender();
            this.imageUrl = privateDogDto.getImageUrl();
            this.birthdate = privateDogDto.getBirthdate();
        }
    }
}
