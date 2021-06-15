package dtos;

import dtos.external.DawaDto;
import entities.Owner;
import entities.Walker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class OwnerDto extends PersonDto {
    List<PersonDto.PersonDogDto> dogs;

    public OwnerDto(Owner owner) {
        super(owner);
        this.dogs = owner.getDogs().stream().map(DogDto::new).map(PersonDto.PersonDogDto::new).collect(Collectors.toList());
    }

    public OwnerDto(Owner owner, DawaDto dawa) {
        super(owner, dawa);
        this.dogs = owner.getDogs().stream().map(DogDto::new).map(PersonDto.PersonDogDto::new).collect(Collectors.toList());
    }
}
