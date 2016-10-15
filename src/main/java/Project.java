/**
 * Created by benedikt on 15.10.16.
 */

import com.sun.javafx.beans.IDProperty;

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

    Project(String name, String descrShort, String descrLong, String host) {
        this.name = name;
        this.descrShort = descrShort;
        this.descrLong = descrLong;
        this.host = host;
    }

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
    public String getDescrShort() {
        return this.descrShort;
    }
    public String getDescrLong() {
        return this.descrLong;
    }
    public String getHost() {
        return this.host;
    }

    public Project setName( String name ) {
        this.name = name;
        return this;
    }
    public Project setHost( String host ) {
        this.host = host;
        return this;
    }
    public Project setDescrShort( String descrShort ) {
        this.descrShort = descrShort;
        return this;
    }
    public Project setDescrLong( String descrLong ) {
        this.descrShort = descrLong;
        return this;
    }
    //Private stuff

    @Id
    @GeneratedValue
    private Integer id;

    private String name;
    private String descrShort;
    private String descrLong;
    private String host;


}
