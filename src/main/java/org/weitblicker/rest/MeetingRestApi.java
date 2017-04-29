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
import org.weitblicker.UserRole;
import org.weitblicker.Utility;
import org.weitblicker.database.Image;
import org.weitblicker.database.Location;
import org.weitblicker.database.Meeting;
import org.weitblicker.database.PersistenceHelper;
import org.weitblicker.database.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("rest/meeting")
public class MeetingRestApi {
	
    @Context
	UriInfo uri;
	
    private ObjectMapper jsonMapper = new ObjectMapper();
	
    @GET
    @Path("list{noop: (/)?}{language : ((?<=/)\\w+)?}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMeetings(@PathParam("language") final String language) {

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
            List<Meeting> meetings = PersistenceHelper.getMeetings();
            for(Meeting meeting : meetings){
            	meeting.setCurrentLanguage(currentLanguage);
            }

            // response ok with json content
            return Response.ok(jsonMapper.writeValueAsString(meetings)).build();
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
    public Response getMeeting(@PathParam("id") final String id, @PathParam("language") final String language, @Context SecurityContext securityContext)
    {
        try
        {
        	Locale currentLanguage = Utility.getLanguage(language);
        	
        	// TODO use these examples
        	// get the user
        	User user = (User) securityContext.getUserPrincipal();
        	
        	// if in a specific role 
        	boolean inRole = securityContext.isUserInRole("some role");
        	
            Long meetingId = Long.valueOf(id);
            Meeting meeting = PersistenceHelper.getMeeting(meetingId);
            meeting.setCurrentLanguage(currentLanguage);
            return Response.ok(jsonMapper.writeValueAsString(meeting)).build();
        } catch(IllformedLocaleException e){
        	e.printStackTrace();
        	return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (JsonProcessingException e)
        {
            e.printStackTrace();
        	return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

	@Secured({UserRole.admin, UserRole.maintainer})
	@POST
	@Path("remove/{id}")
	public Response removeMeeting(@PathParam("id") final Long id){
		EntityManager em =PersistenceHelper.getPersistenceManager().getEntityManager();
		em.getTransaction().begin();
		try{
			System.out.println("trying to remove meeting with the id: " + id);
			Meeting meeting = em.find(Meeting.class, id);
			if(meeting == null){
				System.out.println("No meeting for the given id \"" + id + "\".");
				return Response.status(Status.BAD_REQUEST).build();
			}
			
			System.out.println("remove meeting: " + meeting);

			em.remove(meeting);
		} catch(Exception e){
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		em.getTransaction().commit();
		em.close();
		
		return Response.ok().build();
	}

	@Secured({UserRole.admin, UserRole.maintainer})
	@POST
	@Path("clone/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response cloneMeeting(@PathParam("id") final Long id){
		
		try{
			EntityManager em =PersistenceHelper.getPersistenceManager().getEntityManager();
			em.getTransaction().begin();
			System.out.println("trying to clone meeting with the id: " + id);
			
			Meeting meeting_old = em.find(Meeting.class, id);
			em.detach(meeting_old);
			meeting_old.resetId();

			if(meeting_old.getId() == null){
				System.out.println("no id given, meeting is new...");
				try{
								//em.persist(meeting_old);
			
					long id_new = PersistenceHelper.createOrUpdateMeeting(meeting_old);
					System.out.println("created new meeting with id: " + id_new);

				}catch(Exception e){
					e.printStackTrace();
				}
				
				try {
					String jsonResponse = jsonMapper.writeValueAsString(meeting_old);
					em.getTransaction().commit();
					em.close();
					System.out.println("Cloned...");
					return Response.ok(jsonResponse).build();
				} catch (JsonProcessingException e) {
					em.close();
					e.printStackTrace();
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
				}
			}else{
				return Response.status(Response.Status.BAD_REQUEST).build();
			}


		} catch(Exception e){
			
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}

	}


	@Secured({UserRole.admin, UserRole.maintainer})
	@POST
	@Path("{meetingId}/remove-image/{imageId}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response removeMeetingImage(
			@PathParam("meetingId") final Long meetingId,
			@PathParam("imageId") final Long imageId){
		System.out.println("called delete action for image id:\""+imageId+"\" of the meeting:\""+meetingId+"\"");
		EntityManager em =PersistenceHelper.getPersistenceManager().getEntityManager();
		em.getTransaction().begin();
		try{
			System.out.println("trying to remove meeting image with the id: " +
					imageId + " of the meeting with the id: " + meetingId);
			Meeting meeting = em.find(Meeting.class, meetingId);
			Image image = meeting.removeImage(imageId);
			if(image == null){
				throw new IllegalArgumentException("The image id \"" + imageId 
					+ "\" is not referenced in the meeting with the id \"" + meetingId + "\"!");
			}
			image.getFile().delete();
			em.remove(image);
			System.out.println("remove meeting image: " + image);

			em.persist(meeting);
			System.out.println("update meeting: " + meeting);
		} catch(Exception e){
			// TODO is this necessary? if yes -> should do this also at the other places
			e.printStackTrace();
			if(em.getTransaction().isActive()){
				em.getTransaction().rollback();
			}
			em.close();
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		em.getTransaction().commit();
		em.close();
		System.out.println("removed meeting image succefully!");
		
		String ret;
		try {
			ret = jsonMapper.writeValueAsString("removed meeting image succefully!");
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.ok(ret).build();
	}

	@Secured({UserRole.admin, UserRole.maintainer})
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
				+ fileMetaData.getFileName() + "\" for meeting \"" + id + "\".");
		
		String uploadFileLocation = "meeting-images/"+ id + "/" + fileMetaData.getFileName();
		
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
			
			
			image.setUri( uri.getBaseUri().toString() + "rest/meeting/" + id + "/image/" + image.getName());
			Meeting meeting = em.find(Meeting.class, id);
			meeting.addImage(image);
			em.persist(image);
			em.persist(meeting);
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
		
		File file = new File("meeting-images/" + id + "/" + fileName);
		
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


	@Secured({UserRole.admin, UserRole.maintainer})
	@POST
	@Path("new")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createMeeting(@QueryParam("language") String language, String jsonMeeting){
		
		System.out.println("create new meeting...");
		
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
		Meeting meeting = new Meeting();
		meeting.setCurrentLanguage(currentLanguage);
		System.out.println("language: " + currentLanguage.getLanguage());
		System.out.println("json:" + jsonMeeting);
		
		// parse json 
		try {
			meeting = jsonMapper.readerForUpdating(meeting).readValue(jsonMeeting);
			System.out.println(meeting);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// set location by id - the location has to be exist already.
		Location location = meeting.getLocation();
		
		if(location == null || location.getId() == null){
			System.out.println("location is not defined!");
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		location = PersistenceHelper.getLocation(location.getId());
		meeting.setLocation(location);
		
		System.out.println("Meeting: " + meeting);
		System.out.println("Location: " + meeting.getLocation());
		if(meeting.getId() == null){
			System.out.println("no id given, meeting is new...");
			try{
				long id = PersistenceHelper.createOrUpdateMeeting(meeting);
				System.out.println("created new meeting with id: " + id);

			}catch(Exception e){
				e.printStackTrace();
			}
			try {
				String jsonResponse = jsonMapper.writeValueAsString(meeting);
				return Response.ok(jsonResponse).build();
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
			}
		}else{
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
	}

	@Secured({UserRole.admin, UserRole.maintainer})
	@POST
	@Path("update/{language}")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateMeeting(@PathParam("language") final String language, String jsonMeeting){
		
		System.out.println("update meeting...");

		try{		
			Locale currentLanguage = Utility.getLanguage(language);
			System.out.println("language: " + currentLanguage.getLanguage());
		
			Meeting receivedMeeting = jsonMapper.readValue(jsonMeeting, Meeting.class);
			Long id = receivedMeeting.getId();
			
			// no id given -> bad request
			if(id == null) 
				return Response.status(Response.Status.BAD_REQUEST).build();
			
			// get meeting from database
			EntityManager em = PersistenceHelper.getPersistenceManager().getEntityManager();
			em.getTransaction().begin();
			Meeting dbMeeting = em.find(Meeting.class, id);
			
			// set current language
			dbMeeting.setCurrentLanguage(currentLanguage);
			
			// merge meeting with changes
			dbMeeting = jsonMapper.readerForUpdating(dbMeeting).readValue(jsonMeeting);

			// set location by id - the location has to be exist already.
			Long locationId = dbMeeting.getLocation().getId();
			if(locationId == null)
				return Response.status(Response.Status.BAD_REQUEST).build();
			
			// get location object by id
			Location location = em.find(Location.class, locationId);
			dbMeeting.setLocation(location);

			// update meeting in database
			System.out.println("Meeting: " + dbMeeting);
			System.out.println("Location: " + dbMeeting.getLocation());
			em.persist(dbMeeting);
			em.getTransaction().commit();
			em.close();
			System.out.println("updated meeting with id: " + dbMeeting.getId());
			
			// return the updated meeting as json
			String jsonResponse = jsonMapper.writeValueAsString(dbMeeting);
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
