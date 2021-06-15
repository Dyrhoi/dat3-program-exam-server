package dtos.people._private;

import dtos.external.DawaDto;
import entities.Walker;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class PrivateWalkerDto extends PrivatePersonDto {
    List<PrivatePersonDto.PersonDogDto> dogs;

    public PrivateWalkerDto(Walker walker) {
        super(walker);
        this.dogs = walker.getDogs().stream().map(PrivateDogDto::new).map(PrivatePersonDto.PersonDogDto::new).collect(Collectors.toList());
    }

    public PrivateWalkerDto(Walker walker, DawaDto dawa) {
        super(walker, dawa);
        this.dogs = walker.getDogs().stream().map(PrivateDogDto::new).map(PrivatePersonDto.PersonDogDto::new).collect(Collectors.toList());
    }
}
