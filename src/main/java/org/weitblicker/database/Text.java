package org.weitblicker.database;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Weitblick-DB table object, contains all language depeding texts in the app
 * @author benedikt
 * @since 16.10.16
 */

// TODO: 18.10.16 responsible: Bene V

@Entity
@Table( name = "texts" )
public class Text {

    @Id
    @GeneratedValue
    private long id;

    private long languageId;
    private String text;
    private long no;

    Text() {

    }

    Text(int languageId, String text, long no) {
        this.languageId = languageId;
        this.text = text;
        this.no = no;
    }

    public long getId() {
        return this.id;
    }
    public String getText() {
        return this.text;
    }
    public long getLanguageId() {
        return this.languageId;
    }
    public long getNo() {
        return this.no;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public void setLanguageId(long languageId)
    {
        this.languageId = languageId;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public void setNo(long no)
    {
        this.no = no;
    }
}
