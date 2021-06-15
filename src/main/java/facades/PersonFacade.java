package facades;

import dtos.OwnerDto;
import dtos.PersonDto;
import dtos.WalkerDto;
import entities.Dog;
import entities.Owner;
import entities.Walker;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
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

    public PersonDto createOwner(OwnerDto ownerDto) {
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

            return new OwnerDto(owner, DawaFacade.getDawaByAddressId(owner.getDawaAddressId()));
        } finally {
            em.close();
        }
    }

    public PersonDto createWalker(WalkerDto walkerDto) {
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

            return new WalkerDto(walker, DawaFacade.getDawaByAddressId(walker.getDawaAddressId()));
        } finally {
            em.close();
        }
    }

    public List<PersonDto> getAllWalkersPrivate() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Walker> q = em.createQuery("SELECT w FROM Walker w", Walker.class);
            return q.getResultList().stream()
                    .map(walker -> new WalkerDto(walker, DawaFacade.getDawaByAddressId(walker.getDawaAddressId())))
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    public List<PersonDto> getAllOwnersPrivate() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Owner> q = em.createQuery("SELECT w FROM Owner w", Owner.class);
            return q.getResultList().stream()
                    .map(owner -> new OwnerDto(owner, DawaFacade.getDawaByAddressId(owner.getDawaAddressId())))
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }
}
