package org.weitblicker.database;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author Sebastian Pütz <spuetz@uos.de>
 * @since 01.11.16.
 */

@Entity
@Table( name = "users" )
public class User implements Comparable<User>, Principal {
	
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CUST_SEQ")
	@Column(name = "id")
    Long id;
	
	@ManyToMany(mappedBy="maintainer")
	private Set<Host> hosts;
	
	@Column(name = "name", unique=true)
	private String name;
	
	@Column(name = "email", unique=true)
	private String email;
	
    @JsonIgnore	
	@Column(name = "password")
	private String password;
    
    public User(){
    	
    }
	
	public User(String email, String name, String password){
		this.email = email;
		this.name = name;
		this.hosts = new HashSet<Host>();
		this.password = password;
	}
	
	public void addToHost(Host host){
		hosts.add(host);
		if(!host.maintains(this)){
			host.addUserAsMaintainer(this);
		}
	}
	
	public boolean maintains(Host host){	
		return this.hosts.contains(host);
	}
	
    @JsonIgnore
	public String getPassword(){
		return password;
    }
    
    public void setPassword(String password){
    	this.password = password;
    }
    
    public String getName(){
    	return name;
    }
    
    public void setName(String name){
    	this.name = name;
    }
    
    public String getEmail(){
    	return email;
    }
    
    public void setEmail(String email){
    	this.email = email;
    }

	@Override
	public int compareTo(User o) {
		return name.compareTo(o.getName());
	}
	
	public int hashCode(){
		return name.hashCode();
	}
	
	public String toString(){
		if(name != null && email != null && password != null)
			return name + "<"+ email +">";
		else
			return "User is undefined!";
	}
}
