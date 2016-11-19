package org.weitblicker.database;

/**
 * Weitblick-DB Location Table
 * @author Sebastian Pütz <spuetz@uos.de>
 * @since 16.10.16
 */

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table( name = "locations" )
public class Location {

    private String town;
	private int postalCode;
    private String street;
    private int number;
    private double longitude;
    private double latitude;
    private String country;
    private String addition;
    //public long commentNo;

    public Location(){}
	
    public Location(String town, Integer postalCode, String street,Integer number, Integer longitude, Integer latitude, String country, String addition) {
        this.town = town;
        this.postalCode = postalCode;
        this.street = street;
        this.number = number;
        this.longitude = longitude;
        this.latitude = latitude;
        this.country = country;
        this.addition = addition;
       // this.commentNo = commentNo;
    }
    
    public static Location location(String id){
    	return PersistenceHelper.getLocation(Long.valueOf(id));
    }

    public String getTown() {
        return this.town;
    }

    // members
    @Id
    @GeneratedValue
    private Long id;

    public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(int postalCode) {
		this.postalCode = postalCode;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getAddition() {
		return addition;
	}

	public void setAddition(String addition) {
		this.addition = addition;
	}

	public void setTown(String town) {
		this.town = town;
	}
	
	public String toString(){
		return "id: " + getId() + " – " + getStreet() 
			+ " " + getNumber() + ", " + getPostalCode()
			+ " " + getTown() + ", " + getCountry();
	}
}
