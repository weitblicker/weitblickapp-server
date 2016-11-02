package org.weitblicker.database;



import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.weitblicker.Options;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CUST_SEQ")
	@Column(name = "id")
    Long id;
    
	@JoinColumn(name = "key_name")
    private LanguageString name;
	
	public void setName(final String name, final Locale language) {
		name().addText(language, name);
	}
	
	public String getName(){
		return getName(currentLanguage);
	}

	public String getName(final Locale language) {
		return name().getText(language);
	}
    
    LanguageString name(){
		return name != null ? name : (name = new LanguageString());
    }
    
    
	
	@Column(name = "email")
    private String email;
	
    public String getEmail()
    {
        return email;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }

	
	@JoinColumn(name = "key_location")
    private Location location;

    public Location getLocationId()
    {
        return location;
    }

    public void setLocation(Location location)
    {
        this.location = location;
    }
    
	
    @ManyToMany
    @JoinTable(
        name="host_maintainer",
        joinColumns=@JoinColumn(name="user_id", referencedColumnName="id"),
        inverseJoinColumns=@JoinColumn(name="host_id", referencedColumnName="id"))
    private Set<User> maintainer;
    
    @Transient
    public Locale currentLanguage = Options.DEFAULT_LANGUAGE;
    
    void setCurrentLanguage(Locale language){
    	currentLanguage = language;
    }
    
    public Host(){
    	
    }
    
    public Host( LanguageString name, String email, Location location) {
        this.name = name;
        this.email = email;
        this.location = location;
        maintainer = new HashSet<User>();
    }
    
    public void addUserAsMaintainer(User user){
		maintainer.add(user);
		if(!user.maintains(this)){
			user.addToHost(this);
    	}
    }
    
    public boolean maintains(User user){
    	return maintainer.contains(user);
    }
    
}
