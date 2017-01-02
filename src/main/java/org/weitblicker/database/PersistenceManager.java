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

    public PersistenceManager()
    {
        Map<String, String> properties_jpa_weitblick = createCustomProperties();
        if (Options.DB_URL_JPA_WEITBLICK != null)
            properties_jpa_weitblick.put(PersistenceUnitProperties.JDBC_URL, Options.DB_URL_JPA_WEITBLICK);

        emFactoryWeitblick = Persistence.createEntityManagerFactory("jpa-weitblick", properties_jpa_weitblick);
    }

    private Map<String, String> createCustomProperties()
    {
        Map<String, String> properties = new HashMap<>();
        if (Options.DB_USER != null)
            properties.put(PersistenceUnitProperties.JDBC_USER, Options.DB_USER);
        if (Options.DB_PASSWORD != null)
            properties.put(PersistenceUnitProperties.JDBC_PASSWORD, Options.DB_PASSWORD);
        if (Options.DB_DDL_GENERATION != null)
            properties.put(PersistenceUnitProperties.DDL_GENERATION, Options.DB_DDL_GENERATION);
        return properties;
    }

    // EntityManager is chosen depending on user request
    public EntityManager getEntityManager(){
    	return emFactoryWeitblick.createEntityManager();
    }

    public void close() {
        emFactoryWeitblick.close();
    }

}
