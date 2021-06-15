package facades;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.OwnerDto;
import dtos.PersonDto;
import dtos.WalkerDto;
import dtos.external.DawaDto;
import entities.Dog;
import entities.Owner;
import entities.Walker;
import utils.HttpUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
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

    private OwnerDto createOwner(OwnerDto ownerDto) {
        EntityManager em = emf.createEntityManager();
        try {
            Owner owner = Owner.builder()
                    .name(ownerDto.getName())
                    .dawaAddressId(ownerDto.getAddressId())
                    .phoneNumber(ownerDto.getPhone())
                    .dogs(
                            ownerDto.getDogs().stream().map(dog -> em.find(Dog.class, dog.getId())).collect(Collectors.toList())
                    )
                    .build();

            em.getTransaction().begin();
            em.merge(owner);
            em.getTransaction().commit();

            return new OwnerDto(owner, DawaFacade.getDawaByAddressId(owner.getDawaAddressId()));
        } finally {
            emf.close();
        }
    }

    private WalkerDto createWalker(WalkerDto walkerDto) {
        EntityManager em = emf.createEntityManager();
        try {
            Walker walker = Walker.builder()
                    .name(walkerDto.getName())
                    .dawaAddressId(walkerDto.getAddressId())
                    .phoneNumber(walkerDto.getPhone())
                    .dogs(
                            walkerDto.getDogs().stream().map(dog -> em.find(Dog.class, dog.getId())).collect(Collectors.toList())
                    )
                    .build();

            em.getTransaction().begin();
            em.merge(walker);
            em.getTransaction().commit();

            return new WalkerDto(walker, DawaFacade.getDawaByAddressId(walker.getDawaAddressId()));
        } finally {
            emf.close();
        }
    }

    public List<PersonDto> getAllWalkers() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Walker> q = em.createQuery("SELECT w FROM Walker w", Walker.class);
            return q.getResultList().stream()
                    .map(walker -> new WalkerDto(walker, DawaFacade.getDawaByAddressId(walker.getDawaAddressId())))
                    .collect(Collectors.toList());
        } finally {
            emf.close();
        }
    }

    public List<PersonDto> getAllOwners() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Owner> q = em.createQuery("SELECT w FROM Owner w", Owner.class);
            return q.getResultList().stream()
                    .map(owner -> new OwnerDto(owner, DawaFacade.getDawaByAddressId(owner.getDawaAddressId())))
                    .collect(Collectors.toList());
        } finally {
            emf.close();
        }
    }
}
