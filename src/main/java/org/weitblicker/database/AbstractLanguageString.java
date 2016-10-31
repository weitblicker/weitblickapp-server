package org.weitblicker.database;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@MappedSuperclass
public abstract class AbstractLanguageString implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Version
	private Integer version;

	public AbstractLanguageString() {}

	public AbstractLanguageString(String lang, String text) {}

	protected abstract Map<String, String> getMap();

	public void addText(String lang, String text) {
		System.out.println("Language String" + lang);
		getMap().put(lang, text);
	}
	
    public String getText(String language){
    	return getMap().get(language);
    }
}

