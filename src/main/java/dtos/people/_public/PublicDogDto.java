package dtos.people._public;

import entities.Dog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
public class PublicDogDto {
    private long id;
    private String name;
    private String breed;
    private String imageUrl;
    private String gender;
    private long birthdate;
    private List<PublicPersonDto> walkers;
    private PublicPersonDto owner;

    public PublicDogDto(Dog dog) {
        this.id = dog.getId();
        this.name = dog.getName();
        this.breed = dog.getBreed();
        this.imageUrl = dog.getImageUrl();
        this.gender = dog.getGender().name().toLowerCase();
        this.birthdate = dog.getBirthdate().getTime();
        this.walkers = dog.getWalkers().stream().map(PublicPersonDto::new).collect(Collectors.toList());
        this.owner = new PublicPersonDto(dog.getOwner());
    }
}
