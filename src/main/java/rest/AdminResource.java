package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.OwnerDto;
import dtos.PersonDto;
import dtos.WalkerDto;
import dtos.user.PrivateUserDto;
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

    public AdminResource() {}

    @GET
    @Path("/users")
    public Response getUsers() {
        List<PrivateUserDto> users = USER_FACADE.getAllPrivate();
        return Response.ok().entity(GSON.toJson(users)).build();
    }

    @GET
    @Path("/people/owners")
    public Response getOwners() {
        List<PersonDto> people = PERSON_FACADE.getAllOwnersPrivate();
        return Response.ok().entity(GSON.toJson(people)).build();
    }

    @GET
    @Path("/people/walkers")
    public Response getWalkers() {
        List<PersonDto> people = PERSON_FACADE.getAllWalkersPrivate();
        return Response.ok().entity(GSON.toJson(people)).build();
    }

    @POST
    @Path("/people/owners")
    public Response createOwner(String json) {
        PersonDto ownerDto = PERSON_FACADE.createOwner(GSON.fromJson(json, OwnerDto.class));
        return Response.ok().entity(GSON.toJson(ownerDto)).build();
    }

    @POST
    @Path("/people/walkers")
    public Response createWalker(String json) {
        PersonDto walkerDto = PERSON_FACADE.createWalker(GSON.fromJson(json, WalkerDto.class));
        return Response.ok().entity(GSON.toJson(walkerDto)).build();
    }

}
