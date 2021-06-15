package entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Entity
public class Owner extends Person implements Serializable {

    private static final long serialVersionUID = 1L;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "walker_dogs",
            joinColumns = {@JoinColumn(name = "fk_walker_id")},
            inverseJoinColumns = {@JoinColumn(name = "fk_dog_id")})
    private List<Dog> dogs;
}
