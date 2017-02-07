package org.weitblicker.database;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

public class PersistenceHelper
{
	public final static String ADMIN_ROLE_NAME = "admin";
	
    public static PersistenceManager persistenceManager = new PersistenceManager();

    public static PersistenceManager getPersistenceManager(){
    	return persistenceManager;
    }
    
    public static void addUser(User user){
    	EntityManager em = persistenceManager.getEntityManager();
    	em.getTransaction().begin();
    	em.persist(user);
    	em.getTransaction().commit();
        em.close();
    }   
    
    public static List<User> getAdmins(){
    	EntityManager em = persistenceManager.getEntityManager();
    	em.getTransaction().begin();
        TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.role = :role", User.class);
        query.setParameter("role", ADMIN_ROLE_NAME);
        List<User> admins = query.getResultList();
        em.getTransaction().commit();
        em.close();

        return admins;
    }

    public static User getUserByEmail(String email){
    	EntityManager em = persistenceManager.getEntityManager();
    	em.getTransaction().begin();
        TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class);
        query.setParameter("email", email);
        User user = query.getSingleResult();
        em.getTransaction().commit();
        em.close();
        return user;
    }
    
	public static User getUserByName(String name) {
    	EntityManager em = persistenceManager.getEntityManager();
    	em.getTransaction().begin();
        TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.name = :name", User.class);
        query.setParameter("name", name);
        User user = query.getSingleResult();
        em.getTransaction().commit();
        em.close();
        
        return user;
	}

    public static Host getHostByEmail(String email)
    {
    	EntityManager em = persistenceManager.getEntityManager();
    	em.getTransaction().begin();
        TypedQuery<Host> query = em.createQuery(
        		"SELECT h FROM Host h WHERE h.email = :email", Host.class);
        query.setParameter("email", email);
        Host host = query.getSingleResult();
        em.getTransaction().commit();
        em.close();
        return host;
    }

    public static Long createOrUpdateHost( Host host ) {
    	EntityManager em = persistenceManager.getEntityManager();
    	em.getTransaction().begin();
        em.persist(host);
        em.getTransaction().commit();
        em.close();
        return host.id;
    }


    /**
     * Gets the project list from DB
     * @return list of all projects
     */
    public static List<Project> getAllProjects()
    {
    	EntityManager em = persistenceManager.getEntityManager();
    	em.getTransaction().begin();
        TypedQuery<Project> query = em.createQuery(
                "SELECT c FROM Project c", Project.class);
        List<Project> projects = query.getResultList();
        for(Project p: projects){
        	// adds all not listed (manual inserted) images to the database and the project
        	scanProjectImageFolder(p, em);
        }
        
        em.getTransaction().commit();
        em.close();
        return projects;
    }
    


    /**
     * Gets the project list from DB
     * @return list of project ids
     */
    public static List<Long> getProjectList()
    {
    	EntityManager em = persistenceManager.getEntityManager();
    	em.getTransaction().begin();
        TypedQuery<Long> query = em.createQuery(
                "SELECT c.id FROM Project c", Long.class);
        List<Long> ids = query.getResultList();
        em.getTransaction().commit();
        em.close();
        return ids;
    }
    
    private static void scanProjectImageFolder(Meeting m, EntityManager em){
    	List<Image> notListedImages = m.scanFolder();
    	for(Image image: notListedImages){
    		image.setUri( "/rest/meeting/" + m.getId() + "/image/" + image.getName());
    		m.addImage(image);
    		em.persist(image);
    	}
    	em.persist(m);
    }
    
    private static void scanProjectImageFolder(Project p, EntityManager em){
    	List<Image> notListedImages = p.scanFolder();
    	for(Image image: notListedImages){
    		image.setUri( "/rest/project/" + p.getId() + "/image/" + image.getName());
    		p.addImage(image);
    		em.persist(image);
    	}
    	em.persist(p);
    }

    /**
     * Gets a project object from DB
     * @param long projectId
     * @return project project
     */
    public static Project getProject(long projectId)
    {
    	EntityManager em = persistenceManager.getEntityManager();
    	em.getTransaction().begin();
        Project project = em.find(Project.class, projectId);
    	// adds all not listed (manual inserted) images to the database and the project
        scanProjectImageFolder(project, em); 
        em.getTransaction().commit();
        em.close();
        return project;
    }
    
    public static Long createOrUpdateProject(Project project){
    	EntityManager em = persistenceManager.getEntityManager();
    	em.getTransaction().begin();
        em.persist(project);
    	em.getTransaction().commit();
    	
    	em.getTransaction().begin();
    	// adds all not listed (manual inserted) images to the database and the project
        scanProjectImageFolder(project, em);
        em.getTransaction().commit();
        em.close();
        
        return project.getId();
    }
    
    public static Long createOrUpdateLocation(Location location){
    	EntityManager em = persistenceManager.getEntityManager();
    	em.getTransaction().begin();
        em.persist(location);
    	em.getTransaction().commit();
        em.close();
        return location.getId();
    }

    public static Need getNeed(Long id)
    {
    	EntityManager em = persistenceManager.getEntityManager();
    	em.getTransaction().begin();
        Need need = em.find(Need.class, id);
        em.getTransaction().commit();
        em.close();
        return need;
    }

    public static Long createOrUpdateNeed(Need need) {
    	EntityManager em = persistenceManager.getEntityManager();
    	em.getTransaction().begin();
    	em.merge(need);
        em.persist(need);
        em.getTransaction().commit();
        return need.getId();
    }

    public static Location getLocation(Long id)
    {
    	EntityManager em = persistenceManager.getEntityManager();
    	em.getTransaction().begin();
        Location location =  em.find(Location.class, id);
        em.getTransaction().commit();
        em.close();
        return location;
    }
    
    public static List<Project> getProjectsOfLocation(Long locationId){
    	EntityManager em = persistenceManager.getEntityManager();
    	em.getTransaction().begin();

        TypedQuery<Project> query = em.createQuery(
                "SELECT p FROM Project p WHERE p.location.id = :locationId", Project.class);
        query.setParameter("locationId", locationId);
        List<Project> projects = query.getResultList();
        em.getTransaction().commit();
        em.close();
        return projects;
    }

	public static List<Location> getAllLocations() {
    	EntityManager em = persistenceManager.getEntityManager();
    	em.getTransaction().begin();

        TypedQuery<Location> query = em.createQuery(
                "SELECT c FROM Location c", Location.class);
        List<Location> locations = query.getResultList();
        em.getTransaction().commit();
        em.close();
        return locations;
    }

    public static Donation getDonation(Long id) {
    	EntityManager em = persistenceManager.getEntityManager();
    	em.getTransaction().begin();
        Donation donation = em.find(Donation.class, id);
        em.getTransaction().commit();
        em.close();
        return donation;
    }

	public static List<Host> getHosts() {
    	EntityManager em = persistenceManager.getEntityManager();
    	em.getTransaction().begin();
        TypedQuery<Host> query = em.createQuery(
                "SELECT h FROM Host h", Host.class);
        List<Host> hosts = query.getResultList();
        em.getTransaction().commit();
        em.close();
        return hosts;
    }

    public static Host getHost(Long id)
    {
    	EntityManager em = persistenceManager.getEntityManager();
    	em.getTransaction().begin();
        Host host =  em.find(Host.class, id);
        em.getTransaction().commit();
        em.close();
        return host;
    }

	public static List<User> getUsers() {
    	EntityManager em = persistenceManager.getEntityManager();
    	em.getTransaction().begin();
        TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u", User.class);
        List<User> users = query.getResultList();
        em.getTransaction().commit();
        em.close();
        return users;
    }

	public static Long createOrUpdateUser(User user) {
    	EntityManager em = persistenceManager.getEntityManager();
    	em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        return user.getId();
    }

    public static User getUser(Long id)
    {
    	EntityManager em = persistenceManager.getEntityManager();
    	em.getTransaction().begin();
        User user =  em.find(User.class, id);
        em.getTransaction().commit();
        em.close();
        return user;
    }

	public static List<Need> getNeeds() {
    	EntityManager em = persistenceManager.getEntityManager();
    	em.getTransaction().begin();
        TypedQuery<Need> query = em.createQuery(
                "SELECT n FROM Need n", Need.class);
        List<Need> needs = query.getResultList();
        em.getTransaction().commit();
        em.close();
        return needs;
    }

    public static List<Meeting> getMeetings() {
        
        EntityManager em = persistenceManager.getEntityManager();
        em.getTransaction().begin();
        TypedQuery<Meeting> query = em.createQuery(
        "SELECT m FROM Meeting m", Meeting.class);
        List<Meeting> meetings = query.getResultList();
        em.getTransaction().commit();
        em.close();
        return meetings;
    }


	public static Meeting getMeeting(Long meetingId) {
    	EntityManager em = persistenceManager.getEntityManager();
    	em.getTransaction().begin();
        Meeting meeting =  em.find(Meeting.class, meetingId);
        em.getTransaction().commit();
        em.close();
        return meeting;
	}

	public static long createOrUpdateMeeting(Meeting meeting) {
    	EntityManager em = persistenceManager.getEntityManager();
    	em.getTransaction().begin();
        em.persist(meeting);
    	em.getTransaction().commit();
    	
    	em.getTransaction().begin();
    	// adds all not listed (manual inserted) images to the database and the project
        scanProjectImageFolder(meeting, em);
        em.getTransaction().commit();
        em.close();
        
        return meeting.getId();
	}
}

