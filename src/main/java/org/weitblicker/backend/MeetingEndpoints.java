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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.weitblicker.Options;
import org.weitblicker.Secured;
import org.weitblicker.UserRole;
import org.weitblicker.Utility;
import org.weitblicker.database.Meeting;
import org.weitblicker.database.PersistenceHelper;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

@Path("backend/meetings")
@Secured({UserRole.admin, UserRole.maintainer})
public class MeetingEndpoints {
	
    @Secured
	@GET
	@Path("{language}")
	@Produces("text/html")
	public Response meetings(@Context SecurityContext securityContext, @PathParam("language") final String language){
		System.out.println("All meetings...");
		
		Locale currentLanguage = Options.DEFAULT_LANGUAGE;
		
		try{
			currentLanguage = Utility.getLanguage(language);
			System.out.println("language: " + currentLanguage.getLanguage());			
		} catch(IllformedLocaleException e){
        	e.printStackTrace();
        	return Response.status(Response.Status.BAD_REQUEST).build();
        }
		
		List<Meeting> meetings = PersistenceHelper.getMeetings();

		// set language for response
		for(Meeting meeting : meetings){
			meeting.setCurrentLanguage(currentLanguage);
		}

		class BackendInfo{
			public BackendInfo(
					List<Meeting> meetings,
					Locale language
					){
				this.meetings = meetings;
				this.language = language;
			}
			List<Meeting> meetings;
			Locale language;	
			
			@SuppressWarnings("unused")
			public List<Meeting>getMeetings(){
				return meetings;
			}

			@SuppressWarnings("unused")
			public String getLanguage(){
				return language.getLanguage();
			}
		}
		
	    MustacheFactory mf = new DefaultMustacheFactory();
	    Mustache mustache = mf.compile("files/mustache/meetings.mustache");
	    StringWriter stringWriter = new StringWriter();
	    try {
			mustache.execute(stringWriter, new BackendInfo(meetings, currentLanguage)).flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.ok(stringWriter.getBuffer().toString()).build();
	}
    
	@GET
	@Path("add")
	@Produces("text/html")
	public Response addMeeting(){
	    MustacheFactory mf = new DefaultMustacheFactory();
	    Mustache mustache = mf.compile("files/mustache/addMeeting.mustache");
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
	public Response editMeeting(@PathParam("id") Long id, @PathParam("language") final String language){


		Locale currentLanguage = Options.DEFAULT_LANGUAGE;
		
		try{
			currentLanguage = Utility.getLanguage(language);
			System.out.println("language: " + currentLanguage.getLanguage());			
		} catch(IllformedLocaleException e){
        	e.printStackTrace();
        	return Response.status(Response.Status.BAD_REQUEST).build();
        }
		
		System.out.println("Meeting with id:" + id);
		Meeting meeting = PersistenceHelper.getMeeting(id);
		meeting.setCurrentLanguage(currentLanguage);
		
	    MustacheFactory mf = new DefaultMustacheFactory();
	    Mustache mustache = mf.compile("files/mustache/editMeeting.mustache");
	    StringWriter stringWriter = new StringWriter();
	    try {
			mustache.execute(stringWriter, meeting).flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Response.ok(stringWriter.getBuffer().toString()).build();
	}	
}
