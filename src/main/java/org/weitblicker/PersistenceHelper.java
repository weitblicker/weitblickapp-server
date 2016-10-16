package org.weitblicker;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by Janis on 16.10.2016.
 */
public class PersistenceHelper
{
    static EntityManager emWeitblick = PersistenceManager.INSTANCE.getEntityManager( "weitblick" );
    //static EntityManager emApp = PersistenceManager.INSTANCE.getEntityManager( "app" );

    public static Text getText(long textNo, long languageId)
    {
        TypedQuery<Text> query = emWeitblick.createQuery(
                "SELECT c FROM Text c WHERE c.textNo = :textNo AND c.languageNo = : languageNo", Text.class);
        query.setParameter("textNo", textNo);
        query.setParameter("languageId", languageId);
        return query.getSingleResult();
    }

    /**
     * This method adds or changes text
     * @param text
     * @param languageId
     * @param textNo nullable
     * @return textNo
     */
    public static long setText(String text, int languageId, long textNo)
    {
        Text textObject = new Text(languageId, text, textNo);
        emWeitblick.persist(textObject);
        emWeitblick.flush();
        return textObject.getId();
    }

    public static Project getProject(long projectId)
    {
        return emWeitblick.find(Project.class, projectId);
    }

    public static Need getNeed(long needId)
    {
        return emWeitblick.find(Need.class, needId);
    }

    public static Location getLocation(long locationId)
    {
        return emWeitblick.find(Location.class, locationId);
    }

    public static Donation getDonation(long donationId)
    {
        return emWeitblick.find(Donation.class, donationId);
    }

    public static List<Projects_Hosts> getProjektHostIds(long projectId)
    {
        TypedQuery<Projects_Hosts> query = emWeitblick.createQuery(
                "SELECT c FROM Text c WHERE c.projectId = :projectId", Projects_Hosts.class);
        query.setParameter("projectId", projectId);
        return query.getResultList();
    }

    public static Host getHost(long hostId)
    {
        return emWeitblick.find(Host.class, hostId);
    }
}
