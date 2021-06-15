package dtos;

import entities.Dog;
import lombok.Data;

@Data
public class DogDto {
    private String name;
    private String breed;
    private String imageUrl;
    private String gender;
    private long birthdate;

    public DogDto(Dog dog) {
        this.name = dog.getName();
        this.breed = dog.getBreed();
        this.imageUrl = dog.getImageUrl();
        this.gender = dog.getGender().name().toLowerCase();
        this.birthdate = dog.getBirthdate().getTime();
    }
}
