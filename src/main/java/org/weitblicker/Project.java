package org.weitblicker; /**
 * Created by benedikt on 15.10.16.
 */

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table( name = "projects" )
public class Project {

    // Constructor
    Project() {

    }

    Project(long nameNo, long descriptionShortNo, long descriptionLongNo, long locationId) {
        this.nameNo = nameNo;
        this.descriptionShortNo = descriptionShortNo;
        this.descriptionLongNo = descriptionLongNo;
        this.locationId = locationId;
    }

    public long getId() {
        return this.id;
    }

    public long getNameNo() {
        return this.nameNo;
    }
    public String getNameText() {
        return "coming soon";
        //return sql_getText(languageId,this.nameNo)
    }

    public long getDescriptionShortNo() {
        return this.descriptionShortNo;
    }
    public String getDescriptionShortText() {
        return "coming soon";
        //return sql_getText(languageNo,this.descriptionShortNo)
    }

    public long getDescriptionLongNo() {
        return this.descriptionLongNo;
    }
    public String getDescriptionLongText() {
        return "coming soon";
        //return sql_getText(languageNo,this.descriptionLongNo)
    }

    public long getLocationId() {
        return this.locationId;
    }

    public Project setId( long id ) {
        this.id = id;
        return this;
    }

    public Project setLocationId( long locationId ) {
        this.locationId = locationId;
        return this;
    }
    public Project setNameNo( long nameNo ) {
        this.nameNo = nameNo;
        return this;
    }
    public Project setNameText( String nameText ) {
        //this.nameNo = sql_setText(languageNo,nameText)
        return this;
    }
    public Project setDescriptionShortNo( long descriptionShortNo ) {
        this.descriptionShortNo = descriptionShortNo;
        return this;
    }
    public Project setDescriptionShort( String descriptionShortText ) {
        //this.descriptionShortNo = sql_setText(languageNo,descriptionShortText);
        return this;
    }

    public Project setDescriptionLongNo( long descriptionLongNo ) {
        this.descriptionLongNo = descriptionLongNo;
        return this;
    }
    public Project setDescriptionLong( String descriptionShortText ) {
        //this.descriptionLong = sql_setText(languageNo,descriptionShortText)
        return this;
    }
    //Private stuff

    @Id
    @GeneratedValue
    private long id;

    private long nameNo;
    private long descriptionShortNo;
    private long descriptionLongNo;
    private long locationId;


}
