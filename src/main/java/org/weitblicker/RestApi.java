package org.weitblicker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.weitblicker.database.PersistenceHelper;
import org.weitblicker.database.Project;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;

/**
 * Root resource (exposed at "rest" path)
 */
@Path("rest/projekt")
public class RestApi
{
    private ObjectMapper jsonMapper = new ObjectMapper();


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
            List<Long> liste = PersistenceHelper.getProjectList();
            return jsonMapper.writeValueAsString(liste);
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
            Long projectId = Long.valueOf(id);
            Project project = PersistenceHelper.getProject(projectId);
            return jsonMapper.writeValueAsString(project);
        } catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }
        return "";
    }
}
