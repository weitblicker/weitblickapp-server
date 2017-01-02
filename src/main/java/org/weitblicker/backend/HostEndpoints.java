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
import org.weitblicker.database.Host;
import org.weitblicker.database.PersistenceHelper;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

@Path("backend/hosts")
public class HostEndpoints {
	
    @Secured
	@GET
	@Path("")
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
			
			@SuppressWarnings("unused")
			public List<Host>getHosts(){
				return hosts;
			}

			@SuppressWarnings("unused")
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
	@Path("add")
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
	@Path("edit/{id}")
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
}
