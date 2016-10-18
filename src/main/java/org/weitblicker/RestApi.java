package org.weitblicker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.weitblicker.database.Project;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.LinkedList;

/**
 * Root resource (exposed at "rest" path)
 */
@Path("rest/projekt")
public class RestApi
{
    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Path("liste")
    @Produces("application/json")
    public String getProjectIds() {

        try
        {
            Project project = new Project( 123 , 234, 345, 1);
            project.setId(0);
            LinkedList<Long> liste = new LinkedList<Long>();
            liste.add(new Long(0));
            liste.add(new Long(1));

            ObjectMapper mapper = new ObjectMapper();

            return mapper.writeValueAsString(liste);
        } catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    @GET
    @Path("{id}")
    @Produces("application/json")
    public String getProject(@PathParam("id") final String id)
    {
        try
        {
            Project project = new Project( 123 , 234, 345, 1);
            project.setId(0);
            ObjectMapper mapper = new ObjectMapper();

            return mapper.writeValueAsString(project);
        } catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }
        return "";
    }
}
