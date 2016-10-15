package org.weitblicker; /**
 * Created by benedikt on 15.10.16.
 */

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


public enum PersistenceManager {

    INSTANCE;

    private EntityManagerFactory emFactoryWeitblick;
    private EntityManagerFactory emFactoryApp;

    private PersistenceManager() {

        // 2 persistent units are defined, one for each database
        emFactoryWeitblick = Persistence.createEntityManagerFactory("jpa-weitblick");
        emFactoryApp = Persistence.createEntityManagerFactory("jpa-app");
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
