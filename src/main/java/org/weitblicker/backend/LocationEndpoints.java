package org.weitblicker.backend;

import java.io.IOException;
import java.io.StringWriter;
import java.util.IllformedLocaleException;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.weitblicker.Options;
import org.weitblicker.Secured;
import org.weitblicker.UserRole;
import org.weitblicker.Utility;
import org.weitblicker.database.Location;
import org.weitblicker.database.PersistenceHelper;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

@Secured({UserRole.admin, UserRole.maintainer})
@Path("backend/locations")
public class LocationEndpoints {
	
	@GET
	@Path("{language}")
	@Produces("text/html")
	public Response locations(@PathParam("language") final String language){
		System.out.println("All locations...");
		
		Locale currentLanguage = Options.DEFAULT_LANGUAGE;
		
		try{
			currentLanguage = Utility.getLanguage(language);
			System.out.println("language: " + currentLanguage.getLanguage());			
		} catch(IllformedLocaleException e){
        	e.printStackTrace();
        	return Response.status(Response.Status.BAD_REQUEST).build();
        }
			
		List<Location> locations = PersistenceHelper.getAllLocations();
		
		// set language for response
		for(Location location : locations){
			location.setCurrentLanguage(currentLanguage);
		}

		class BackendInfo{
			public BackendInfo(
					List<Location> locations,
					Locale language
					){
				this.locations = locations;
				this.language = language;
			}
			List<Location> locations;
			Locale language;	
			
			@SuppressWarnings("unused")
			public List<Location>getLocations(){
				return locations;
			}

			@SuppressWarnings("unused")
			public String getLanguage(){
				return language.getLanguage();
			}
		}
		
	    MustacheFactory mf = new DefaultMustacheFactory();
	    Mustache mustache = mf.compile("files/mustache/locations.mustache");
	    StringWriter stringWriter = new StringWriter();
	    try {
			mustache.execute(stringWriter, new BackendInfo(locations, currentLanguage)).flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.ok(stringWriter.getBuffer().toString()).build();
	}
	
	@GET
	@Path("add")
	@Produces("text/html")
	public Response addLocation(){
	    MustacheFactory mf = new DefaultMustacheFactory();
	    Mustache mustache = mf.compile("files/mustache/addLocation.mustache");
	    StringWriter stringWriter = new StringWriter();
	    try {
			mustache.execute(stringWriter, new Object()).flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.ok(stringWriter.getBuffer().toString()).build();
	}
	
	
	
	@GET
	@Path("{language}/edit/{id}")
	@Produces("text/html")
	public Response editLocation(@PathParam("id") Long id, @PathParam("language") final String language){

		Locale currentLanguage = Options.DEFAULT_LANGUAGE;
		
		try{
			currentLanguage = Utility.getLanguage(language);
			System.out.println("language: " + currentLanguage.getLanguage());			
		} catch(IllformedLocaleException e){
        	e.printStackTrace();
        	return Response.status(Response.Status.BAD_REQUEST).build();
        }
		
		System.out.println("Location with id:" + id);
		Location location = PersistenceHelper.getLocation(id);
		if(location == null){
			System.out.println("No location for the given id found!");
			// no location for the given id found.
			return Response.status(Status.BAD_REQUEST).build();
		}
		location.setCurrentLanguage(currentLanguage);
		
	    MustacheFactory mf = new DefaultMustacheFactory();
	    Mustache mustache = mf.compile("files/mustache/editLocation.mustache");
	    StringWriter stringWriter = new StringWriter();
	    try {
			mustache.execute(stringWriter, location).flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.ok(stringWriter.getBuffer().toString()).build();
	}	
}
