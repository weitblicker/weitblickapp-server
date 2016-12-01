package org.weitblicker.database;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.weitblicker.Options;

import javax.persistence.GenerationType;


@Entity
@Table(name = "projects")
public class Project implements Serializable{
	
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CUST_SEQ")
	@Column(name = "id")
    Long id;
    
    public Long getId(){
		return id;
    }
    
    @Transient
    private Locale currentLanguage = Options.DEFAULT_LANGUAGE;
    
    public String getLanguage(){
    	return currentLanguage.getLanguage();
    }    
    
    public void setCurrentLanguage(Locale language){
    	currentLanguage = language;
    	for(ProjectImage image : images){
    		image.setCurrentLanguage(language);
    	}
    }
	
	@OneToOne(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
	@JoinColumn(name = "key_name")
	private LanguageString name;

	public void setName(final String name, final Locale language) {
		name().addText(language, name);
	}
	
	public void setName(String name){
		setName(name, currentLanguage);
	}
	
	public String getName(){
		return getName(currentLanguage);
	}

	public String getName(final Locale language) {
		return name().getText(language);
	}

	private LanguageString name() {
		return name != null ? name : (name = new LanguageString());
	}
	
	@OneToOne(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
	@JoinColumn(name = "key_description")
	private LanguageString desc;
	
	public void setDesc(String desc, final Locale language) {
		desc().addText(language, desc);
	}
	
	public void setDesc(String desc){
		setDesc(desc, currentLanguage);
	}
	
	public String getDesc(){
		return getDesc(currentLanguage);
	}

	public String getDesc(final Locale language) {
		return desc().getText(language);
	}

	private LanguageString desc() {
		return desc != null ? desc : (desc = new LanguageString());
	}
	
	@OneToOne(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
	@JoinColumn(name = "key_abstract")
	private LanguageString abst;
	
	public void setAbst(final String abst, final Locale language) {
		abst().addText(language, abst);
	}
	
	public void setAbst(String abst){
		setAbst(abst, currentLanguage);
	}
	
	public String getAbst(){
		return getAbst(currentLanguage);
	}

	public String getAbst(final Locale language) {
		return abst().getText(language);
	}

	private LanguageString abst() {
		return abst != null ? abst : (abst = new LanguageString());
	}
	
	@ManyToOne
	@JoinColumn(name = "key_location")
	private Location location;
	
	public Location getLocation(){
		return location;
	}
	
	public void setLocation(long id){
		this.location = Location.location(id);
	}
	
	public void setLocation(Location location){
		this.location = location;
	}
	
	public String toString(){
		return "id: " + this.id + " name: " + this.getName(); 
	}
	
	@ElementCollection
	private List<ProjectImage> images = new LinkedList<ProjectImage>();
	
	public List<ProjectImage> getImages(){
		return images;
	}
	
	public void setImages(List<ProjectImage> images){
		this.images = images;
	}
	
	@ManyToMany(mappedBy="projects")
	private Set<Host> hosts;
		
}