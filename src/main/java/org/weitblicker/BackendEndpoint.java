package org.weitblicker;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.IllformedLocaleException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
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

import org.apache.tika.Tika;
import org.weitblicker.database.PersistenceHelper;
import org.weitblicker.database.Project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

@Path("backend/")
public class BackendEndpoint {
	
    private ObjectMapper jsonMapper = new ObjectMapper();
	
	class BackendInfo{
		List<Project> projects;
		Project project;
	}

	@GET
	@Path("projects/")
	@Produces("text/html")
	public Response projects(@QueryParam("language") String language){
		System.out.println("All projects...");
		
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
		
		System.out.println("language: " + currentLanguage.getLanguage());
		
		BackendInfo info = new BackendInfo();
		info.projects = PersistenceHelper.getAllProjects();
		
		// set language for response
		for(Project project : info.projects){
			project.setCurrentLanguage(currentLanguage);
		}

	    MustacheFactory mf = new DefaultMustacheFactory();
	    Mustache mustache = mf.compile("files/projects.mustache");
	    StringWriter stringWriter = new StringWriter();
	    try {
			mustache.execute(stringWriter, info).flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.ok(stringWriter.getBuffer().toString()).build();
	}
	
	@GET
	@Path("projects/edit/{id}")
	@Produces("text/html")
	public Response editProject(@PathParam("id") Long id){
		System.out.println("Project with id:" + id);
		Project project = PersistenceHelper.getProject(id);

	    MustacheFactory mf = new DefaultMustacheFactory();
	    Mustache mustache = mf.compile("files/edit_project.mustache");
	    StringWriter stringWriter = new StringWriter();
	    try {
			mustache.execute(stringWriter, new PersistenceHelper()).flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.ok(stringWriter.getBuffer().toString()).build();
	}	
	
	@GET
	@Path("download/{file:.*}")
	@Consumes("application/octet-stream")
	public Response getAttachment(@PathParam("file") String fileName) {
		
		if (fileName == null) {
			System.err.println("No such item");
				return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		if (fileName.contains("..")){
			// illegal folder traversal 
			Response.status(Status.BAD_REQUEST).build();
		}
		
		// TODO ensure that the file is inside 
		// the correct folder to prevent hacker attacks!
		// TODO get path to resource folder!

		File file = new File("files/"+ fileName);

		if(file.exists()){
			return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM).build();
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	@GET
	@Path("file/{file:.*}")
	@Produces("*/*")
	public Response getFile(@PathParam("file") String fileName){
		if (fileName == null) {
			System.err.println("No such item");
				return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		if (fileName.contains("..")){
			// illegal folder traversal 
			Response.status(Status.BAD_REQUEST).build();
		}
		
		// TODO ensure that the file is inside
		// the correct folder to prevent hacker attacks!
		// TODO get path to resource folder!
		File file = new File("files/"+ fileName);
		System.out.println(file.getAbsolutePath());

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
}
