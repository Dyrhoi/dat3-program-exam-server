package facades;

import com.mysql.cj.util.StringUtils;
import dtos.people._private.PrivateOwnerDto;
import dtos.people._private.PrivatePersonDto;
import dtos.people._private.PrivateWalkerDto;
import dtos.people._public.PublicOwnerDto;
import dtos.people._public.PublicPersonDto;
import dtos.people._public.PublicWalkerDto;
import entities.Dog;
import entities.Owner;
import entities.Person;
import entities.Walker;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PersonFacade {

    private static EntityManagerFactory emf;
    private static PersonFacade instance;

    private PersonFacade() {
    }

    /**
     * @param _emf
     * @return the instance of this facade.
     */
    public static PersonFacade getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonFacade();
        }
        return instance;
    }

    public PrivatePersonDto createOwner(PrivateOwnerDto ownerDto) {
        EntityManager em = emf.createEntityManager();
        try {
            Owner owner = Owner.builder()
                    .name(ownerDto.getName())
                    .dawaAddressId(ownerDto.getAddressId())
                    .phoneNumber(ownerDto.getPhone())
                    .dogs(
                            ownerDto.getDogs() == null
                                    ? new ArrayList<>()
                                    : ownerDto.getDogs().stream().map(dog -> em.find(Dog.class, dog.getId())).collect(Collectors.toList())
                    )
                    .build();

            em.getTransaction().begin();
            em.persist(owner);
            em.getTransaction().commit();

            return new PrivateOwnerDto(owner, DawaFacade.getDawaByAddressId(owner.getDawaAddressId()));
        } finally {
            em.close();
        }
    }

    public PrivatePersonDto createWalker(PrivateWalkerDto walkerDto) {
        EntityManager em = emf.createEntityManager();
        try {
            Walker walker = Walker.builder()
                    .name(walkerDto.getName())
                    .dawaAddressId(walkerDto.getAddressId())
                    .phoneNumber(walkerDto.getPhone())
                    .dogs(
                            walkerDto.getDogs() == null
                                    ? new ArrayList<>()
                                    : walkerDto.getDogs().stream().map(dog -> em.find(Dog.class, dog.getId())).collect(Collectors.toList())
                    )
                    .build();

            em.getTransaction().begin();
            em.persist(walker);
            em.getTransaction().commit();

            return new PrivateWalkerDto(walker, DawaFacade.getDawaByAddressId(walker.getDawaAddressId()));
        } finally {
            em.close();
        }
    }

    public List<PrivatePersonDto> getAllWalkersPrivate() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Walker> q = em.createQuery("SELECT w FROM Walker w", Walker.class);
            return q.getResultList().stream()
                    .map(walker -> new PrivateWalkerDto(walker, DawaFacade.getDawaByAddressId(walker.getDawaAddressId())))
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public List<PrivatePersonDto> getAllOwnersPrivate() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Owner> q = em.createQuery("SELECT w FROM Owner w", Owner.class);
            return q.getResultList().stream()
                    .map(owner -> new PrivateOwnerDto(owner, DawaFacade.getDawaByAddressId(owner.getDawaAddressId())))
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public List<PublicPersonDto> getAllOwnersPublic() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Owner> q = em.createQuery("SELECT w FROM Owner w", Owner.class);
            return q.getResultList().stream()
                    .map(PublicOwnerDto::new)
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public List<PublicPersonDto> getAllWalkersPublic() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Walker> q = em.createQuery("SELECT w FROM Walker w", Walker.class);
            return q.getResultList().stream()
                    .map(PublicWalkerDto::new)
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public PublicPersonDto getOwnerPublic(long id) {
        return new PublicOwnerDto((Owner) _get(id));
    }

    public PrivatePersonDto updatePerson(PrivatePersonDto updatedPerson) {
        // Only replace posted fields.

        EntityManager em = emf.createEntityManager();
        try {
            Person person = _get(updatedPerson.getId());

            // Update "Person class"

            if (!StringUtils.isNullOrEmpty(updatedPerson.getName())) {
                person.setName(updatedPerson.getName());
            }
            if (!StringUtils.isNullOrEmpty(updatedPerson.getAddressId())) {
                person.setDawaAddressId(updatedPerson.getAddressId());
            }
            if (!StringUtils.isNullOrEmpty(updatedPerson.getPhone())) {
                person.setPhoneNumber(updatedPerson.getPhone());
            }

            // Figure out instance
            // We have different fields on each Person Type we would update.
            List<Dog> dogsToRemove = new ArrayList<>();
            if (person instanceof Owner) {
                Owner owner = (Owner) person;
                // We now expect the updated user to be a PrivateOwnerDto

                PrivateOwnerDto po = (PrivateOwnerDto) updatedPerson;
                if (po.getDogs() != null && !po.getDogs().isEmpty()) {
                    List<Dog> newDogs = po.getDogs().stream().map(dogDto -> em.find(Dog.class, dogDto.getId())).collect(Collectors.toList());
                    dogsToRemove = new ArrayList<>(owner.getDogs());
                    dogsToRemove.removeAll(newDogs);

                    owner.setDogs(newDogs);
                }

                person = owner;

                em.getTransaction().begin();
                // Make the managed by entity manager and delete the dogs. ENSURE BIDRECTIONAL
                // Also this is a fucking mess... Please don't let me touch JPA with a ten foot stick again...
                // Spring boot please, or let me use graphql with typescript.
                dogsToRemove.stream().map(dog -> em.find(Dog.class, dog.getId())).forEach(dog -> {
                    dog.removeAllWalkers();
                    em.remove(dog);
                });
                em.merge(person);
                em.getTransaction().commit();

            } else {
                Walker walker = (Walker) person;
                // We now expect the updated user to be a PrivateOwnerDto
                PrivateWalkerDto pw = (PrivateWalkerDto) updatedPerson;
                if (pw.getDogs() != null && !pw.getDogs().isEmpty()) {
                    List<Dog> newDogs = pw.getDogs().stream().map(dogDto -> em.find(Dog.class, dogDto.getId())).collect(Collectors.toList());
                    dogsToRemove = new ArrayList<>(walker.getDogs());
                    dogsToRemove.removeAll(newDogs);

                    walker.setDogs(newDogs);

                    person = walker;

                    em.getTransaction().begin();
                    dogsToRemove.stream().map(dog -> em.find(Dog.class, dog.getId())).forEach(dog -> {
                        dog.removeWalker(walker);
                    });
                    em.merge(person);
                    em.getTransaction().commit();
                }
            }

            if (person instanceof Owner) {
                return new PrivateOwnerDto((Owner) _get(person.getId()), DawaFacade.getDawaByAddressId(person.getDawaAddressId()));
            } else if (person instanceof Walker) {
                return new PrivateWalkerDto((Walker) _get(person.getId()), DawaFacade.getDawaByAddressId(person.getDawaAddressId()));
            }
            throw new WebApplicationException("error occured updating user.", 500);
        } finally {
            em.close();
        }
    }

    public Person _get(long id) {
        EntityManager em = emf.createEntityManager();
        try {
            Person person = em.find(Person.class, id);
            if (person == null)
                throw new WebApplicationException("No user found with this id (" + id + ").", 404);
            return person;
        } finally {
            em.close();
        }
    }
}
