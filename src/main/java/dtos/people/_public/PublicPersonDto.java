package dtos.people._public;

import entities.Person;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
public class PublicPersonDto {
    private long id;
    private String name;

    public PublicPersonDto(Person person) {
        this.id = person.getId();
        this.name = person.getName();
    }

    /**
     * We do this to avoid infinite pointers: dog -> owner -> [dogs ... -> owner -> [dogs ... ] ... ]
     */
    @Data
    public class PersonDogDto {
        private long id;
        private String name;
        private String breed;
        private String imageUrl;
        private String gender;
        private long birthdate;

        public PersonDogDto(PublicDogDto publicDogDto) {
            this.id = publicDogDto.getId();
            this.name = publicDogDto.getName();
            this.breed = publicDogDto.getBreed();
            this.gender = publicDogDto.getGender();
            this.imageUrl = publicDogDto.getImageUrl();
            this.birthdate = publicDogDto.getBirthdate();
        }
    }
}
