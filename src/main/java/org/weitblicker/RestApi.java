package org.weitblicker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

import org.weitblicker.database.PersistenceHelper;
import org.weitblicker.database.Project;
import org.weitblicker.database.User;

import javax.persistence.NoResultException;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import java.security.Key;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Root resource (exposed at "rest" path)
 */
@Path("rest")
public class RestApi
{
    private ObjectMapper jsonMapper = new ObjectMapper();

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Path("projekt/liste")
    @Produces("application/json")
    public String getProjectIds() {

        try
        {
            List<Long> liste = PersistenceHelper.getProjectList();
            return jsonMapper.writeValueAsString(liste);
        } catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    @GET
    @Secured
    @Path("projekt/{id}")
    @Produces("application/json")
    public String getProject(@PathParam("id") final String id, @Context SecurityContext securityContext)
    {
        try
        {
        	// TODO use these examples
        	// get the user
        	User user = (User) securityContext.getUserPrincipal();
        	
        	// if in a specific role 
        	boolean inRole = securityContext.isUserInRole("some role");
        	
            Long projectId = Long.valueOf(id);
            Project project = PersistenceHelper.getProject(projectId);
            return jsonMapper.writeValueAsString(project);
        } catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }
        return "";
    }
    
    @POST
    @Path("authentication")
    @Produces("application/json")
    @Consumes("application/x-www-form-urlencoded")
    public Response authenticateUser(@FormParam("username") String username, 
                                     @FormParam("password") String password) {

    	System.out.println("Authentication");
        try {

            // Authenticate the user using the credentials provided
            User user = authenticate(username, password);
        	System.out.println("login user: " + user);

            // Issue a token for the user
            String token = issueToken(user);
            
            // Remember token
            AuthenticationFilter.login(token, user);
            
            // Return the token on the response
            return Response.ok(token).build();

        } catch(NotAuthorizedException e){
        	System.out.println("login failed: " + e.getLocalizedMessage());
        	return Response.status(Response.Status.UNAUTHORIZED).entity("wrong password!").build();
            
        } catch(NoResultException e) {
        	System.out.println("no user for the given name \"" + username + "\" found!");
            return Response.status(Response.Status.UNAUTHORIZED).entity("no user for the given name \"" + username + "\" found!").build();
        } catch(Exception e){
        	// TODO Error logging
        	e.printStackTrace();
        	return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    private User authenticate(String username, String password) throws Exception {
        // Authenticate against a database, LDAP, file or whatever
        // Throw an Exception if the credentials are invalid
    	User user = PersistenceHelper.getUserByName(username);
    	if(!user.getPassword().equals(password)){
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
