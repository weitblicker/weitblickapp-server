package org.weitblicker.database;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@SuppressWarnings("serial")
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

	public void addText(Locale language, String text) {
		getMap().put(language.toString(), text);
	}
	
    public String getText(Locale language){
    	return getMap().get(language.toString());
    }
}

