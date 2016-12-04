package org.weitblicker;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.weitblicker.database.Location;
import org.weitblicker.database.PersistenceHelper;
import org.weitblicker.database.PersistenceManager;
import org.weitblicker.database.Project;
import org.weitblicker.database.User;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.net.URI;
import java.util.Locale;

/**
 * Main Class for Weitblick App Server
 * @author benedikt, nizzke, Janis
 * @since 14.10.2016
 */

public class Main
{

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer()
    {
        // create a resource config that scans for JAX-RS resources and providers
        // in org.weitblicker package
        final ResourceConfig rc = new ResourceConfig().packages("org.weitblicker");
        //final ResourceConfig resourceConfig = new ResourceConfig(BackendEndpoint.class);
        rc.register(MultiPartFeature.class);
        
        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(Options.BASE_URI), rc);
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException
    {
        if (Options.BASE_URI == null)
            Options.BASE_URI = "http://localhost:8180";

        Project project = new Project();
        project.setName("Projekt in Osnabrück", Locale.GERMAN);
        project.setName("Project in Osnabrück", Locale.ENGLISH);
        project.setDesc("Mein Zuhause", Locale.GERMAN);
        project.setDesc("My Home", Locale.ENGLISH);
        project.setAbst("Hier wohne ich.", Locale.GERMAN);
        project.setAbst("I live here.", Locale.ENGLISH);
        Location location = new Location();
        location.setCountry("Deutschland");
        location.setLatitude(52.2704912f);
        location.setLongitude(8.0423217f);
        location.setStreet("Neuer Graben");
        location.setNumber(5);
        project.setLocation(location);
        
        User user = new User();
        user.setEmail("spuetz@uos.de");
        user.setName("spuetz");
        user.setPassword("123");

        
        try {
        	EntityManager em = PersistenceHelper.getPersistenceManager().getEntityManager();
        	em.getTransaction().begin();
            em.merge(location);
            em.merge(project);
            em.persist(user);
            em.getTransaction().commit();
            em.close();
        } catch (Error E) {
            System.out.println(E.getMessage());
            E.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", Options.BASE_URI));
        System.in.read();
        server.shutdown();
    }
}

