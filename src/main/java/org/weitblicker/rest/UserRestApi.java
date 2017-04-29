package org.weitblicker.rest;

import java.io.IOException;
import java.util.IllformedLocaleException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.weitblicker.Secured;
import org.weitblicker.UserRole;
import org.weitblicker.database.Host;
import org.weitblicker.database.PersistenceHelper;
import org.weitblicker.database.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("rest/user")
public class UserRestApi {
	
    private ObjectMapper jsonMapper = new ObjectMapper();

	@Secured({UserRole.admin, UserRole.maintainer})
    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers() {
    	List<User> users = PersistenceHelper.getUsers();
        try {
			return Response.ok(jsonMapper.writeValueAsString(users)).build();
		} catch (JsonProcessingException e) {
			// TODO logging
			e.printStackTrace();
			return Response.status(Status.BAD_REQUEST).build();
		}
    }

	@Secured({UserRole.admin})
	@POST
	@Path("new")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createUser(String jsonUser){
		
		System.out.println("create new user...");
		User user = null;

		// parse json 
		try {
			user = jsonMapper.readValue(jsonUser, User.class);
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		System.out.println("User: " + user);
		
		if(user.getId() != null){
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		try{
			Long id = PersistenceHelper.createOrUpdateUser(user);
			System.out.println("created new user with id: " + id);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try {
			String jsonResponse = jsonMapper.writeValueAsString(user);
			return Response.ok(jsonResponse).build();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}		
	}

	@Secured({UserRole.admin})
	@POST
	@Path("remove/{id}")
	public Response removeUser(@PathParam("id") final Long id){
		EntityManager em =PersistenceHelper.getPersistenceManager().getEntityManager();
		em.getTransaction().begin();
		try{
			System.out.println("trying to remove user with the id: " + id);
			User user = em.find(User.class, id);
			if(user == null){
				System.out.println("No host for the given id \"" + id + "\".");
				return Response.status(Status.BAD_REQUEST).build();
			}
			System.out.println("remove user: " + user);
			em.remove(user);
		} catch(Exception e){
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		em.getTransaction().commit();
		em.close();
		
		return Response.ok().build();
	}

	@Secured({UserRole.admin})
	@POST
	@Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateUser(String jsonUser){
		
		System.out.println("update user...");

		try{		
			User receivedUser = jsonMapper.readValue(jsonUser, User.class);
			Long id = receivedUser.getId();
			
			// no id given -> bad request
			if(id == null) {
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
			
			// get user from database
			EntityManager em = PersistenceHelper.getPersistenceManager().getEntityManager();
			em.getTransaction().begin();
			User dbUser = em.find(User.class, id);
			
			// merge user with changes
			dbUser = jsonMapper.readerForUpdating(dbUser).readValue(jsonUser);

			em.persist(dbUser);
			em.getTransaction().commit();
			em.close();
			System.out.println("updated user with id: " + dbUser.getId());
			
			for(Host host: dbUser.getHosts()){
				em = PersistenceHelper.getPersistenceManager().getEntityManager();
				em.getTransaction().begin();
				em.merge(host);
				em.getTransaction().commit();
				em.close();
			}
			
			// return the updated user as json
			String jsonResponse = jsonMapper.writeValueAsString(dbUser);
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
