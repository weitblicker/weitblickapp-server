package org.weitblicker.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

import org.weitblicker.AuthenticationFilter;
import org.weitblicker.database.PersistenceHelper;
import org.weitblicker.database.User;
import javax.persistence.NoResultException;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.Serializable;
import java.security.Key;

/**
 * Root resource (exposed at "rest" path)
 */
@Path("rest")
public class AuthRestApi
{

    private ObjectMapper jsonMapper = new ObjectMapper();
    
    @Context
	UriInfo uri;
    
    @POST
    @Path("authentication")
    @Produces("application/json")
    @Consumes("application/x-www-form-urlencoded")
    public Response authenticateUser(@FormParam("email") String email, 
                                     @FormParam("password") String password) {

    	System.out.println("Authentication");
        try {
        	
            // Authenticate the user using the credentials provided
            User user = authenticate(email, password);
        	System.out.println("login user: " + user);

            // Issue a token for the user
            String token = issueToken(user);
            
            // Remember token
            AuthenticationFilter.login(token, user);
            
            // the maximum age of the the cookie in seconds
            int maxAge = AuthenticationFilter.getSession(token).getExpiringTime(); 
            System.out.println("maxAge: " + maxAge);
            
            // the URI path for which the cookie is valid.
            String path = "/"; // every path in the domain
            
            // whether the cookie will only be sent over a secure connection
            boolean secure = false;
            
            String domain = uri.getBaseUri().getHost();
            
            String comment = "";
            
            String cookieName = AuthenticationFilter.TOKEN_NAME;
            
            @SuppressWarnings("serial")
			class AuthenticationResponse implements Serializable{
            	@SuppressWarnings("unused")
				public String token;
            	@SuppressWarnings("unused")
				public String message;
            	public AuthenticationResponse(String token, String message){
            		this.token = token;
            		this.message = message;
            	}
            }
            AuthenticationResponse response = new AuthenticationResponse(token, "Authentication was successful!");
            String jsonResponse = jsonMapper.writeValueAsString(response);
            NewCookie newCookie = new NewCookie(cookieName, token, path, domain, comment, maxAge, secure); 
            return Response.ok(jsonResponse).cookie(newCookie).build();

        } catch(NotAuthorizedException e){
        	System.out.println("login failed: " + e.getLocalizedMessage());
        	return Response.status(Response.Status.UNAUTHORIZED).entity("wrong password!").build();
            
        } catch(NoResultException e) {
        	System.out.println("no user for the given email \"" + email + "\" found!");
            return Response.status(Response.Status.UNAUTHORIZED).entity("no user for the given email \"" + email + "\" found!").build();
        } catch(Exception e){
        	// TODO Error logging
        	e.printStackTrace();
        	return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private User authenticate(String email, String password) throws Exception {
        // Authenticate against a database, LDAP, file or whatever
        // Throw an Exception if the credentials are invalid
    	User user = PersistenceHelper.getUserByEmail(email);
    	if(user == null){
    		throw new NotAuthorizedException("Not authorized, no user for the given email: \""+ email + "\"");
    	}
    	if(!user.equalsPassword(password)){
    		throw new NotAuthorizedException("Not authorized, wrong password for user: " + user);
    	}
		return user;
    }

    private String issueToken(User user) {
        // Issue a token (can be a random String persisted to a database or a JWT token)
        // The issued token must be associated to a user
        // Return the issued token
    	
    	// We need a signing key, so we'll create one just for this example. Usually
    	// the key would be read from your application configuration instead.
    	Key key = MacProvider.generateKey();

    	String compactJws = Jwts.builder()
    	  .setSubject(user.getName())
    	  .signWith(SignatureAlgorithm.HS512, key)
    	  .compact();
    	    	
    	return compactJws;
    }
}
