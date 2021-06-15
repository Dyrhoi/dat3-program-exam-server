package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.people._private.PrivateDogDto;
import dtos.people._private.PrivateOwnerDto;
import dtos.people._private.PrivateWalkerDto;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;
import utils.Populate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdminResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();

        httpServer.shutdownNow();
    }

    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            //Delete existing users and roles to get a "fresh" database
            em.createQuery("delete from Walker").executeUpdate();
            em.createQuery("delete from Dog").executeUpdate();
            em.createQuery("delete from Owner").executeUpdate();
            em.createQuery("delete from User").executeUpdate();
            em.createQuery("delete from Role").executeUpdate();
            //System.out.println("Saved test data to database");
            em.getTransaction().commit();

            new Populate(EMF_Creator.createEntityManagerFactoryForTest()).populateAll();
        } finally {
            em.close();
        }
    }

    //This is how we hold on to the token after login, similar to that a client must store the token somewhere
    private static String securityToken;

    //Utility method to login and set the returned securityToken
    private static void login(String username, String password) {
        String json = String.format("{username: \"%s\", password: \"%s\"}", username, password);
        securityToken = given()
                .contentType("application/json")
                .body(json)
                //.when().post("/api/login")
                .when().post("/authentication/login")
                .then()
                .statusCode(200)
                .extract().path("token");
        //System.out.println("TOKEN ---> " + securityToken);
    }

    private void logOut() {
        securityToken = null;
    }

    @Test
    public void noAccess() {
        login("user", "test");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/admin/users").then()
                .statusCode(401);

        given()
                .contentType("application/json")
                .when()
                .get("/admin/users").then()
                .statusCode(401);
    }

    @Test
    public void users() {
        login("admin", "test");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/admin/users").then()
                .statusCode(200)
                .body("", hasSize(2));
    }

    @Test
    public void createOwner() {
        login("admin", "test");
        PrivateOwnerDto ownerDto = PrivateOwnerDto.builder()
                .name("Ole Tuborg")
                .addressId("0a3f50a0-37af-32b8-e044-0003ba298018")
                .phone("+45 12345678")
                .build();

        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(GSON.toJson(ownerDto))
                //.when().post("/api/login")
                .when().post("/admin/people/owners");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/admin/people/owners").then()
                .statusCode(200)
                .body("", hasSize(2));
    }


    @Test
    public void getAllOwners() {
        login("admin", "test");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/admin/people/owners").then()
                .statusCode(200)
                .body("", hasSize(1))
                .body("[0]", hasKey("addressId"));
    }

    @Test
    public void expectOwnerToHaveAtLeastOneDog() {
        login("admin", "test");
        PrivateOwnerDto[] _temp = given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/admin/people/owners").then()
                .extract().as(PrivateOwnerDto[].class);
        PrivateOwnerDto ownerDto = _temp[0];
        assertTrue(ownerDto.getDogs().size() > 0);
    }

    @Test
    public void expectWalkerToHaveMoreAtLeastOneDog() {
        login("admin", "test");
        PrivateWalkerDto[] _temp = given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/admin/people/walkers").then()
                .extract().as(PrivateWalkerDto[].class);
        PrivateWalkerDto walkerDto = _temp[0];
        assertTrue(walkerDto.getDogs().size() > 0);
    }

    @Test
    public void createWalker() {
        login("admin", "test");
        PrivateWalkerDto walkerDto = PrivateWalkerDto.builder()
                .name("Ole Tuborg")
                .addressId("0a3f50a0-37af-32b8-e044-0003ba298018")
                .phone("+45 12345678")
                .build();

        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(GSON.toJson(walkerDto))
                //.when().post("/api/login")
                .when().post("/admin/people/walkers");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/admin/people/walkers").then()
                .statusCode(200)
                .body("", hasSize(3));
    }


    @Test
    public void getAllWalkers() {
        login("admin", "test");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/admin/people/walkers").then()
                .statusCode(200)
                .body("", hasSize(2));
    }

    @Test
    public void createDog() {
        login("admin", "test");

        // A dog is required an already established Owner entity.
        // A dog can have walkers by default, but not required.
        PrivateOwnerDto[] _temp = given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/admin/people/owners").then()
                .extract().as(PrivateOwnerDto[].class);
        PrivateOwnerDto ownerDto = _temp[0];

        PrivateDogDto privateDogDto = PrivateDogDto.builder()
                .owner(ownerDto)
                .name("Fjeffi")
                .birthdate(1529057111)
                .imageUrl("https://i.imgur.com/xyPtn4m.jpg")
                .breed("Labrador")
                .gender("female")
                .build();

        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(GSON.toJson(privateDogDto))
                //.when().post("/api/login")
                .when().post("/admin/dogs");

        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/admin/dogs").then()
                .statusCode(200)
                .body("", hasSize(3));


    }


    @Test
    public void getAllDogs() {
        login("admin", "test");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/admin/dogs").then()
                .statusCode(200)
                .body("", hasSize(2));
    }

}
