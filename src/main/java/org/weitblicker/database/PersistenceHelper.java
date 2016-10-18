package org.weitblicker.database;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Concrete and general Helper functions with direct database access
 * @see {Persistence Manager}
 * @author
 * @since
 */

public class PersistenceHelper
{
    static EntityManager emWeitblick = PersistenceManager.INSTANCE.getEntityManager( "weitblick" );
    //static EntityManager emApp = PersistenceManager.INSTANCE.getEntityManager( "app" );


    /**
     * Reads a text from DB
     * @param textNo
     * @param languageId
     * @return string text
     */
    public static String getText(long languageId, long textNo)
    {
        TypedQuery<Text> query = emWeitblick.createQuery(
                "SELECT c FROM Text c WHERE c.textNo = :textNo AND c.languageNo = : languageNo", Text.class);
        query.setParameter("textNo", textNo);
        query.setParameter("languageId", languageId);
        return query.getSingleResult().getText();
    }

    /**
     * This method adds or changes text
     * @param string text
     * @param long languageId
     * @param long textNo nullable
     * @return long textNo
     */
    public static long setText( long languageId, String text, long textNo)
    {
        textNo = 0;
        Text textObject = new Text();
        textObject.setLanguageId( languageId );
        textObject.setText(text);
        if (textNo != 0)
            textObject.setNo(textNo);

        emWeitblick.persist(textObject);
        emWeitblick.flush();
        return textObject.getId();
    }

    /**
     * Gets a project object from DB
     * @param long projectId
     * @return project project
     */
    public static Project getProject(long projectId)
    {
        return emWeitblick.find(Project.class, projectId);
        return emWeitblick.
    }

    public static Need getNeed(long needId)
    {
        return emWeitblick.find(Need.class, needId);
    }

    public static long setNeed( String name, String descriptionShort, String descriptionLong, long projectId, long languageId ) {
        Need need = new Need( name, descriptionShort, descriptionLong, projectId, languageId );
        emWeitblick.persist( need );
        emWeitblick.flush();
        return need.getId();
    }

    public static Location getLocation(long locationId)
    {
        return emWeitblick.find(Location.class, locationId);
    }

    public static Donation getDonation(long donationId)
    {
        return emWeitblick.find(Donation.class, donationId);
    }

    public static List<Project_Host> getProjektHostIds(long projectId)
    {
        TypedQuery<Project_Host> query = emWeitblick.createQuery(
                "SELECT c FROM Text c WHERE c.projectId = :projectId", Project_Host.class);
        query.setParameter("projectId", projectId);
        return query.getResultList();
    }

    public static Host getHost(long hostId)
    {
        return emWeitblick.find( Host.class, hostId );
    }

    public static long setHost( String name, String email, long locationId ) {
        Host host = new Host(name, email, locationId);
        emWeitblick.persist( host );
        emWeitblick.flush();
        return host.getId();
    }

    // TODO: 18.10.16 finish this one
    public static long setHost( String name, String email, Location location ) {
        //
        emWeitblick.persist( location );
        emWeitblick.flush();
        //long id = location.getId();
        //setHost( name, email, id );
        return 1;
    }
}
