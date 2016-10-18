package org.weitblicker;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Weitblick-DB Need Table Object, Defines demands in projects
 * @author nizzke
 * @since 15.10.16
 */
@Entity
@Table( name = "needs" )
public class Need {

    // LOCAL VARIABLES
    @Id
    @GeneratedValue
    private long id;

    private long nameNo;
    private long descriptionShortNo;
    private long descriptionLongNo;
    private long projectId;

    // CONSTRUCTOR
    Need() {

    }

    Need(long nameNo, long descriptionShortNo, long descriptionLongNo, long projectId) {
        this.nameNo = nameNo;
        this.descriptionShortNo = descriptionShortNo;
        this.descriptionLongNo = descriptionLongNo;
        this.projectId = projectId;
    }

    // GETTER
    public long getId() {
        return this.id;
    }

    public long getNameNo() {
        return this.nameNo;
    }

    public String getName(int languageId) {
        return PersistenceHelper.getText(languageId,this.nameNo);
    }

    public long getDescriptionShortNo() {
        return this.descriptionShortNo;
    }
    public String getDescriptionShort(int languageId) {
        return PersistenceHelper.getText(languageId,this.descriptionShortNo);
    }

    public long getDescriptionLongNo() {
        return this.descriptionLongNo;
    }
    public String getDescriptionLong(int languageId) {
        return PersistenceHelper.getText(languageId, this.descriptionLongNo);
    }

    public long getProjectId() {
        return this.projectId;
    }

    // SETTER
    public Need setId( long id ) {
        this.id = id;
        return this;
    }

    public Need setProjectId( long projectId ) {
        this.projectId = projectId;
        return this;
    }
    public Need setNameNo( int nameNo ) {
        this.nameNo = nameNo;
        return this;
    }
    public Need setNameText( int languageId, String name ) {
        this.nameNo = PersistenceHelper.setText(languageId,name,this.nameNo);
        return this;
    }
    public Need setDescriptionShortNo( Integer descriptionShortNo ) {
        this.descriptionShortNo = descriptionShortNo;
        return this;
    }
    public Need setDescriptionShort( int languageId, String descriptionShort ) {
        this.descriptionShortNo = PersistenceHelper.setText(languageId,descriptionShort,this.nameNo);
        return this;
    }

    public Need setDescriptionLongNo( Integer descriptionLongNo ) {
        this.descriptionLongNo = descriptionLongNo;
        return this;
    }
    public Need setDescriptionLong( int languageId, String descriptionLong ) {
        this.descriptionLongNo = PersistenceHelper.setText(languageId, descriptionLong, this.descriptionLongNo);
        return this;
    }

}
