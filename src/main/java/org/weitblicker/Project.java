package org.weitblicker; /**
 * Created by benedikt on 15.10.16.
 */

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table( name = "projects" )
public class Project {

    // Constructor
    Project() {

    }

    Project(String name, String descriptionShort, String descriptionLong, String host, long locationId) {
        this.name = name;
        this.descriptionShort = descriptionShort;
        this.descriptionLong = descriptionLong;
        this.host = host;
        this.locationId = locationId;
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
    public String getdescriptionShort() {
        return this.descriptionShort;
    }
    public String getdescriptionLong() {
        return this.descriptionLong;
    }
    public String getHost() {
        return this.host;
    }
    public Long getLocationId() {return this.locationId; }

    public Project setId( long id ) {
        this.id = id;
        return this;
    }

    public Project setName( long locationId ) {
        this.locationId = locationId;
        return this;
    }
    public Project setName( String name ) {
        this.name = name;
        return this;
    }
    public Project setHost( String host ) {
        this.host = host;
        return this;
    }
    public Project setDescriptionShort( String descriptionShort ) {
        this.descriptionShort = descriptionShort;
        return this;
    }
    public Project setDescriptionLong( String descriptionLong ) {
        this.descriptionLong = descriptionLong;
        return this;
    }
    //Private stuff

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String descriptionShort;
    private String descriptionLong;
    private String host;
    private Long locationId;


}
