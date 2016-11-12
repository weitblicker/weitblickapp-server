package org.weitblicker.database;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
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
    static EntityManager emWeitblick = new PersistenceManager().getEntityManager( "weitblick" );
    //static EntityManager emApp = PersistenceManager.getEntityManager( "app" );


    public static User getUserByEmail(String email){
        TypedQuery<User> query = emWeitblick.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class);
        query.setParameter("email", email);
        return query.getSingleResult();
    }
    
	public static User getUserByName(String name) {
        TypedQuery<User> query = emWeitblick.createQuery(
                "SELECT u FROM User u WHERE u.name = :name", User.class);
        query.setParameter("name", name);
        return query.getSingleResult();
	}

    public static Host getHostByEmail(String email)
    {
        TypedQuery<Host> query = emWeitblick.createQuery(
        		"SELECT h FROM Host h WHERE h.email = :email", Host.class);
        query.setParameter("email", email);
        return query.getSingleResult();
    }

    public static void setHost( LanguageString name, String email, Location location ) {
        Host host = new Host(name, email, location);
        emWeitblick.persist(location);
        emWeitblick.persist(host);
        emWeitblick.flush();
    }


    // ---------------------------- PROJECT HELPER ----------------------------------------

    /**
     * Gets the project list from DB
     * @return list of projectIds
     */
    public static List<Project> getAllProjects()
    {
        TypedQuery<Project> query = emWeitblick.createQuery(
                "SELECT c FROM Project c", Project.class);
        return query.getResultList();
    }

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
    
    public static Long createOrUpdateProject(Project project){
    	try{
	        EntityTransaction transaction = emWeitblick.getTransaction();
	        transaction.begin();
	        emWeitblick.persist(project);
	        emWeitblick.flush();
	        transaction.commit();
	        return project.getId();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return null;
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

