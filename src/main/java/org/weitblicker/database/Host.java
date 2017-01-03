package org.weitblicker.database;


import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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

import com.fasterxml.jackson.annotation.JsonIgnore;

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
        inverseJoinColumns=@JoinColumn(name="host_id", referencedColumnName="id")) Set<Project> projects;
    
    public Host() {
    	maintainer = new HashSet<User>();
    	projects = new HashSet<Project>();
    }
    
    public Host(String name, String email, Location location) {
        this.name = name;
        this.email = email;
        this.location = location;
        maintainer = new HashSet<User>();
    }
    
    public void addUserAsMaintainer(User user){
    	if(!maintains(user)){
    		maintainer.add(user);
    	}
		if(!user.maintains(this)){
			user.addToHost(this);
    	}
    }
    
    public boolean maintains(User user){
    	return maintainer.contains(user);
    }
    
    @JsonIgnore
    public List<User> getMaintainers(){
    	return new LinkedList<User>(maintainer);
    }
    
    public List<Project> getProjects(){
    	return new LinkedList<Project>(projects);
    }

	public boolean hasProject(Project project) {
		return projects.contains(project);
	}
	
	public void addProject(Project project){
		if(!hasProject(project)){
			projects.add(project);
		}
		if(!project.hasHost(this)){
			project.addHost(this);
		}
	}
	
	
	// removes connections from both sides
	public void clearProjects(){
		Iterator<Project> pIter = projects.iterator();
		while(pIter.hasNext()){
			Project project = pIter.next();
			Iterator<Host>hIter = project.hosts.iterator();
			while(hIter.hasNext()){
				Host host = hIter.next();
				if(host == this){
					hIter.remove();
				}
			}
			pIter.remove();
		}
	}
	
	public void removeProject(Project project){
		if(project.hasHost(this)){
			project.removeHost(this);
		}
		if(hasProject(project)){
			projects.remove(project);
		}
	}
	
	public void setProjectIds(List<Long> ids){
		clearProjects();
		for(Long id:ids){
			Project project = PersistenceHelper.getProject(id);
			if(project != null)
				addProject(project);
			// TODO throw error if null
		}
	}
    
}
