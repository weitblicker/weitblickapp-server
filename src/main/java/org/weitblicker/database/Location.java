package org.weitblicker.database;

import java.util.Locale;

import javax.persistence.CascadeType;
import javax.persistence.Column;

/**
 * Weitblick-DB Location Table
 * @author Sebastian Pütz <spuetz@uos.de>
 * @since 16.10.16
 */

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.weitblicker.Options;

@Entity
@Table( name = "locations" )
public class Location {

    public Location(){}
	
    public static Location location(Long id){
    	return PersistenceHelper.getLocation(id);
    }
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CUST_SEQ")
	@Column(name = "id")
    private Long id;
    public Long getId() { return id; }
	public void setId(long id) { this.id = id; }
    
	@Transient
	private Locale currentLanguage = Options.DEFAULT_LANGUAGE;

    public void setCurrentLanguage(Locale language){
    	this.currentLanguage = language;
    }
    
	
	@OneToOne(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
	@JoinColumn(name = "key_town")
    private LanguageString town;
    private LanguageString town(){
    	return town != null ? town : (town = new LanguageString());
    }

    public String getTown(Locale language) {
        return town().getText(language);
    }
    
    public String getTown(){
    	return getTown(currentLanguage);
    }
    
    public void setTown(String town){
    	town().addText(currentLanguage, town);
    }

	@Column(name = "postalCode")
	private String postalCode;
	public String getPostalCode(){ return postalCode;	}
	public void setPostalCode(String postalCode){ this.postalCode = postalCode; } 
	
	@OneToOne(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
	@JoinColumn(name = "key_street")
	private LanguageString street;
	private LanguageString street(){
		return street != null ? street : (street = new LanguageString());
	}
	
	public String getStreet(Locale language){
		return street().getText(language);
	}
	
	public String getStreet(){
		return getStreet(currentLanguage);
	}
	
	public void setStreet(String street){
		street().addText(currentLanguage, street);
	}
	
	@Column(name = "number")    
    private int number;
    public int getNumber(){ return number; }
    public void setNumber(int number){ this.number = number; }

	@Column(name = "longitude")    
    private double longitude;
	public double getLongitude(){ return longitude; } 
	public void setLongitude(double longitude){ this.longitude = longitude; }
	
	@Column(name = "latitude")    
    private double latitude;
	public double getLatitude(){ return latitude; }
	public void setLatitude(double latitude){ this.latitude = latitude; }
	
	@OneToOne(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
	@JoinColumn(name = "key_country")
	private LanguageString country;
	private LanguageString country(){
		return country != null ? country : (country = new LanguageString());
	}
	
	public String getCountry(Locale language){
		return country().getText(language);
	}
	
	public String getCountry(){
		return getCountry(currentLanguage);
	}
	
	public void setCountry(String country){
		country().addText(currentLanguage, country);
	}
	
	@OneToOne(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
	@JoinColumn(name = "key_addition")
	private LanguageString addition;
	private LanguageString addition(){
		return addition != null ? addition : (addition = new LanguageString());
	}
	
	public String getAddition(Locale addition){
		return addition().getText(addition);
	}
	
	public String getAddition(){
		return getAddition(currentLanguage);
	}
	
	public void setAddition(String addition){
		addition().addText(currentLanguage, addition);
	}
	
	@OneToOne(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
	@JoinColumn(name = "key_comment")  
    private LanguageString comment;
    private LanguageString comment(){
    	return comment != null ? comment : (comment = new LanguageString());
    }
    
    public String getComment(Locale language){
    	return comment().getText(language);
    }
    
    public String getComment(){
    	return getComment(currentLanguage);
    }
    
    public void setComment(String comment){
    	comment().addText(currentLanguage, comment);
    }

	public String toString(){
		return "id: " + getId() + " – " + getStreet() 
			+ " " + getNumber() + ", " + getPostalCode()
			+ " " + getTown() + ", " + getCountry();
	}
}
