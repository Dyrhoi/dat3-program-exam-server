package security;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import dtos.user.PrivateUserDto;
import facades.UserFacade;
import java.util.Date;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import security.errorhandling.AuthenticationException;

import javax.persistence.EntityManagerFactory;
import utils.EMF_Creator;

@Path("authentication")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthenticationEndpoint {

    public static final int TOKEN_EXPIRE_TIME = 1000 * 60 * 30; //30 min
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    public static final UserFacade USER_FACADE = UserFacade.getInstance(EMF);

    @POST
    @Path("login")
    public Response login(String jsonString) {
        try {
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            String username = json.get("username").getAsString();
            String password = json.get("password").getAsString();

            PrivateUserDto user = USER_FACADE.login(username, password);

            return createLoginResponse(user);
        } catch (JsonSyntaxException e) {
           throw new WebApplicationException("Bad request, expected: username, password", 400);
        } catch (AuthenticationException e) {
          throw new WebApplicationException("Username and password do not match.", 401);
        }
    }

    @POST
    @Path("register")
    public Response register(String jsonString) {
        try {
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            String username = json.get("username").getAsString();
            String password = json.get("password").getAsString();
            String passwordConfirm = json.get("passwordConfirm").getAsString();

            PrivateUserDto user = USER_FACADE.register(username, password, passwordConfirm);

            return createLoginResponse(user);
        } catch (JsonSyntaxException e) {
            throw new WebApplicationException("Bad request, expected: username, password, passwordConfirm", 400);
        }
    }


    private Response createLoginResponse(PrivateUserDto user) {
        try {
            String token = createToken(user.getUsername(), user.getRolesAsStrings());
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("username", user.getUsername());
            responseJson.addProperty("token", token);
            return Response.ok(new Gson().toJson(responseJson)).build();
        } catch (JOSEException ex) {
            throw new WebApplicationException("User could not be authorized.", 401);
        }
    }

    private String createToken(String userName, List<String> roles) throws JOSEException {

        StringBuilder res = new StringBuilder();
        for (String string : roles) {
            res.append(string);
            res.append(",");
        }
        String rolesAsString = res.length() > 0 ? res.substring(0, res.length() - 1) : "";
        String issuer = "semesterstartcode-dat3";

        JWSSigner signer = new MACSigner(SharedSecret.getSharedKey());
        Date date = new Date();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(userName)
                .claim("username", userName)
                .claim("roles", rolesAsString)
                .claim("issuer", issuer)
                .issueTime(date)
                .expirationTime(new Date(date.getTime() + TOKEN_EXPIRE_TIME))
                .build();
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        signedJWT.sign(signer);
        return signedJWT.serialize();

    }
}
