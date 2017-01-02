package org.weitblicker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.IllformedLocaleException;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.tika.Tika;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.weitblicker.database.Host;
import org.weitblicker.database.Image;
import org.weitblicker.database.Location;
import org.weitblicker.database.PersistenceHelper;
import org.weitblicker.database.Project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

@Path("backend/")
public class BackendEndpoint {
	

    @GET
    @Path("/login")
	@Produces("text/html")
    public Response loginForm(){
	    MustacheFactory mf = new DefaultMustacheFactory();
	    Mustache mustache = mf.compile("files/login.mustache");
	    StringWriter stringWriter = new StringWriter();
	    try {
			mustache.execute(stringWriter, new Object()).flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.ok(stringWriter.getBuffer().toString()).build();	
    }
    
    @POST
    @Path("/upload/{filename}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadFile(
        @FormDataParam("file") InputStream uploadedInputStream,
        @FormDataParam("file") FormDataContentDisposition fileMetaData,
        @PathParam("filename") String fileName){

    	
        System.out.println("fileName: "+fileMetaData.getFileName());
        // TODO check filename for hacker attacks... should be a real filename
        String uploadedFileLocation = "files/test/"+fileMetaData.getFileName();
        // save it
        saveToFile(uploadedInputStream, uploadedFileLocation);

        ObjectMapper jsonMapper = new ObjectMapper();

        String output = "File uploaded via Jersey based RESTFul Webservice to: " + uploadedFileLocation;
        String json;
		try {
			json = jsonMapper.writeValueAsString(output);
	        return Response.ok(json).build();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}

    }

    // save uploaded file to new location
    private void saveToFile(InputStream uploadedInputStream,
        String uploadedFileLocation) {

        try {
            OutputStream out = null;
            int read = 0;
            byte[] bytes = new byte[1024];

            out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

    }
        
       
    @Secured
	@GET
	@Path("projects/{language}")
	@Produces("text/html")
	public Response projects(@PathParam("language") final String language){
		System.out.println("All projects...");
		
		Locale currentLanguage = Options.DEFAULT_LANGUAGE;
		
		try{
			currentLanguage = RestApi.getLanguage(language);
			System.out.println("language: " + currentLanguage.getLanguage());			
		} catch(IllformedLocaleException e){
        	e.printStackTrace();
        	return Response.status(Response.Status.BAD_REQUEST).build();
        }
			
		List<Project> projects = PersistenceHelper.getAllProjects();
		
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
			
			public List<Project>getProjects(){
				return projects;
			}

			public String getLanguage(){
				return language.getLanguage();
			}
		}
		
	    MustacheFactory mf = new DefaultMustacheFactory();
	    Mustache mustache = mf.compile("files/projects.mustache");
	    StringWriter stringWriter = new StringWriter();
	    try {
			mustache.execute(stringWriter, new BackendInfo(projects, currentLanguage)).flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.ok(stringWriter.getBuffer().toString()).build();
	}
    
    
 @Secured
	@GET
	@Path("hosts")
	@Produces("text/html")
	public Response projects(){
		System.out.println("All hosts...");
					
		List<Host> hosts = PersistenceHelper.getHosts();
		
		class BackendInfo{
			public BackendInfo(List<Host> hosts){
				this.hosts = hosts;
				this.language = Options.DEFAULT_LANGUAGE;
			}
			List<Host> hosts;
			Locale language;	
			
			public List<Host>getHosts(){
				return hosts;
			}

			public String getLanguage(){
				return language.getLanguage();
			}
		}
		
	    MustacheFactory mf = new DefaultMustacheFactory();
	    Mustache mustache = mf.compile("files/hosts.mustache");
	    StringWriter stringWriter = new StringWriter();
	    try {
			mustache.execute(stringWriter, new BackendInfo(hosts)).flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.ok(stringWriter.getBuffer().toString()).build();
	}
 
	
	@GET
	@Path("locations/{language}/edit/{id}")
	@Produces("text/html")
	public Response editLocation(@PathParam("id") Long id, @PathParam("language") final String language){

		Locale currentLanguage = Options.DEFAULT_LANGUAGE;
		
		try{
			currentLanguage = RestApi.getLanguage(language);
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
	    Mustache mustache = mf.compile("files/editLocation.mustache");
	    StringWriter stringWriter = new StringWriter();
	    try {
			mustache.execute(stringWriter, location).flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.ok(stringWriter.getBuffer().toString()).build();
	}	
	
	@GET
	@Path("projects/{language}/edit/{id}")
	@Produces("text/html")
	public Response editProject(@PathParam("id") Long id, @PathParam("language") final String language){


		Locale currentLanguage = Options.DEFAULT_LANGUAGE;
		
		try{
			currentLanguage = RestApi.getLanguage(language);
			System.out.println("language: " + currentLanguage.getLanguage());			
		} catch(IllformedLocaleException e){
        	e.printStackTrace();
        	return Response.status(Response.Status.BAD_REQUEST).build();
        }
		
		System.out.println("Project with id:" + id);
		Project project = PersistenceHelper.getProject(id);
		project.setCurrentLanguage(currentLanguage);
		
	    MustacheFactory mf = new DefaultMustacheFactory();
	    Mustache mustache = mf.compile("files/editProject.mustache");
	    StringWriter stringWriter = new StringWriter();
	    try {
			mustache.execute(stringWriter, project).flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.ok(stringWriter.getBuffer().toString()).build();
	}	
	
	@GET
	@Path("upload/form")
	@Produces("text/html")
	public Response uploadForm(){
	    MustacheFactory mf = new DefaultMustacheFactory();
	    Mustache mustache = mf.compile("files/upload_file.mustache");
	    StringWriter stringWriter = new StringWriter();
	    try {
			mustache.execute(stringWriter, new Object()).flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.ok(stringWriter.getBuffer().toString()).build();
	}	

	@GET
	@Path("hosts/add")
	@Produces("text/html")
	public Response addHost(){
	    MustacheFactory mf = new DefaultMustacheFactory();
	    Mustache mustache = mf.compile("files/addHost.mustache");
	    StringWriter stringWriter = new StringWriter();
	    try {
			mustache.execute(stringWriter, new Object()).flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.ok(stringWriter.getBuffer().toString()).build();
	}
	
	@GET
	@Path("hosts/edit/{id}")
	@Produces("text/html")
	public Response editLocation(@PathParam("id") Long id){

		
		System.out.println("Host with id:" + id);
		Host host = PersistenceHelper.getHost(id);
		if(host == null){
			System.out.println("No host for the given id found!");
			// no location for the given id found.
			return Response.status(Status.BAD_REQUEST).build();
		}
		
	    MustacheFactory mf = new DefaultMustacheFactory();
	    Mustache mustache = mf.compile("files/editHost.mustache");
	    StringWriter stringWriter = new StringWriter();
	    try {
			mustache.execute(stringWriter, host).flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.ok(stringWriter.getBuffer().toString()).build();
	}	
	
	@GET
	@Path("locations/add")
	@Produces("text/html")
	public Response addLocation(){
	    MustacheFactory mf = new DefaultMustacheFactory();
	    Mustache mustache = mf.compile("files/addLocation.mustache");
	    StringWriter stringWriter = new StringWriter();
	    try {
			mustache.execute(stringWriter, new Object()).flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.ok(stringWriter.getBuffer().toString()).build();
	}
	
	@GET
	@Path("projects/add")
	@Produces("text/html")
	public Response addProject(){
	    MustacheFactory mf = new DefaultMustacheFactory();
	    Mustache mustache = mf.compile("files/addProject.mustache");
	    StringWriter stringWriter = new StringWriter();
	    try {
			mustache.execute(stringWriter, new Object()).flush();
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
	
    @Secured
	@GET
	@Path("locations/{language}")
	@Produces("text/html")
	public Response locations(@PathParam("language") final String language){
		System.out.println("All locations...");
		
		Locale currentLanguage = Options.DEFAULT_LANGUAGE;
		
		try{
			currentLanguage = RestApi.getLanguage(language);
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
			
			public List<Location>getLocations(){
				return locations;
			}

			public String getLanguage(){
				return language.getLanguage();
			}
		}
		
	    MustacheFactory mf = new DefaultMustacheFactory();
	    Mustache mustache = mf.compile("files/locations.mustache");
	    StringWriter stringWriter = new StringWriter();
	    try {
			mustache.execute(stringWriter, new BackendInfo(locations, currentLanguage)).flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.ok(stringWriter.getBuffer().toString()).build();
	}
	
}