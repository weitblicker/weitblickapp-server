package org.weitblicker.database;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

@Entity
@Table(name = "texts")
public class LanguageString extends AbstractLanguageString {

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "language", length = 50)
    @CollectionTable(name = "texts_lang", joinColumns = @JoinColumn(name = "string_id"))
    @Column(name = "text", length = 1000000)
    @Lob
    private Map<String, String> map = new HashMap<String, String>();

    public LanguageString() {
        super();
    }

    public LanguageString(Locale language, final String text) {
        addText(language, text);
    }

    public Map<String, String> getMap() {
        return map;
    }
}
