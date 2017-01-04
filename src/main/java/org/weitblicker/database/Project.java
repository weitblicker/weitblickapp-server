package org.weitblicker.database;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.weitblicker.Options;

import javax.persistence.GenerationType;


@SuppressWarnings("serial")
@Entity
@Table(name = "projects")
public class Project implements Serializable{
	
	public Project(){
		hosts = new HashSet<Host>();
	}
	
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
    	for(Image image : images){
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
	
	@ManyToMany
    @JoinTable(
            name="project_images",
            joinColumns=@JoinColumn(name="image_id", referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="project_id", referencedColumnName="id"))
	private List<Image> images = new LinkedList<Image>();
	
	public List<Image> getImages(){
		return images;
	}
	
	@JoinColumn(name = "preview_image")
	Image previewImage;
	
	public Image getPreviewImage(){
		if(previewImage != null){
			return previewImage;
		}
		if(!images.isEmpty()){
			return images.get(0);
		}
		return null;
	}
	
	public void setPreviewImage(Image image){
		if(images.contains(image)){
			
		}
	}
	
	// returns and adds a list of images are not in the database yet
	public List<Image> scanFolder(){
		String folderPath = "project-images/" + getId() + "/";
		File folder = new File(folderPath);
		
		// create directory if necessary 
		if(!folder.exists()){
			folder.mkdirs();
		}
		
		// check if the directory has been created successfully
		if(!folder.isDirectory()){
			System.out.println("Can not scan the project image folder for id: " + id + "!\n"
						+ "\"" + folderPath + "\" is not a directory!");
			throw new IllegalArgumentException("Can not scan the projet image folder for id: " + id + "!\n"
						+ "\"" + folderPath + "\" is not a directory!");
		}
		
		List<Image> list = new LinkedList<Image>();
		
		System.out.println("Scan folder:\"" + folder.getPath() + "\"");

		for(File file : folder.listFiles()){

			Image image = new Image(file);
			image.setName(file.getName());
			if(!images.contains(image)){
				System.out.println("Found:\"" + file.getName() + "\" as not listed!");
				list.add(image);
			}else{
				System.out.println("Found:\"" + file.getName() + "\" as already listed.");
			}
		}
		if(list.isEmpty()){
			System.out.println("No unlisted images found!");
		}
		return list;
	}
	
	public void addImage(Image image){
		images.add(image);
	}
	
	public Image removeImage(Long id){
		if(id == null) return null;
		
		Iterator<Image> iter = images.iterator();
		while(iter.hasNext()){
			Image image = iter.next();
			if(image.getImageId().equals(id)){
				System.out.println("remove project image from project - image: " + image);
				iter.remove();
				return image;
			}
		}
		return null;
	}
	
	public void setImages(List<Image> images){
		this.images = images;
	}
	
	@ManyToMany(mappedBy="projects")
	Set<Host> hosts;
	
	public boolean hasHost(Host host){
		return hosts.contains(host);
	}
	
	public void addHost(Host host){
		if(!hasHost(host)){
			hosts.add(host);
		}
		if(!host.hasProject(this)){
			host.addProject(this);
		}
	}

	public void removeHost(Host host) {
		if(hasHost(host)){
			hosts.remove(host);
		}
		if(host.hasProject(this)){
			host.removeProject(this);
		}
	}
	
	// removes connections from both sides
	public void clearHost(){
		Iterator<Host> hIter = hosts.iterator();
		while(hIter.hasNext()){
			Host host = hIter.next();
			Iterator<Project> pIter = host.projects.iterator();
			while(pIter.hasNext()){
				Project project = pIter.next();
				if(project == this){
					pIter.remove();
					break;
				}
			}
			hIter.remove();
		}
	}
	
	public List<Long> getHostIds(){
		List<Long> ids = new LinkedList<Long>();
		for(Host host : hosts){
			ids.add(host.getId());
		}
		return ids;
	}
	
	public void setHostIds(List<Long> ids){
		clearHost();
		for(Long id:ids){
			Host host = PersistenceHelper.getHost(id);
			if(host != null){
				addHost(host);
			}
		}
	}

	public List<Host> getHosts() {
		return new LinkedList<Host>(hosts);
	}
		
}