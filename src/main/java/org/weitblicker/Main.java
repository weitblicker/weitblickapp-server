package org.weitblicker;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.net.URI;

/**
 * Main class.
 *
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/";

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
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.stop();

        Project project = new Project();
        project.setHost("Muenster")
                .setName("Uni-baut-Uni")
                .setDescrShort("Tolles Projekt")
                .setDescrLong("Noch besseres Projekt");

        EntityManager emWeitblick = PersistenceManager.INSTANCE.getEntityManager( "weitblick" );
        EntityManager emApp = PersistenceManager.INSTANCE.getEntityManager( "app" );

        emWeitblick.getTransaction().begin();
        emWeitblick.persist(project);
        emWeitblick.getTransaction().commit();
        emWeitblick.close();

        PersistenceManager.INSTANCE.close();
    }
}

