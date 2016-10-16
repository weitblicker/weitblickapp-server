package org.weitblicker;

/**
 * Created by benedikt on 16.10.16.
 */

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table( name = "locations" )
public class Location {

    // Constructor
    Location() {

    }

    Location(String town, Integer postalCode, String street,Integer number, Integer longitude, Integer latitude, String country, String addition, long commentNo) {
        this.town = town;
        this.postalCode = postalCode;
        this.street = street;
        this.number = number;
        this.longitude = longitude;
        this.latitude = latitude;
        this.country = country;
        this.addition = addition;
        this.commentNo = commentNo;

    }

    public String getTown() {
        return this.town;
    }

    // members
    @Id
    @GeneratedValue
    private long id;

    private String town;
    private Integer postalCode;
    private String street;
    private Integer number;
    private Integer longitude;
    private Integer latitude;
    private String country;
    private String addition;
    private long commentNo;
}
