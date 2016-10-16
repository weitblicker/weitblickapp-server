package org.weitblicker;

/**
 * Created by benedikt on 16.10.16.
 */

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table( name = "texts" )
public class Text {

    // Constructor
    Text() {

    }

    Text(String language, String text, long no) {
        this.language = language;
        this.text = text;
        this.no = no;
    }

    public long getId() {
        return this.id;
    }
    public String getText() {
        return this.text;
    }
    public String getLanguage() {
        return this.language;
    }
    public long getNo() {
        return this.no;
    }

    // members
    @Id
    @GeneratedValue
    private long id;

    private String language;
    private String text;
    private long no;

}
