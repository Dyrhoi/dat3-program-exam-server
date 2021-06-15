package dtos;

import dtos.external.DawaDto;
import entities.Owner;
import entities.Person;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Data
@SuperBuilder
@AllArgsConstructor
public class PersonDto {
    private String name;
    private String phone;
    private AddressDto address;

    public PersonDto(Person person) {
        this.name = person.getName();
        this.phone = person.getPhoneNumber();
        this.address = new AddressDto();
    }

    public PersonDto(Person person, DawaDto dawa) {
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
     *
     * We do this to avoid infinite pointers: dog -> owner -> [dogs ... -> owner -> [dogs ... ] ... ]
     *
     * */
    protected class PersonDogDto {
        private String name;
        private String breed;
        private String imageUrl;
        private String gender;
        private long birthdate;

        public PersonDogDto(DogDto dogDto) {
            this.name = dogDto.getName();
            this.breed = dogDto.getBreed();
            this.gender = dogDto.getGender();
            this.imageUrl = dogDto.getImageUrl();
            this.birthdate = dogDto.getBirthdate();
        }
    }
}
