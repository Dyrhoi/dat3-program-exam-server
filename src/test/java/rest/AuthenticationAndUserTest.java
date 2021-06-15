package rest;

import com.google.gson.Gson;
import dtos.user.PrivateUserDto;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import java.net.URI;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;
import utils.Populate;

public class AuthenticationAndUserTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";

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
            em.createQuery("delete from User").executeUpdate();
            em.createQuery("delete from Role").executeUpdate();
            //System.out.println("Saved test data to database");
            em.getTransaction().commit();

            new Populate(EMF_Creator.createEntityManagerFactoryForTest()).populateUsers();
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
    public void serverIsRunning() {
        given().when().get("/info").then().statusCode(200);
    }

    @Test
    public void userNotFoundLogin() {
        String json = String.format("{username: \"%s\", password: \"%s\"}", "non_existing_user", "123");
        given()
                .contentType("application/json")
                .body(json)
                //.when().post("/api/login")
                .when().post("/authentication/login")
                .then()
                .statusCode(401)
                .body("message", equalTo("Username and password do not match."));
    }

    @Test
    public void register() {
        String jsonRegister = String.format("{username: \"%s\", password: \"%s\", passwordConfirm: \"%s\"}", "new_user", "test", "test");
        given()
                .contentType("application/json")
                .body(jsonRegister)
                .when().post("/authentication/register")
                .then()
                .statusCode(200);

        login("new_user", "test");
    }

    @Test
    public void registerUserAlreadyExists() {
        String jsonRegister = String.format("{username: \"%s\", password: \"%s\"}, passwordConfirm: \"%s\"}", "user", "test", "test");
        given()
                .contentType("application/json")
                .body(jsonRegister)
                .when().post("/authentication/register")
                .then()
                .statusCode(400);
    }

    @Test
    public void registerNonMatchingPasswords() {
        String jsonRegister = String.format("{username: \"%s\", password: \"%s\"}, passwordConfirm: \"%s\"}", "new_user_7", "test", "tests");
        given()
                .contentType("application/json")
                .body(jsonRegister)
                .when().post("/authentication/register")
                .then()
                .statusCode(400);
    }

    @Test
    public void incorrectPasswordLogin() {
        String json = String.format("{username: \"%s\", password: \"%s\"}", "admin", "test123");
        given()
                .contentType("application/json")
                .body(json)
                //.when().post("/api/login")
                .when().post("/authentication/login")
                .then()
                .statusCode(401)
                .body("message", equalTo("Username and password do not match."));
    }

    @Test
    public void testRestNoAuthenticationRequired() {
        given()
                .contentType("application/json")
                .when()
                .get("/info/").then()
                .statusCode(200)
                .body("msg", equalTo("Hello anonymous"));
    }

    @Test
    public void testRestForAdmin() {
        login("admin", "test");
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get("/info/admin").then()
                .statusCode(200)
                .body("msg", equalTo("Hello to (admin) User: admin"));
    }

    @Test
    public void testRestForUser() {
        login("user", "test");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/info/user").then()
                .statusCode(200)
                .body("msg", equalTo("Hello to User: user"));
    }

    @Test
    public void testAutorizedUserCannotAccesAdminPage() {
        login("user", "test");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/info/admin").then() //Call Admin endpoint as user
                .statusCode(401);
    }

    @Test
    public void testRestForMultiRole1() {
        login("admin", "test");
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get("/info/admin").then()
                .statusCode(200)
                .body("msg", equalTo("Hello to (admin) User: admin"));
    }

    @Test
    public void testRestForMultiRole2() {
        login("admin", "test");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/info/user").then()
                .statusCode(200)
                .body("msg", equalTo("Hello to User: admin"));
    }

    @Test
    public void userNotAuthenticated() {
        logOut();
        given()
                .contentType("application/json")
                .when()
                .get("/info/user").then()
                .statusCode(401)
                .body("code", equalTo(401))
                .body("message", equalTo("Not authenticated - do login"));
    }

    @Test
    public void adminNotAuthenticated() {
        logOut();
        given()
                .contentType("application/json")
                .when()
                .get("/info/user").then()
                .statusCode(401)
                .body("code", equalTo(401))
                .body("message", equalTo("Not authenticated - do login"));
    }

    @Test
    public void me() {
        login("admin", "test");
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get("/me").then()
                .statusCode(200)
                .body("username", equalTo("admin"));

        // Given an invalid token...
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", "invalidToken")
                .when()
                .get("/me").then()
                .statusCode(401);

        // Given no token...
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .when()
                .get("/me").then()
                .statusCode(401);
    }

    @Test
    public void updateUser() {
        PrivateUserDto updatedUser = PrivateUserDto.builder()
                .username("user")

                .displayName("A cool new display name")
                .build();

        login("user", "test");

        // See who I am (from me resource).
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/me").then()
                .statusCode(200)
                .body("displayName", equalTo(null))
                .body("updatedAt", equalTo(null))
                .body("createdAt", notNullValue());

        // Update me.
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .body(new Gson().toJson(updatedUser))
                .put("/user").then()
                .statusCode(200)
                .body("displayName", equalTo("A cool new display name"));

        // See who I am again (from me resource).
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/me").then()
                .statusCode(200)
                .body("displayName", equalTo("A cool new display name"))
                .body("updatedAt", notNullValue())
                .body("createdAt", notNullValue());

    }

}
