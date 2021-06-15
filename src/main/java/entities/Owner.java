package entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
@Entity
public class Owner extends Person implements Serializable {

    private static final long serialVersionUID = 1L;

    @OneToMany(
            mappedBy = "owner",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Dog> dogs = new ArrayList<>();

    public void removeDog(Dog dog) {
        dogs.remove(dog);
    }

    public void addDog(Dog dog) {
        dogs.add(dog);
    }
}
