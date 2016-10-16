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

    public String getName(int languageId) {
        return PersistenceHelper.getText(languageId,this.nameNo)
    }

    public long getDescriptionShortNo(int languageId) {
        return this.descriptionShortNo;
    }
    public String getDescriptionShort() {
        return PersistenceHelper.getText(languageId,this.descriptionShortNo);
    }

    public long getDescriptionLongNo() {
        return this.descriptionLongNo;
    }
    public String getDescriptionLong(int languageId) {
        return PersistenceHelper.getText(languageId,this.descriptionLongNo);
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
    public Project setName( int languageId, String nameText ) {
        this.nameNo = PersistenceHelper.setText(languageId,nameText,this.nameNo);
        return this;
    }
    public Project setDescriptionShortNo( long descriptionShortNo ) {
        this.descriptionShortNo = descriptionShortNo;
        return this;
    }
    public Project setDescriptionShort( int languageId, String descriptionShortText ) {
        this.descriptionShortNo = PersistenceHelper.setText(languageId,descriptionShortText,this.descriptionShortNo);
        return this;
    }

    public Project setDescriptionLongNo( long descriptionLongNo ) {
        this.descriptionLongNo = descriptionLongNo;
        return this;
    }
    public Project setDescriptionLong( int languageId, String descriptionLongText ) {
        this.descriptionLongNo = PersistenceHelper.setText(languageId,descriptionLongText,this.descriptionLongNo);
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
