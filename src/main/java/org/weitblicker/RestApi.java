package org.weitblicker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

import org.weitblicker.database.PersistenceHelper;
import org.weitblicker.database.Project;
import org.weitblicker.database.User;
import org.weitblicker.database.Location;

import javax.persistence.NoResultException;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import java.io.IOException;
import java.security.Key;
import java.util.HashSet;
import java.util.IllformedLocaleException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Root resource (exposed at "rest" path)
 */
@Path("rest")
public class RestApi
{
    public static Locale getLanguage(String language) throws IllformedLocaleException{
    	Locale.Builder languageBuilder = new Locale.Builder();
		languageBuilder.setLanguage(language);
		return languageBuilder.build();
    }
	
    private ObjectMapper jsonMapper = new ObjectMapper();

    @GET
    @Path("location/list")
    @Produces(MediaType.APPLICATION_JSON)
    public String getLocations() {
        try
        {
            List<Location> liste = PersistenceHelper.getAllLocations();
            return jsonMapper.writeValueAsString(liste);
        } catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }
        return "";
    }
    
    @GET
    @Path("project/list{noop: (/)?}{language : ((?<=/)\\w+)?}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectIds(@PathParam("language") final String language) {

    	// TODO put this to the config 
    	Set<Locale> languages = new HashSet();
    	languages.add(Locale.GERMAN);
    	languages.add(Locale.ENGLISH);
    	languages.add(Locale.FRENCH);
    	languages.add(Options.DEFAULT_LANGUAGE);
    	
        try
        {
        	Locale currentLanguage = Options.DEFAULT_LANGUAGE;
        	System.out.println("given language: \"" + language + "\"");
        	if(language == null || language.equals("")){
        		System.out.println("no language specified! - return with default language");
        	}else{        	
	        	// set response language
	        	currentLanguage = getLanguage(language);
	        	System.out.println("language: " + currentLanguage.getLanguage());
	        	
	        	// check if language is allowed
	        	if(! languages.contains(currentLanguage)){
	            	System.out.println("language: " + currentLanguage.getLanguage() + " ist not allowed!");
	            	return Response.status(Response.Status.BAD_REQUEST).build();
	        	}
        	}
        	
            List<Project> list = PersistenceHelper.getAllProjects();
            for(Project project : list){
            	project.setCurrentLanguage(currentLanguage);
            }

            // response ok with json content
            return Response.ok(jsonMapper.writeValueAsString(list)).build();
        } catch(IllformedLocaleException e){
        	e.printStackTrace();
        	return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (JsonProcessingException e)
        {
            e.printStackTrace();
        	return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("project/{id}/{language}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProject(@PathParam("id") final String id, @PathParam("language") final String language, @Context SecurityContext securityContext)
    {
        try
        {
        	Locale currentLanguage = getLanguage(language);
        	
        	// TODO use these examples
        	// get the user
        	User user = (User) securityContext.getUserPrincipal();
        	
        	// if in a specific role 
        	boolean inRole = securityContext.isUserInRole("some role");
        	
            Long projectId = Long.valueOf(id);
            Project project = PersistenceHelper.getProject(projectId);
            project.setCurrentLanguage(currentLanguage);
            return Response.ok(jsonMapper.writeValueAsString(project)).build();
        } catch(IllformedLocaleException e){
        	e.printStackTrace();
        	return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (JsonProcessingException e)
        {
            e.printStackTrace();
        	return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
	
	@POST
	@Path("project/new")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createProject(@QueryParam("language") String language, String jsonProject){
		
		System.out.println("create new project...");
		
		Locale currentLanguage = Options.DEFAULT_LANGUAGE;
		
		// read language
		if(language != null){
			Locale.Builder languageBuilder = new Locale.Builder();
			try{
				languageBuilder.setLanguage(language);
				currentLanguage = languageBuilder.build();
			}catch(IllformedLocaleException e){
				// TODO maybe response bad request
				e.printStackTrace();
			}
		}
		
		// set current language
		Project project = new Project();
		project.setCurrentLanguage(currentLanguage);
		System.out.println("language: " + currentLanguage.getLanguage());
		System.out.println("json:" + jsonProject);
		
		// parse json 
		try {
			project = jsonMapper.readerForUpdating(project).readValue(jsonProject);
			System.out.println(project);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// set location by id - the location has to be exist already.
		Location location = project.getLocation();
		
		if(location == null || location.getId() == null){
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		location = PersistenceHelper.getLocation(location.getId());
		project.setLocation(location);
		
		System.out.println("Project: " + project);
		System.out.println("Location: " + project.getLocation());
		if(project.getId() == null){
			System.out.println("no id given, project is new...");
			long id = PersistenceHelper.createOrUpdateProject(project);
			System.out.println("created new project with id: " + id);
			try {
				String jsonResponse = jsonMapper.writeValueAsString(project);
				return Response.ok(jsonResponse).build();
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
			}
		}else{
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
	}
	
	@POST
	@Path("project/update/{language}")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateProject(@PathParam("language") final String language, String jsonProject){
		
		System.out.println("update project...");

		try{		
			Locale currentLanguage = getLanguage(language);
			System.out.println("language: " + currentLanguage.getLanguage());
		
			Project receivedProject = jsonMapper.readValue(jsonProject, Project.class);
			Long id = receivedProject.getId();
			
			// no id given -> bad request
			if(id == null) 
				return Response.status(Response.Status.BAD_REQUEST).build();
			
			// get project from database
			Project dbProject = PersistenceHelper.getProject(id);
			
			// set current language
			dbProject.setCurrentLanguage(currentLanguage);
			
			// merge project with changes
			dbProject = jsonMapper.readerForUpdating(dbProject).readValue(jsonProject);

			// TODO Fix that.. does not work... why?
			// set location by id - the location has to be exist already.
			Long locationId = dbProject.getLocation().getId();
			if(locationId == null)
				return Response.status(Response.Status.BAD_REQUEST).build();
			
			// get location object by id
			Location location = PersistenceHelper.getLocation(locationId);
			dbProject.setLocation(location);

			// update project in database
			System.out.println("Project: " + dbProject);
			System.out.println("Location: " + dbProject.getLocation());
			id = PersistenceHelper.createOrUpdateProject(dbProject);
			System.out.println("updated project with id: " + id);
			
			// return the updated project as json
			String jsonResponse = jsonMapper.writeValueAsString(dbProject);
			return Response.ok(jsonResponse).build();
			
		} catch(IllformedLocaleException e){
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).build();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		} 
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
