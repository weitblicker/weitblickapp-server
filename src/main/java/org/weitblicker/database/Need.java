package org.weitblicker.database;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Weitblick-DB Need Table Object, Defines demands in projects
 * @author nizzke
 * @since 15.10.16
 */

// TODO: 18.10.16 responsible: Bene V

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
    private long languageId;
    private long amount;
    private long current;

    // CONSTRUCTOR
    Need() {

    }

    Need(long nameNo, long descriptionShortNo, long descriptionLongNo, long projectId, long languageId) {
        this.nameNo = nameNo;
        this.descriptionShortNo = descriptionShortNo;
        this.descriptionLongNo = descriptionLongNo;
        this.projectId = projectId;
        this.languageId = languageId;
    }

    // TODO: 18.10.16 how to get and define the nameNo and others? Where to get the information and how can you decide which number to get?
            Need( String name, String descriptionShort, String descriptionLong, long projectId, long languageId ) {
        //Need( PersistenceHelper.setText( languageId, name ))
    }

    // GETTER
    public long getId() {
        return this.id;
    }

    public long getNameNo() {
        return this.nameNo;
    }

    public String getName(int languageId) {
        return null; // PersistenceHelper.getText(languageId,this.nameNo);
    }

    public long getDescriptionShortNo() {
        return this.descriptionShortNo;
    }
    public String getDescriptionShort(int languageId) {
        return null; //PersistenceHelper.getText(languageId,this.descriptionShortNo);
    }

    public long getDescriptionLongNo() {
        return this.descriptionLongNo;
    }
    public String getDescriptionLong(int languageId) {
        return null;// PersistenceHelper.getText(languageId, this.descriptionLongNo);
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
    public Need setNameNo( long nameNo ) {
        this.nameNo = nameNo;
        return this;
    }
    public Need setNameText( long languageId, String name ) {
        //this.nameNo = PersistenceHelper.setText(languageId,name,this.nameNo);
        return this;
    }
    public Need setDescriptionShortNo( long descriptionShortNo ) {
        //this.descriptionShortNo = descriptionShortNo;
        return this;
    }
    public Need setDescriptionShort( long languageId, String descriptionShort ) {
        //this.descriptionShortNo = PersistenceHelper.setText(languageId,descriptionShort,this.nameNo);
        return this;
    }

    public Need setDescriptionLongNo( long descriptionLongNo ) {
        this.descriptionLongNo = descriptionLongNo;
        return this;
    }
    public Need setDescriptionLong( long languageId, String descriptionLong ) {
        //this.descriptionLongNo = PersistenceHelper.setText( languageId, descriptionLong, this.descriptionLongNo );
        return this;
    }

}
