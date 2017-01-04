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

import org.weitblicker.database.Host;
import org.weitblicker.database.PersistenceHelper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("rest/host")
public class HostRestApi {
	
    private ObjectMapper jsonMapper = new ObjectMapper();
	
    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHosts() {
    	List<Host> hosts = PersistenceHelper.getHosts();
        try {
			return Response.ok(jsonMapper.writeValueAsString(hosts)).build();
		} catch (JsonProcessingException e) {
			// TODO logging
			e.printStackTrace();
			return Response.status(Status.BAD_REQUEST).build();
		}
    }
    
	@POST
	@Path("new")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createHost(String jsonHost){
		
		System.out.println("create new host...");
		Host host = null;

		// parse json 
		try {
			host = jsonMapper.readValue(jsonHost, Host.class);
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		System.out.println("Host: " + host);
		
		if(host.getId() != null){
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		try{
			Long id = PersistenceHelper.createOrUpdateHost(host);
			System.out.println("created new host with id: " + id);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try {
			String jsonResponse = jsonMapper.writeValueAsString(host);
			return Response.ok(jsonResponse).build();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}		
	}
	
	@PUT
	@Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateHost(String jsonHost){
		
		System.out.println("update host...");

		try{		
			Host receivedHost = jsonMapper.readValue(jsonHost, Host.class);
			Long id = receivedHost.getId();
			
			// no id given -> bad request
			if(id == null) {
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
			
			// get host from database
			EntityManager em = PersistenceHelper.getPersistenceManager().getEntityManager();
			em.getTransaction().begin();
			Host dbHost = em.find(Host.class, id);
			
			// merge host with changes
			dbHost = jsonMapper.readerForUpdating(dbHost).readValue(jsonHost);

			em.persist(dbHost);
			em.getTransaction().commit();
			em.close();
			System.out.println("updated host with id: " + dbHost.getId());
			
			// return the updated host as json
			String jsonResponse = jsonMapper.writeValueAsString(dbHost);
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
	
	
	@DELETE
	@Path("remove/{id}")
	public Response removeHost(@PathParam("id") final Long id){
		EntityManager em =PersistenceHelper.getPersistenceManager().getEntityManager();
		em.getTransaction().begin();
		try{
			System.out.println("trying to remove host with the id: " + id);
			Host host = em.find(Host.class, id);
			if(host == null){
				System.out.println("No host for the given id \"" + id + "\".");
				return Response.status(Status.BAD_REQUEST).build();
			}
			System.out.println("remove host: " + host);
			em.remove(host);
		} catch(Exception e){
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		em.getTransaction().commit();
		em.close();
		
		return Response.ok().build();
	}
}
