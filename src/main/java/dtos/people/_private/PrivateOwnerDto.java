package dtos.people._private;

import dtos.external.DawaDto;
import entities.Owner;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class PrivateOwnerDto extends PrivatePersonDto {
    List<PrivatePersonDto.PersonDogDto> dogs;

    public PrivateOwnerDto(Owner owner) {
        super(owner);
        this.dogs = owner.getDogs().stream().map(PrivateDogDto::new).map(PrivatePersonDto.PersonDogDto::new).collect(Collectors.toList());
    }

    public PrivateOwnerDto(Owner owner, DawaDto dawa) {
        super(owner, dawa);
        this.dogs = owner.getDogs().stream().map(PrivateDogDto::new).map(PrivatePersonDto.PersonDogDto::new).collect(Collectors.toList());
    }
}
