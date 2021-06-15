package facades;

import dtos.people._private.PrivateDogDto;
import dtos.people._public.PublicDogDto;
import entities.Dog;
import entities.Owner;
import entities.Walker;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.WebApplicationException;
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

    public PrivateDogDto create(PrivateDogDto privateDogDto) {
        EntityManager em = emf.createEntityManager();
        try {
            Dog dog = Dog.builder()
                    .name(privateDogDto.getName())
                    .breed(privateDogDto.getBreed())
                    .imageUrl(privateDogDto.getImageUrl())
                    .gender(Dog.GenderTypes.valueOf(privateDogDto.getGender().toUpperCase()))
                    .birthdate(new Date(privateDogDto.getBirthdate()))
                    .build();

            // Ensure Bidirectional update.
            if (privateDogDto.getWalkers() != null)
                privateDogDto.getWalkers().forEach(walker ->
                        dog.addWalker(em.find(Walker.class, walker.getId()))
                );
            dog.setOwner(em.find(Owner.class, privateDogDto.getOwner().getId()));
            em.getTransaction().begin();
            em.persist(dog);
            em.getTransaction().commit();

            return new PrivateDogDto(dog);
        } finally {
            em.close();
        }
    }

    public List<PrivateDogDto> getAllPrivate() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Dog> q = em.createQuery("SELECT w FROM Dog w", Dog.class);
            return q.getResultList().stream().map(PrivateDogDto::new).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public List<PublicDogDto> getAllPublic() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Dog> q = em.createQuery("SELECT w FROM Dog w", Dog.class);
            return q.getResultList().stream().map(PublicDogDto::new).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public PublicDogDto getPublic(long id) {
        return new PublicDogDto(_get(id));
    }

    public Dog _get(long id) {
        EntityManager em = emf.createEntityManager();
        try {
            Dog dog = em.find(Dog.class, id);
            if (dog == null)
                throw new WebApplicationException("No dog found with this id (" + id + ").", 404);
            return dog;
        } finally {
            em.close();
        }
    }
}
