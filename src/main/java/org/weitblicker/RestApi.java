package org.weitblicker;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

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
        return "return projects";
    }

    @GET
    @Path("{id}")
    @Produces("application/json")
    public String getProject(@PathParam("id") final String id) {
//        UUID uuid = UUID.fromString(id);
        return "return project ".concat(id);
    }
}
