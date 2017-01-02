package org.weitblicker.database;


import java.util.HashSet;
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
    
    public Long getId(){
    	return id;
    }
    
	@Column(name = "name")
    private String name;
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName(){
		return name;
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

    public Location getLocation()
    {
        return location;
    }
    
	public void setLocation(long id){
		this.location = Location.location(id);
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
    
    @ManyToMany
    @JoinTable(
        name="host_projects",
        joinColumns=@JoinColumn(name="project_id", referencedColumnName="id"),
        inverseJoinColumns=@JoinColumn(name="host_id", referencedColumnName="id"))
    private Set<Project> projects;
    
    public Host() { }
    
    public Host(String name, String email, Location location) {
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
