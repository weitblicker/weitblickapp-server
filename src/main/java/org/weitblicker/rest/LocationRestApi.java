package org.weitblicker.rest;

import java.io.IOException;
import java.util.IllformedLocaleException;
import java.util.List;
import java.util.Locale;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.weitblicker.Options;
import org.weitblicker.Secured;
import org.weitblicker.UserRole;
import org.weitblicker.Utility;
import org.weitblicker.database.Location;
import org.weitblicker.database.PersistenceHelper;
import org.weitblicker.database.Project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("rest/location")
public class LocationRestApi {
	
    private ObjectMapper jsonMapper = new ObjectMapper();

    
    @GET
    @Path("list")
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

	@Secured({UserRole.admin, UserRole.maintainer})
	@POST
	@Path("new")
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

	@Secured({UserRole.admin, UserRole.maintainer})
	@PUT
	@Path("update/{language}")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateLocation(@PathParam("language") final String language, String jsonLocation){
		
		System.out.println("update location...");

		try{		
			Locale currentLanguage = Utility.getLanguage(language);
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
			
			if(dbLocation == null){
				System.out.println("Can not find a location for the given id \"" + id + "\"!");
				return Response.status(Status.BAD_REQUEST).build();
			}
			
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
		} catch(Exception e){
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	@Secured({UserRole.admin, UserRole.maintainer})
	@DELETE
	@Path("remove/{id}")
	public Response removeLocation(@PathParam("id") final Long id){
		
		EntityManager em =PersistenceHelper.getPersistenceManager().getEntityManager();
		try{
			List<Project> projects = PersistenceHelper.getProjectsOfLocation(id);
			if(!projects.isEmpty()){
				StringBuilder output = new StringBuilder();
				// TODO logging
				output.append("The location can only be deleted when no project refers to the location.\n");
				output.append("The following projects refer to the location:\n");
				for(Project p : projects){
					output.append(p.toString() + "\n");
				}
				return Response.status(Status.CONFLICT).entity(output.toString()).build();
			}
			em.getTransaction().begin();
				System.out.println("trying to remove project with the id: " + id);
				Location location = em.find(Location.class, id);
				if(location == null){
					System.out.println("no location for the given id \""+id +"\"!");
					return Response.status(Status.BAD_REQUEST).build();
				}
				System.out.println("remove location: " + location);
	
				em.remove(location);
			
			em.getTransaction().commit();
		} catch(Exception e){
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		} finally{
			em.close();
		}
		
		return Response.ok().build();
	}

}
