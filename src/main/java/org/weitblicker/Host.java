package org.weitblicker;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Weitblick-DB Host Table Object, Lists Weitblick cities
 * @author Janis
 * @since 16.10.2016
 */
@Entity
@Table( name = "hosts" )
public class Host
{
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private long locationId;

    public Host()
    {
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public long getLocationId()
    {
        return locationId;
    }

    public void setLocationId(long locationId)
    {
        this.locationId = locationId;
    }
}
