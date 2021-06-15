package facades;

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

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

    private List<String> getPeople() {
        return new ArrayList<>();
    }

}
