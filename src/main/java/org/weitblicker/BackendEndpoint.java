package org.weitblicker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.tika.Tika;

@Path("backend/")
public class BackendEndpoint {

	@GET
	@Path("admin/")
	@Produces("text/html")
	public Response login(){
		return Response.ok("<html><h1>Test</h1></html>").build();
	}
	
	@GET
	@Path("download/{file}")
	@Consumes("text/plain; charset=UTF-8")
	public Response getAttachment(@PathParam("file") String fileName) {
		if (fileName == null) {
			System.err.println("No such item");
				return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		// TODO ensure that the file is inside the correct folder to prevent hacker attacks!
		File file = new File("webfiles/"+ fileName);

		if(file.exists()){
			return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM).build();
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}
	
	@GET
	@Path("file/{file}")
	@Produces("text/html")
	public Response getFile(@PathParam("file") String fileName){
		if (fileName == null) {
			System.err.println("No such item");
				return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		// TODO ensure that the file is inside the correct folder to prevent hacker attacks!
		// TODO get path to resource folder!
		File file = new File("files/"+ fileName);
		System.out.println(file.getAbsolutePath());

		try {
			InputStream inputStream = new FileInputStream(file);
			
			Tika tika = new Tika();
			String mimeType = tika.detect(inputStream);
			System.out.println("MimeType: " + mimeType);
			return Response.ok(inputStream, mimeType).build();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}
}
