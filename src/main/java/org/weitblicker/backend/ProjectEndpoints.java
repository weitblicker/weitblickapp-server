package org.weitblicker.backend;

import java.io.IOException;
import java.io.StringWriter;
import java.security.Principal;
import java.util.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.weitblicker.Options;
import org.weitblicker.Secured;
import org.weitblicker.UserRole;
import org.weitblicker.Utility;
import org.weitblicker.database.PersistenceHelper;
import org.weitblicker.database.Project;
import org.weitblicker.database.User;
import org.weitblicker.database.Host;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

@Path("backend/projects")
@Secured({UserRole.admin, UserRole.maintainer})
public class ProjectEndpoints {
	
    @Secured
	@GET
	@Path("{language}")
	@Produces("text/html")
	public Response projects(@Context SecurityContext securityContext, @PathParam("language") final String language){

		System.out.println("All projects...");
		
		Locale currentLanguage = Options.DEFAULT_LANGUAGE;
		
		try{
			currentLanguage = Utility.getLanguage(language);
			System.out.println("language: " + currentLanguage.getLanguage());			
		} catch(IllformedLocaleException e){
        	e.printStackTrace();
        	return Response.status(Response.Status.BAD_REQUEST).build();
        }

		Principal principal = securityContext.getUserPrincipal();
		User user = (User) principal;
		List<Project> projects;

		if(user.hasRole(UserRole.admin)){
			projects = PersistenceHelper.getAllProjects();
		}
		else{
			Set<Project> projectsSet = new HashSet<Project>();

			List<Host> hosts = user.getHosts();

			for (Host h : hosts) {
				projectsSet.addAll(h.getProjects());
			}

			projects = new LinkedList<Project>(projectsSet);
		}

		// set language for response
		for(Project project : projects){
			project.setCurrentLanguage(currentLanguage);
		}

		class BackendInfo{
			public BackendInfo(
					List<Project> projects,
					Locale language
					){
				this.projects = projects;
				this.language = language;
			}
			List<Project> projects;
			Locale language;	
			
			@SuppressWarnings("unused")
			public List<Project>getProjects(){
				return projects;
			}

			@SuppressWarnings("unused")
			public String getLanguage(){
				return language.getLanguage();
			}
		}
		
	    MustacheFactory mf = new DefaultMustacheFactory();
	    Mustache mustache = mf.compile("files/mustache/projects.mustache");
	    StringWriter stringWriter = new StringWriter();
	    try {
			mustache.execute(stringWriter, new BackendInfo(projects, currentLanguage)).flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.ok(stringWriter.getBuffer().toString()).build();
	}
    
	@GET
	@Path("add")
	@Produces("text/html")
	public Response addProject(){
	    MustacheFactory mf = new DefaultMustacheFactory();
	    Mustache mustache = mf.compile("files/mustache/addProject.mustache");
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
	public Response editProject(@PathParam("id") Long id, @PathParam("language") final String language){


		Locale currentLanguage = Options.DEFAULT_LANGUAGE;
		
		try{
			currentLanguage = Utility.getLanguage(language);
			System.out.println("language: " + currentLanguage.getLanguage());			
		} catch(IllformedLocaleException e){
        	e.printStackTrace();
        	return Response.status(Response.Status.BAD_REQUEST).build();
        }
		
		System.out.println("Project with id:" + id);
		Project project = PersistenceHelper.getProject(id);
		project.setCurrentLanguage(currentLanguage);
		
	    MustacheFactory mf = new DefaultMustacheFactory();
	    Mustache mustache = mf.compile("files/mustache/editProject.mustache");
	    StringWriter stringWriter = new StringWriter();
	    try {
			mustache.execute(stringWriter, project).flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.ok(stringWriter.getBuffer().toString()).build();
	}	
}
