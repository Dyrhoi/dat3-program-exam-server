package dtos.people._private;

import entities.Dog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
public class PrivateDogDto {
    private long id;
    private String name;
    private String breed;
    private String imageUrl;
    private String gender;
    private long birthdate;
    private List<PrivatePersonDto> walkers;
    private PrivatePersonDto owner;

    public PrivateDogDto(Dog dog) {
        this.id = dog.getId();
        this.name = dog.getName();
        this.breed = dog.getBreed();
        this.imageUrl = dog.getImageUrl();
        this.gender = dog.getGender().name().toLowerCase();
        this.birthdate = dog.getBirthdate().getTime();
        this.walkers = dog.getWalkers().stream().map(PrivatePersonDto::new).collect(Collectors.toList());
        this.owner = new PrivatePersonDto(dog.getOwner());
    }
}
