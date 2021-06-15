package dtos.people._public;

import entities.Walker;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class PublicWalkerDto extends PublicPersonDto {
    List<PersonDogDto> dogs;

    public PublicWalkerDto(Walker walker) {
        super(walker);
        this.dogs = walker.getDogs().stream().map(PublicDogDto::new).map(PersonDogDto::new).collect(Collectors.toList());
    }
}
