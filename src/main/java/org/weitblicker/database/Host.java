package org.weitblicker.database;



import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Weitblick-DB Host Table Object, Lists Weitblick cities
 * @author Janis
 * @since 16.10.2016
 */

// TODO: 18.10.16 responsible: Bene V

@Entity
@Table( name = "hosts" )
public class Host
{
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private String email;
    private long locationId;

    public Host()
    {
    }

    Host( String name, String email, long locationId ) {
        this.name = name;
        this.email = email;
        this.locationId = locationId;
    }

    public long getId()
    {
        return id;
    }

    public void setId( long id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }

    public long getLocationId()
    {
        return locationId;
    }

    public void setLocationId( long locationId )
    {
        this.locationId = locationId;
    }
}
