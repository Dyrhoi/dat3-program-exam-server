package dtos.people._public;

import entities.Owner;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class PublicOwnerDto extends PublicPersonDto {
    List<PersonDogDto> dogs;

    public PublicOwnerDto(Owner owner) {
        super(owner);
        this.dogs = owner.getDogs().stream().map(PublicDogDto::new).map(PersonDogDto::new).collect(Collectors.toList());
    }
}
