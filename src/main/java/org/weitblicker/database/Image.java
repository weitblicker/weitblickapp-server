package org.weitblicker.database;

import java.io.File;
import java.io.Serializable;
import java.util.Locale;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "images")
public class Image implements Serializable, Comparable<Image> {
	
	public Image(){}
	
	public Image(File file){
		setFile(file);
	}
	
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CUST_SEQ")
	@Column(name = "id")
    Long imageId;
    
    public Long getImageId(){
		return imageId;
    }
	
	@Transient
	Locale currentLanguage = Options.DEFAULT_LANGUAGE;
	
	@Column(name = "file")
	private File file;
	
	@JsonIgnore
	public File getFile(){
		return file;
	}
	
	@JsonIgnore
	public void setFile(File file){
		this.file = file;
	}
	
	public void setCurrentLanguage(Locale language){
		currentLanguage = language;
	}
	
	@Column(name = "uri")
	private String uri;
	
	public String getUri(){
		return uri;
	}
	
	public void setUri(String uri){
		this.uri = uri;
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
	
	public long getSize(){
		return file.length();
	}
	
	public String toString(){
		return getName() + " – " + getUri();
	}

	public int compareTo(Image o) {
		return getFile().getPath().compareTo(o.getFile().getPath());
	}
	
	@Override 
	public boolean equals(Object o) {
		if(!(o instanceof Image)) return false;
		
		Image image = (Image) o;
		return getFile().getPath().equals(image.getFile().getPath());
	}
	
	
}
