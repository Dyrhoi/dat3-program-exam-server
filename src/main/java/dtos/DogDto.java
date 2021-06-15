package dtos;

import entities.Dog;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class DogDto {
    private long id;
    private String name;
    private String breed;
    private String imageUrl;
    private String gender;
    private long birthdate;
    private List<PersonDto> walkers;
    private PersonDto owner;

    public DogDto(Dog dog) {
        this.id = dog.getId();
        this.name = dog.getName();
        this.breed = dog.getBreed();
        this.imageUrl = dog.getImageUrl();
        this.gender = dog.getGender().name().toLowerCase();
        this.birthdate = dog.getBirthdate().getTime();
        this.walkers = dog.getWalkers().stream().map(PersonDto::new).collect(Collectors.toList());
        this.owner = new PersonDto(dog.getOwner());
    }
}
