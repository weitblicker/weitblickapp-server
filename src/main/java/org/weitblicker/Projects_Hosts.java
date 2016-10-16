package org.weitblicker;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Janis on 16.10.2016.
 */
@Entity
@Table( name = "projects_hosts" )
public class Projects_Hosts
{
    @Id
    @GeneratedValue
    private long id;
    private long projectId;
    private long locationId;

    public Projects_Hosts()
    {
    }
}
