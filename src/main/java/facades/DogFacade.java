package facades;

import dtos.DogDto;
import entities.Dog;
import entities.Owner;
import entities.Walker;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DogFacade {

    private static EntityManagerFactory emf;
    private static DogFacade instance;

    private DogFacade() {
    }

    /**
     * @param _emf
     * @return the instance of this facade.
     */
    public static DogFacade getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new DogFacade();
        }
        return instance;
    }

    public DogDto createDog(DogDto dogDto) {
        EntityManager em = emf.createEntityManager();
        try {
            Dog dog = Dog.builder()
                    .name(dogDto.getName())
                    .breed(dogDto.getBreed())
                    .imageUrl(dogDto.getImageUrl())
                    .gender(Dog.GenderTypes.valueOf(dogDto.getGender().toUpperCase()))
                    .birthdate(new Date(dogDto.getBirthdate()))
                    .build();

            // Ensure Bidirectional update.
            if (dogDto.getWalkers() != null)
                dogDto.getWalkers().forEach(walker ->
                        dog.addWalker(em.find(Walker.class, walker.getId()))
                );
            dog.setOwner(em.find(Owner.class, dogDto.getOwner().getId()));
            em.getTransaction().begin();
            em.persist(dog);
            em.getTransaction().commit();

            return new DogDto(dog);
        } finally {
            em.close();
        }
    }

    public List<DogDto> getAllDogs() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Dog> q = em.createQuery("SELECT w FROM Dog w", Dog.class);
            return q.getResultList().stream().map(DogDto::new).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }
}
