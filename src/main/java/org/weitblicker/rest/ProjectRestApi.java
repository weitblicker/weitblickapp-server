package org.weitblicker.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.IllformedLocaleException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.apache.tika.Tika;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.weitblicker.Options;
import org.weitblicker.Secured;
import org.weitblicker.Utility;
import org.weitblicker.database.Host;
import org.weitblicker.database.Image;
import org.weitblicker.database.Location;
import org.weitblicker.database.PersistenceHelper;
import org.weitblicker.database.Project;
import org.weitblicker.database.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("rest/project")
public class ProjectRestApi {
	
    @Context
	UriInfo uri;
	
    private ObjectMapper jsonMapper = new ObjectMapper();
	
    @GET
    @Path("list{noop: (/)?}{language : ((?<=/)\\w+)?}")
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
	        	currentLanguage = Utility.getLanguage(language);
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
    @Path("{id}/{language}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProject(@PathParam("id") final String id, @PathParam("language") final String language, @Context SecurityContext securityContext)
    {
        try
        {
        	Locale currentLanguage = Utility.getLanguage(language);
        	
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


	@Secured
	@DELETE
	@Path("remove/{id}")
	public Response removeProject(@PathParam("id") final Long id){
		EntityManager em =PersistenceHelper.getPersistenceManager().getEntityManager();
		em.getTransaction().begin();
		try{
			System.out.println("trying to remove project with the id: " + id);
			Project project = em.find(Project.class, id);
			if(project == null){
				System.out.println("No project for the given id \"" + id + "\".");
				return Response.status(Status.BAD_REQUEST).build();
			}
			
			System.out.println("remove project: " + project);

			em.remove(project);
		} catch(Exception e){
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		em.getTransaction().commit();
		em.close();
		
		return Response.ok().build();
	}

	@Secured
	@GET
	@Path("{projectId}/remove-image/{imageId}")
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


	@Secured
	@POST
	@Path("{id}/upload-image")
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
	@Path("{id}/image/{file}")
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


	@Secured
	@POST
	@Path("new")
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


	@Secured
	@PUT
	@Path("update/{language}")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateProject(@PathParam("language") final String language, String jsonProject){
		
		System.out.println("update project...");

		try{		
			Locale currentLanguage = Utility.getLanguage(language);
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
			
			for(Host host: dbProject.getHosts()){
				em = PersistenceHelper.getPersistenceManager().getEntityManager();
				em.getTransaction().begin();
				em.merge(host);
				em.getTransaction().commit();
				em.close();
			}
			
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
	
}
