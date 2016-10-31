package org.weitblicker.database;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

@Entity
@Table(name = "texts")
public class LanguageString extends AbstractLanguageString {

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "language")
    @CollectionTable(name = "texts_lang", joinColumns = @JoinColumn(name = "string_id"))
    @Column(name = "text")
    
    private Map<String, String> map = new HashMap<String, String>();

    public LanguageString() {
        super();
    }

    public LanguageString(final String language, final String text) {
        addText(language, text);
    }

    public Map<String, String> getMap() {
        return map;
    }
}
