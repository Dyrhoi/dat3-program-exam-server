package entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
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
public class Walker extends Person implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "walker_dogs",
            joinColumns = {@JoinColumn(name = "fk_walker_id")},
            inverseJoinColumns = {@JoinColumn(name = "fk_dog_id")})
    private List<Dog> dogs = new ArrayList<>();

    public void removeAllDogs() {
        // Avoiding concurrent exception...
        for (Iterator<Dog> iterator = this.getDogs().iterator(); iterator.hasNext(); ) {
            iterator.next();
            iterator.remove();
        }
    }
}
