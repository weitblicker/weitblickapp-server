package org.weitblicker;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.weitblicker.database.PersistenceManager;
import org.weitblicker.database.Project;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.net.URI;

/**
 * Main Class for Weitblick App Server
 * @author benedikt, nizzke, Janis
 * @since 14.10.2016
 */

public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8180/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in org.weitblicker package
        final ResourceConfig rc = new ResourceConfig().packages("org.weitblicker");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        Project project = new Project();
        project.setNameNo( 123 )
               .setDescriptionShortNo( 234 )
               .setDescriptionLongNo( 345 )
               .setLocationId( 1 );

        EntityManager emWeitblick;
        EntityManager emApp;
        try {
            emWeitblick = PersistenceManager.INSTANCE.getEntityManager( "weitblick" );
            emApp = PersistenceManager.INSTANCE.getEntityManager( "app" );
        } catch (Error E) {
            System.out.println(E.getMessage());
            return;
        }

        emWeitblick.getTransaction().begin();
        emWeitblick.persist(project);
        emWeitblick.getTransaction().commit();
        emWeitblick.close();

        PersistenceManager.INSTANCE.close();

        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.stop();


    }
}

