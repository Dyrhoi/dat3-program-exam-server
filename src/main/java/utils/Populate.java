package utils;


import com.google.common.base.Strings;
import dtos.people._private.PrivateDogDto;
import dtos.people._private.PrivateOwnerDto;
import dtos.people._private.PrivatePersonDto;
import dtos.people._private.PrivateWalkerDto;
import facades.DogFacade;
import facades.PersonFacade;
import facades.UserFacade;

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Populate {
    private final EntityManagerFactory emf;
    private static PrivatePersonDto owner = null;
    private static PrivatePersonDto walker1 = null;
    private static PrivatePersonDto walker2 = null;


    public static void main(String[] args) {
        new Populate(EMF_Creator.createEntityManagerFactory()).populateAll();
    }

    public Populate(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public List<String> populateAll() {
        List<String> populated = new ArrayList<>();
        if (populateUsers())
            populated.add("users");
        if (populatePeople())
            populated.add("people [owners/walkers]");
        if (populateDogs())
            populated.add("dogs");

        return populated;
    }

    /**
     * @return Boolean regarding table being populated or not.
     */
    public boolean populateUsers() throws IllegalArgumentException {
        UserFacade userFacade = UserFacade.getInstance(this.emf);

        if (!userFacade.getAllPrivate().isEmpty()) return false;

        // NOTICE: Always set your password as environment variables.
        String password_admin = "test";
        String password_user = "test";

        boolean isDeployed = System.getenv("DEPLOYED") != null;
        if (isDeployed) {
            password_user = System.getenv("PASSWORD_DEFAULT_USER");
            password_admin = System.getenv("PASSWORD_DEFAULT_ADMIN");

            // Do not allow "empty" passwords in production.
            if (Strings.isNullOrEmpty(password_admin) || password_admin.trim().length() < 3 || Strings.isNullOrEmpty(password_user) || password_user.trim().length() < 3)
                throw new IllegalArgumentException("FAILED POPULATE OF USERS: Passwords were empty or less than 3 characters? Are environment variables: [PASSWORD_DEFAULT_USER, PASSWORD_DEFAULT_ADMIN] set?");
        }

        userFacade._create("user", password_user, new ArrayList<>());
        userFacade._create("admin", password_admin, Collections.singletonList("admin"));

        return true;
    }

    public boolean populatePeople() throws IllegalArgumentException {
        PersonFacade personFacade = PersonFacade.getInstance(this.emf);

        if (!personFacade.getAllWalkersPrivate().isEmpty() && !personFacade.getAllOwnersPrivate().isEmpty())
            return false;

        if (personFacade.getAllWalkersPrivate().isEmpty()) {
            walker1 = personFacade.createWalker(
                    PrivateWalkerDto.builder()
                            .name("John Eriksen")
                            .addressId("000021c5-e9ee-411d-b2d8-ec9161780ccd")
                            .phone("+45 23232323")
                            .build()

            );

            walker2 = personFacade.createWalker(
                    PrivateWalkerDto.builder()
                            .name("Martin Svensen")
                            .addressId("d0def69f-e4b4-4eb1-a4df-fe36656fee58")
                            .phone("+45 10102323")
                            .build()

            );


        }
        if (personFacade.getAllOwnersPrivate().isEmpty()) {
            owner = personFacade.createOwner(
                    PrivateOwnerDto.builder()
                            .name("Ludwig Charles")
                            .addressId("0a3f50a0-37ad-32b8-e044-0003ba298018")
                            .phone("+45 20592020")
                            .build()

            );
        }

        return true;
    }

    public boolean populateDogs() throws IllegalArgumentException {
        DogFacade dogFacade = DogFacade.getInstance(this.emf);

        if (!dogFacade.getAllDogs().isEmpty()) return false;

        PrivateDogDto privateDogDto = PrivateDogDto.builder()
                .owner(owner)
                .walkers(Arrays.asList(walker1, walker2))
                .name("Fjeffi")
                .birthdate(1529057111L * 1000)
                .imageUrl("https://i.imgur.com/xyPtn4m.jpg")
                .breed("Labrador")
                .gender("female")
                .build();
        dogFacade.createDog(privateDogDto);

        privateDogDto = PrivateDogDto.builder()
                .owner(owner)
                .walkers(Collections.singletonList(walker1))
                .name("Bef")
                .birthdate(1529057111L * 1000)
                .imageUrl("https://i.imgur.com/Qnd34DM.jpg")
                .breed("Bulldog")
                .gender("male")
                .build();
        dogFacade.createDog(privateDogDto);
        return true;
    }

}
