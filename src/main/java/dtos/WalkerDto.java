package dtos;

import dtos.external.DawaDto;
import entities.Owner;
import entities.Walker;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class WalkerDto extends PersonDto {
    List<PersonDto.PersonDogDto> dogs;

    public WalkerDto(Walker walker) {
        super(walker);
        this.dogs = walker.getDogs().stream().map(DogDto::new).map(PersonDto.PersonDogDto::new).collect(Collectors.toList());
    }

    public WalkerDto(Walker walker, DawaDto dawa) {
        super(walker, dawa);
        this.dogs = walker.getDogs().stream().map(DogDto::new).map(PersonDto.PersonDogDto::new).collect(Collectors.toList());
    }
}
