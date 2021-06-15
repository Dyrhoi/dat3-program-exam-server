package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.people._public.PublicDogDto;
import facades.DogFacade;
import utils.EMF_Creator;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("dogs")
@RolesAllowed("user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DogsResource {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final DogFacade DOG_FACADE = DogFacade.getInstance(EMF);

    public DogsResource() {
    }

    @GET
    public Response getDogs() {
        List<PublicDogDto> dogs = DOG_FACADE.getAllPublic();
        return Response.ok().entity(GSON.toJson(dogs)).build();
    }

    @GET
    @Path("/{id}")
    public Response getDog(@PathParam("id") long id) {
        PublicDogDto dog = DOG_FACADE.getPublic(id);
        return Response.ok().entity(GSON.toJson(dog)).build();
    }
}
