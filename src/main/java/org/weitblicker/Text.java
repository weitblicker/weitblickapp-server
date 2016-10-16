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
    public int getLanguageId() {
        return this.languageId;
    }
    public long getNo() {
        return this.no;
    }

    // members
    @Id
    @GeneratedValue
    private long id;

    private int languageId;
    private String text;
    private long no;

}