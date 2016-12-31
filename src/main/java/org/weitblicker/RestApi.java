package org.weitblicker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

import org.weitblicker.database.PersistenceHelper;
import org.weitblicker.database.PersistenceManager;
import org.weitblicker.database.Project;
import org.weitblicker.database.Image;
import org.weitblicker.database.User;
import org.apache.tika.Tika;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.weitblicker.database.Location;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.Key;
import java.util.HashSet;
import java.util.IllformedLocaleException;
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
    
    @Context
	UriInfo uri;
    

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
    
	@POST
	@Path("location/new")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createLocation(@QueryParam("language") String language, String jsonLocation){
		
		System.out.println("create new location...");
		
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
		Location location = new Location();
		location.setCurrentLanguage(currentLanguage);
		System.out.println("language: " + currentLanguage.getLanguage());
		System.out.println("json:" + jsonLocation);
		
		// parse json 
		try {
			location = jsonMapper.readerForUpdating(location).readValue(jsonLocation);
			System.out.println(location);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(location.getId() == null){
			System.out.println("no id given, location is new...");
			try{
				long id = PersistenceHelper.createOrUpdateLocation(location);
				System.out.println("created new location with id: " + id);

			}catch(Exception e){
				e.printStackTrace();
			}
			try {
				String jsonResponse = jsonMapper.writeValueAsString(location);
				return Response.ok(jsonResponse).build();
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
			}
		}else{
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
	}
	
	@PUT
	@Path("location/update/{language}")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateLocation(@PathParam("language") final String language, String jsonLocation){
		
		System.out.println("update location...");

		try{		
			Locale currentLanguage = getLanguage(language);
			System.out.println("language: " + currentLanguage.getLanguage());
		
			Location receivedLocation = jsonMapper.readValue(jsonLocation, Location.class);
			Long id = receivedLocation.getId();
			
			// no id given -> bad request
			if(id == null) 
				return Response.status(Response.Status.BAD_REQUEST).build();
			
			// get project from database
			EntityManager em = PersistenceHelper.getPersistenceManager().getEntityManager();
			em.getTransaction().begin();
			Location dbLocation = em.find(Location.class, id);
			
			// set current language
			dbLocation.setCurrentLanguage(currentLanguage);
			
			// merge project with changes
			dbLocation = jsonMapper.readerForUpdating(dbLocation).readValue(jsonLocation);

		
			// update location in database
			System.out.println("Location: " + dbLocation);
			em.persist(dbLocation);
			em.getTransaction().commit();
			em.close();
			System.out.println("updated location with id: " + dbLocation.getId());
			
			// return the updated project as json
			String jsonResponse = jsonMapper.writeValueAsString(dbLocation);
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

    
    @GET
    @Path("project/list{noop: (/)?}{language : ((?<=/)\\w+)?}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjects(@PathParam("language") final String language) {

    	// TODO put this to the config 
    	Set<Locale> languages = new HashSet<Locale>();
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
	
	@DELETE
	@Path("project/remove/{id}")
	public Response removeProject(@PathParam("id") final Long id){
		EntityManager em =PersistenceHelper.getPersistenceManager().getEntityManager();
		em.getTransaction().begin();
		try{
			System.out.println("trying to remove project with the id: " + id);
			Project project = em.find(Project.class, id);
			System.out.println("remove project: " + project);

			em.remove(project);
		} catch(Exception e){
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		em.getTransaction().commit();
		em.close();
		
		return Response.ok().build();
	}
	
	@GET
	@Path("project/{projectId}/remove-image/{imageId}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response removeProjectImage(
			@PathParam("projectId") final Long projectId,
			@PathParam("imageId") final Long imageId){
		System.out.println("called delete action for image id:\""+imageId+"\" of the project:\""+projectId+"\"");
		EntityManager em =PersistenceHelper.getPersistenceManager().getEntityManager();
		em.getTransaction().begin();
		try{
			System.out.println("trying to remove project image with the id: " +
					imageId + " of the project with the id: " + projectId);
			Project project = em.find(Project.class, projectId);
			Image image = project.removeImage(imageId);
			if(image == null){
				throw new IllegalArgumentException("The image id \"" + imageId 
					+ "\" is not referenced in the project with the id \"" + projectId + "\"!");
			}
			image.getFile().delete();
			em.remove(image);
			System.out.println("remove project image: " + image);

			em.persist(project);
			System.out.println("update project: " + project);
		} catch(Exception e){
			e.printStackTrace();
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();
			}
			em.close();
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		em.getTransaction().commit();
		em.close();
		System.out.println("removed project image succefully!");
		
		String ret;
		try {
			ret = jsonMapper.writeValueAsString("removed project image succefully!");
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.ok(ret).build();
	}	
	
	    
	@POST
	@Path("/project/{id}/upload-image")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadFile(
		@PathParam("id") Long id,
	    @FormDataParam("file") InputStream uploadedInputStream,
	    @FormDataParam("file") FormDataContentDisposition fileMetaData){
	
		if(id == null || uploadedInputStream == null || fileMetaData == null)
			return Response.status(Response.Status.BAD_REQUEST).build();
		
		System.out.println("upload file with filename \"" 
				+ fileMetaData.getFileName() + "\" for project \"" + id + "\".");
		
		String uploadFileLocation = "project-images/"+ id + "/" + fileMetaData.getFileName();
		
		// two dots are not allowed, because of path traversal attacks
		if(uploadFileLocation.contains("..")){
			System.out.println("Detected traversal attack! See path: \"" + uploadFileLocation + "\"!");
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		else{
			System.out.println("target path is: \""+ uploadFileLocation + "\".");
		}
		
		try {
			File file = new File(uploadFileLocation);
			if(file.exists()){
				System.out.println("The file already exists! See path: \"" + file.getPath() + "\"!");
				return Response.status(Response.Status.BAD_REQUEST).build();			
			}
			
			File parent = file.getParentFile();
			if(!parent.exists()){
				if(! parent.mkdir()){
					// TODO log and print as error!
					System.out.println("Can not make directories!");
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
				}
			}
			saveToFile(uploadedInputStream, file);

			EntityManager em =PersistenceHelper.getPersistenceManager().getEntityManager();
			em.getTransaction().begin();
			Image image = new Image(file);
			image.setName(fileMetaData.getFileName());
			
			
			image.setUri( uri.getBaseUri().toString() + "rest/project/" + id + "/image/" + image.getName());
			Project project = em.find(Project.class, id);
			project.addImage(image);
			em.persist(image);
			em.persist(project);
			em.getTransaction().commit();
			//Long imageId = image.getId();
			
			// build response
			ObjectMapper jsonMapper = new ObjectMapper();
			//String output = "File uploaded via Jersey based RESTFul Webservice to: " + uploadedFileLocation;
		    String json = jsonMapper.writeValueAsString(image);
	        return Response.ok(json).build();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		} catch (Exception e){
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
    // save uploaded file to new location
    private void saveToFile(InputStream uploadedInputStream,
        File file) throws IOException{

        OutputStream out = null;
        int read = 0;
        byte[] bytes = new byte[1024];
        
        out = new FileOutputStream(file);
        while ((read = uploadedInputStream.read(bytes)) != -1) {
            out.write(bytes, 0, read);
        }
        out.flush();
        out.close();
    }
    
    
	@GET
	@Path("project/{id}/image/{file}")
	@Produces("*/*")
	public Response getFile(@PathParam("file") String fileName,
			@PathParam("id") String id){
		if (fileName == null) {
			System.err.println("No such item");
				return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		if (fileName.contains("..")){
			// illegal folder traversal 
			Response.status(Status.BAD_REQUEST).build();
		}
		
		File file = new File("project-images/" + id + "/" + fileName);
		
		if(!file.exists()){
			System.out.println("file does not exist!");
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
				
		try {
			Tika tika = new Tika();
			String mimeType = tika.detect(file);
			return Response.ok(file, mimeType).build();
		} catch (Exception e){
			e.printStackTrace();
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
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
			try{
				long id = PersistenceHelper.createOrUpdateProject(project);
				System.out.println("created new project with id: " + id);

			}catch(Exception e){
				e.printStackTrace();
			}
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
	
	@PUT
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
			EntityManager em = PersistenceHelper.getPersistenceManager().getEntityManager();
			em.getTransaction().begin();
			Project dbProject = em.find(Project.class, id);
			
			// set current language
			dbProject.setCurrentLanguage(currentLanguage);
			
			// merge project with changes
			dbProject = jsonMapper.readerForUpdating(dbProject).readValue(jsonProject);

			// set location by id - the location has to be exist already.
			Long locationId = dbProject.getLocation().getId();
			if(locationId == null)
				return Response.status(Response.Status.BAD_REQUEST).build();
			
			// get location object by id
			Location location = em.find(Location.class, locationId);
			dbProject.setLocation(location);

			// update project in database
			System.out.println("Project: " + dbProject);
			System.out.println("Location: " + dbProject.getLocation());
			em.persist(dbProject);
			em.getTransaction().commit();
			em.close();
			System.out.println("updated project with id: " + dbProject.getId());
			
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
            
            class AuthenticationResponse implements Serializable{
            	public String token;
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
