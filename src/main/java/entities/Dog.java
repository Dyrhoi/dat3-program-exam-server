package entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
public class Dog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date createdAt;
    private Date updatedAt;

    private String name;
    private String breed;
    private String imageUrl;
    private GenderTypes gender;
    private Date birthdate;

    @ManyToMany(mappedBy = "dogs", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Walker> walkers = new ArrayList<>();

    @ManyToOne
    private Owner owner;

    @PrePersist
    private void onCreate() {
        this.createdAt = new Date();
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = new Date();
    }


    public enum GenderTypes {
        MALE,
        FEMALE,
        OTHER
    }

    public void addWalker(Walker walker) {
        if (walkers == null)
            walkers = new ArrayList<>();
        walkers.add(walker);
        walker.getDogs().add(this);
    }

    public void removeWalker(Walker walker) {
        walkers.remove(walker);
        walker.getDogs().remove(this);
    }

    public void removeAllWalkers() {
        // Avoiding concurrent exception...
        for (Iterator<Walker> iterator = this.getWalkers().iterator(); iterator.hasNext(); ) {
            Walker walker = iterator.next();
            walker.getDogs().remove(this);
            iterator.remove();
        }
    }

    public void setOwner(Owner newOwner) {
        if (newOwner == null) {
            if (this.owner != null)
                this.owner.removeDog(this);
        } else {
            newOwner.addDog(this);
        }
        this.owner = newOwner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dog dog = (Dog) o;
        return id.equals(dog.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
