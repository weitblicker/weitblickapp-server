package org.weitblicker.database;

import java.security.Principal;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import org.weitblicker.SecurePasswordUtility;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.weitblicker.UserRole;

@Entity
@Table( name = "users" )
public class User implements Comparable<User>, Principal {
	
    @Transient
	SecurePasswordUtility securePwUtil = new SecurePasswordUtility();

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CUST_SEQ")
	@Column(name = "id")
    Long id;
    
	public Long getId() {
		return id;
	}
	
	@ManyToMany(mappedBy="maintainer")
	private Set<Host> hosts;
	
	@Column(name = "name", unique=true)
	private String name;
	
	@Column(name = "email", unique=true)
	private String email;
	
	@Column(name = "password")
    @JsonIgnore	
	private String passwordToken;
    
    public User(){
		this.hosts = new HashSet<Host>();
    }
	
	public User(String email, String name, String password){
		this.email = email;
		this.name = name;
		this.hosts = new HashSet<Host>();
		this.passwordToken = password;
	}
	
	public void addToHost(Host host){
		if(!hosts.contains(host)){
			hosts.add(host);
		}
		if(!host.maintains(this)){
			host.addUserAsMaintainer(this);
		}
	}
	
	@JsonIgnore
	public List<Host> getHosts(){
		return new LinkedList<Host>(hosts);
	}
	
	public List<Long> getHostIds(){
		List<Long> ids = new LinkedList<Long>();
		for(Host host : hosts){
			ids.add(host.getId());
		}
		return ids;
	}
	
	public void setHostIds(List<Long> hostIds){
		hosts.clear();
		for(Long id : hostIds){
			Host host = PersistenceHelper.getHost(id);
			addToHost(host);
		}
	}
	
	public boolean maintains(Host host){	
		return hosts.contains(host);
	}
    
    public void setPassword(String password){
    	passwordToken = securePwUtil.hash(password);
    }
    
    public boolean equalsPassword(String password){
    	return securePwUtil.authenticate(password, passwordToken);
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
	
	public String toString(){
		if(name != null && email != null && passwordToken != null)
			return name + "<"+ email +">";
		else
			return "User is undefined!";
	}



	// the user role will internally always be saved as an enum value
	// but you can set it with either the corresponding string value or the enum value directly
	// the getRole() will return a string for the prersistence to work

	@Column(name = "role")
	private String role;

	public String getRole(){
		return role;
	}
	
	public void setRole(String role){
		this.role = role;
	}
	public void setRole(UserRole usrRole){
		this.role = usrRole.name();
	}

	public boolean hasRole(UserRole otherRole){
		return otherRole == UserRole.valueOf(role);
	}
}
