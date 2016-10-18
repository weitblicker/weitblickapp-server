package org.weitblicker.database;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Weitblick-DB table object, associates projects with hosts
 * @author Janis
 * @since 16.10.16
 */
@Entity
@Table( name = "projects_hosts" )
public class Project_Host
{
    @Id
    @GeneratedValue
    private long id;
    private long projectId;
    private long locationId;

    public Project_Host()
    {
    }
}
