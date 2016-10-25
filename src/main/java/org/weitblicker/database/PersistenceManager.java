package org.weitblicker.database;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.weitblicker.Options;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

/**
 * Database Connection Object
 * @see {JPA}
 * @author benedikt
 * @since 15.10.16
 */
public class PersistenceManager
{
    private EntityManagerFactory emFactoryWeitblick;
    private EntityManagerFactory emFactoryApp;

    public PersistenceManager()
    {
        // 2 persistent units are defined, one for each database
        Map properties_jpa_app = createCustomProperties();
        if (Options.DB_URL_JPA_APP != null)
            properties_jpa_app.put(PersistenceUnitProperties.JDBC_URL, Options.DB_URL_JPA_APP);

        Map properties_jpa_weitblick = createCustomProperties();
        if (Options.DB_URL_JPA_WEITBLICK != null)
            properties_jpa_weitblick.put(PersistenceUnitProperties.JDBC_URL, Options.DB_URL_JPA_WEITBLICK);

        emFactoryWeitblick = Persistence.createEntityManagerFactory("jpa-weitblick", properties_jpa_weitblick);
        emFactoryApp = Persistence.createEntityManagerFactory("jpa-app", properties_jpa_app);
    }

    private Map createCustomProperties()
    {
        Map properties = new HashMap<>();
        if (Options.DB_USER != null)
            properties.put(PersistenceUnitProperties.JDBC_USER, Options.DB_USER);
        if (Options.DB_PASSWORD != null)
            properties.put(PersistenceUnitProperties.JDBC_PASSWORD, Options.DB_PASSWORD);
        if (Options.DB_DDL_GENERATION != null)
            properties.put(PersistenceUnitProperties.DDL_GENERATION, Options.DB_DDL_GENERATION);
        return properties;
    }

    // EntityManager is chosen depending on user request
    public EntityManager getEntityManager( String db ) throws Error {
        if( db.equals( "weitblick" ) ) {
            return emFactoryWeitblick.createEntityManager();
        } else if( db.equals( "app" ) ) {
            return emFactoryApp.createEntityManager();
        } else {
            // Error thrown in case matching EntityManager could not be found
            throw new Error( "ERROR: Invalid choice for persistence unit. Chose 'weitblick' or 'app' ");
        }
    }

    public void close() {
        emFactoryWeitblick.close();
        emFactoryApp.close();
    }

}
