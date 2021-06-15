package entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class Dog implements Serializable  {

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

    @ManyToMany(mappedBy = "dogs")
    private List<Walker> walkers;

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
}
