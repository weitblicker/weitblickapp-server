package org.weitblicker.database;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.LinkedList;
import java.util.List;

/**
 * Concrete and general Helper functions with direct database access
 * @see {Persistence Manager}
 * @author
 * @since
 */

public class PersistenceHelper
{
    // --------------------------- LOCAL VARIABLES ----------------------------------------
    static EntityManager emWeitblick = new PersistenceManager().getEntityManager( "weitblick" );
    //static EntityManager emApp = PersistenceManager.getEntityManager( "app" );

    // ----------------------------- USER HELPER ------------------------------------------
    public static String getUser(long userId){
        return "this";
    }

    // ----------------------------- HOST HELPER ------------------------------------------

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

    // ---------------------------- PROJECT HELPER ----------------------------------------


    /**
     * Gets the project list from DB
     * @return list of projectIds
     */
    public static List<Long> getProjectList()
    {
        TypedQuery<Long> query = emWeitblick.createQuery(
                "SELECT c.id FROM Project c", Long.class);
        return query.getResultList();
    }

    /**
     * Gets a project object from DB
     * @param long projectId
     * @return project project
     */
    public static Project getProject(long projectId)
    {
        return emWeitblick.find(Project.class, projectId);
    }

    // ----------------------------- NEED HELPER ------------------------------------------

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

    // --------------------------- LOCATION HELPER ----------------------------------------

    public static Location getLocation(long locationId)
    {
        return emWeitblick.find(Location.class, locationId);
    }

    // --------------------------- DONATION HELPER ----------------------------------------

    public static Donation getDonation(long donationId)
    {
        return emWeitblick.find(Donation.class, donationId);
    }
}
