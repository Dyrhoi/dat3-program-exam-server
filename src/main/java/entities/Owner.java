package entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
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

    public void removeAllDogs() {
        // Avoiding concurrent exception...
        for (Iterator<Dog> iterator = this.getDogs().iterator(); iterator.hasNext();) {
            Dog dog = iterator.next();
            dog.removeAllWalkers();
            iterator.remove();
        }
    }
}
