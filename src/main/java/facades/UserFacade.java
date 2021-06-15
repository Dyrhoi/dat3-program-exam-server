package facades;

import com.google.common.base.Strings;
import dtos.user.PrivateUserDto;
import entities.user.Role;
import entities.user.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang3.StringUtils;
import security.errorhandling.AuthenticationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class UserFacade {

    private static EntityManagerFactory emf;
    private static UserFacade instance;

    private UserFacade() {
    }

    /**
     * @param _emf
     * @return the instance of this facade.
     */
    public static UserFacade getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserFacade();
        }
        return instance;
    }

    public PrivateUserDto register(String username, String password, String passwordConfirm) {
        // Validation...
        List<String> validationErrors = new ArrayList<>();

        if(_get(username) != null)
            validationErrors.add("A user with this username already exists.");
        if(!password.equals(passwordConfirm))
            validationErrors.add("The passwords entered do not match.");

        if(!validationErrors.isEmpty())
            throw new WebApplicationException("Bad request: " + String.join(", ", validationErrors), 400);
        return new PrivateUserDto(_create(username, password, Collections.emptyList()));
    }

    public PrivateUserDto login(String username, String password) throws AuthenticationException {
        User user = _get(username);

        if (user == null || !user.verifyPassword(password)) {
            throw new AuthenticationException("Invalid user name or password");
        }
        return new PrivateUserDto(user);
    }

    public PrivateUserDto updateUser(PrivateUserDto updatedUser) {
        EntityManager em = emf.createEntityManager();

        try {
            // Replace all accessible entries for our user.
            User user = _get(updatedUser.getUsername());
            if (user == null)
                throw new WebApplicationException("Unknown user (" + updatedUser.getUsername() + ") requested.", 404);

            // Only replace posted fields (check if null first).

            if(updatedUser.getDisplayName() != null) {
                if(StringUtils.isEmpty(updatedUser.getDisplayName()))
                    user.setDisplayName(null);
                else
                    user.setDisplayName(updatedUser.getDisplayName());
            }


            em.getTransaction().begin();
            em.merge(user);
            em.getTransaction().commit();

            return getPrivate(user.getUsername());
        }
        finally {
            em.close();
        }
    }

    public PrivateUserDto getPrivate(String username) {
        User user = _get(username);
        if(user == null)
            throw new WebApplicationException("Unknown user (" + username + ") requested.", 404);

        return new PrivateUserDto(user);
    }

    public List<PrivateUserDto> getAllPrivate() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<User> q = em.createQuery("SELECT u FROM User u", User.class);
            return q.getResultList().stream().map(PrivateUserDto::new).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }



    /**
     *
     * @implNote Should only be used internally and not exposed to a public API.
     *
     * */

    public User _get(String username) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<User> q = em.createQuery("SELECT u from User u WHERE u.username = :username", User.class);
            q.setParameter("username", username);
            return q.getSingleResult();
        } catch(NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    /**
     *
     * @implNote Should only be used internally and not exposed to a public API.
     *
     * */

    public User _create(String username, String password, List<String> roles) {
        EntityManager em = emf.createEntityManager();
        try {
            User user = new User();

            // Validate
            if ((Strings.isNullOrEmpty(username) || username.length() < 3) || (Strings.isNullOrEmpty(password) || password.length() < 3))
                throw new WebApplicationException("Username and/or password should be more than 3 characters!", 400);

            user.setUsername(username);
            user.setPassword(password);

            // Always want the default role for a user.
            Role defaultRole = em.find(Role.class, Role.DEFAULT_ROLE);
            if(defaultRole == null)
                defaultRole = new Role(Role.DEFAULT_ROLE);
            user.addRole(defaultRole);

            // See if role already exists in database... if not:
            // check if the "new role" exists in our enums of roles (! no dynamic roles !), else skip this role.
            roles.forEach(roleName -> {
                Role role;
                role = em.find(Role.class, roleName);
                if(role == null) {
                    String foundSystemRole = Role.findRole(roleName);
                    if(foundSystemRole != null)
                        role = new Role(foundSystemRole);
                    else return;
                }

                user.addRole(role);
            });

            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();

            return user;
        } finally {
            em.close();
        }
    }
}