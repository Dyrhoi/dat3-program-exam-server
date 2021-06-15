package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.people._public.PublicPersonDto;
import facades.PersonFacade;
import utils.EMF_Creator;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("people")
@RolesAllowed("user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PeopleResource {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final PersonFacade PERSON_FACADE = PersonFacade.getInstance(EMF);

    public PeopleResource() {
    }

    @GET
    @Path("/owners")
    public Response getOwners() {
        List<PublicPersonDto> owners = PERSON_FACADE.getAllOwnersPublic();
        return Response.ok().entity(GSON.toJson(owners)).build();
    }

    @GET
    @Path("/owners/{id}")
    public Response getOwners(@PathParam("id") long id) {
        PublicPersonDto owner = PERSON_FACADE.getOwnerPublic(id);
        return Response.ok().entity(GSON.toJson(owner)).build();
    }

    @GET
    @Path("/walkers")
    public Response getWalkers() {
        List<PublicPersonDto> walkers = PERSON_FACADE.getAllWalkersPublic();
        return Response.ok().entity(GSON.toJson(walkers)).build();
    }
}
