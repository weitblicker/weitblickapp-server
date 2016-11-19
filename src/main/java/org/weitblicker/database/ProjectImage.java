package org.weitblicker.database;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.weitblicker.Options;

public class ProjectImage implements Serializable {
	
	public ProjectImage(){
		
	}
	
	@Transient
	Locale currentLanguage = Options.DEFAULT_LANGUAGE;
	
	public void setCurrentLanguage(Locale language){
		currentLanguage = language;
	}
	
	private String uri;

	public URI getURIObject(){
		try {
			return new URI(uri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	String getUri(){
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
}
