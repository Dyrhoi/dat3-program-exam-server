package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.people._private.PrivateDogDto;
import dtos.people._private.PrivateOwnerDto;
import dtos.people._private.PrivatePersonDto;
import dtos.people._private.PrivateWalkerDto;
import dtos.user.PrivateUserDto;
import facades.DogFacade;
import facades.PersonFacade;
import facades.UserFacade;
import utils.EMF_Creator;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("admin")
@RolesAllowed("admin")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final UserFacade USER_FACADE = UserFacade.getInstance(EMF);
    private static final PersonFacade PERSON_FACADE = PersonFacade.getInstance(EMF);
    private static final DogFacade DOG_FACADE = DogFacade.getInstance(EMF);

    public AdminResource() {
    }

    @GET
    @Path("/users")
    public Response getUsers() {
        List<PrivateUserDto> users = USER_FACADE.getAllPrivate();
        return Response.ok().entity(GSON.toJson(users)).build();
    }

    @GET
    @Path("/people/owners")
    public Response getOwners() {
        List<PrivatePersonDto> people = PERSON_FACADE.getAllOwnersPrivate();
        return Response.ok().entity(GSON.toJson(people)).build();
    }

    @GET
    @Path("/people/walkers")
    public Response getWalkers() {
        List<PrivatePersonDto> people = PERSON_FACADE.getAllWalkersPrivate();
        return Response.ok().entity(GSON.toJson(people)).build();
    }

    @GET
    @Path("/dogs")
    public Response getDogs(String json) {
        List<PrivateDogDto> dogs = DOG_FACADE.getAllDogs();
        return Response.ok().entity(GSON.toJson(dogs)).build();
    }

    @POST
    @Path("/people/owners")
    public Response createOwner(String json) {
        PrivatePersonDto ownerDto = PERSON_FACADE.createOwner(GSON.fromJson(json, PrivateOwnerDto.class));
        return Response.ok().entity(GSON.toJson(ownerDto)).build();
    }

    @POST
    @Path("/people/walkers")
    public Response createWalker(String json) {
        PrivatePersonDto walkerDto = PERSON_FACADE.createWalker(GSON.fromJson(json, PrivateWalkerDto.class));
        return Response.ok().entity(GSON.toJson(walkerDto)).build();
    }

    @POST
    @Path("/dogs")
    public Response createDog(String json) {
        PrivateDogDto walkerDto = DOG_FACADE.createDog(GSON.fromJson(json, PrivateDogDto.class));
        return Response.ok().entity(GSON.toJson(walkerDto)).build();
    }

}
