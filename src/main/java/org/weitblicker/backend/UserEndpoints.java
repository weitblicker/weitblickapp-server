package org.weitblicker.backend;

import java.io.IOException;
import java.io.StringWriter;
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
import org.weitblicker.database.PersistenceHelper;
import org.weitblicker.database.User;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

@Path("backend/users")
@Secured(UserRole.admin)
public class UserEndpoints {
	
    @Secured
	@GET
	@Path("")
	@Produces("text/html")
	public Response users(){
		System.out.println("All users...");
					
		List<User> users = PersistenceHelper.getUsers();
		
		class BackendInfo{
			public BackendInfo(List<User> users){
				this.users = users;
				this.language = Options.DEFAULT_LANGUAGE;
			}
			List<User> users;
			Locale language;	
			
			@SuppressWarnings("unused")
			public List<User>getUsers(){
				return users;
			}

			@SuppressWarnings("unused")
			public String getLanguage(){
				return language.getLanguage();
			}
		}
		
	    MustacheFactory mf = new DefaultMustacheFactory();
	    Mustache mustache = mf.compile("files/mustache/users.mustache");
	    StringWriter stringWriter = new StringWriter();
	    try {
			mustache.execute(stringWriter, new BackendInfo(users)).flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.ok(stringWriter.getBuffer().toString()).build();
	}
	
	@GET
	@Path("add")
	@Produces("text/html")
	public Response addUser(){
	    MustacheFactory mf = new DefaultMustacheFactory();
	    Mustache mustache = mf.compile("files/mustache/addUser.mustache");
	    StringWriter stringWriter = new StringWriter();
	    try {
			mustache.execute(stringWriter, new Object()).flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.ok(stringWriter.getBuffer().toString()).build();
	}
	
	@GET
	@Path("edit/{id}")
	@Produces("text/html")
	public Response editUser(@PathParam("id") Long id){

		
		System.out.println("User with id:" + id);
		User user = PersistenceHelper.getUser(id);
		if(user == null){
			System.out.println("No user for the given id found!");
			// no user for the given id found.
			return Response.status(Status.BAD_REQUEST).build();
		}
		
	    MustacheFactory mf = new DefaultMustacheFactory();
	    Mustache mustache = mf.compile("files/mustache/editUser.mustache");
	    StringWriter stringWriter = new StringWriter();
	    try {
			mustache.execute(stringWriter, user).flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.ok(stringWriter.getBuffer().toString()).build();
	}
}
