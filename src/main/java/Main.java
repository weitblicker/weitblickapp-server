/**
 * Created by benedikt on 15.10.16.
 */
import javax.persistence.EntityManager;

public class Main {

    public static void main(String[] args) {
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
