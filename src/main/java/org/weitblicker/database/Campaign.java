package org.weitblicker.database;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Date;


// TODO: 18.10.16 responsible (class + helper): nizzke

/**
 * @author nizzke
 * @since 18.10.16.
 */
@Entity
@Table( name = "campaigns" )
public class Campaign {

    // LOCAL VARIABLES
    @Id
    @GeneratedValue
    private long id;

    private long nameNo;
    private long descriptionShortNo;
    private long descriptionLongNo;
    private LocalDateTime start;
    private LocalDateTime end;
    private long amount;
    private long needId;

    // -------------------------- MAIN CONSTRUCTOR ----------------------------------------
    Campaign(long languageId, long nameNo, long descriptionShortNo, long descriptionLongNo, LocalDateTime start, LocalDateTime end, long amount, long needId) {
        this.nameNo = nameNo;
        this.descriptionShortNo = descriptionShortNo;
        this.descriptionLongNo = descriptionLongNo;
        this.start = start;
        this.end = end;
        this.amount=amount;
        this.needId = needId;
    }

    // -------------------------- NULL CONSTRUCTOR ----------------------------------------
    Campaign() {

    }

    // ------------------------------- GETTER ---------------------------------------------
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
        return PersistenceHelper.getText(languageId,this.descriptionLongNo);
    }

    public LocalDateTime getStart() {
        return this.start;
    }

    public LocalDateTime getEnd() {
        return this.end;
    }

    public long getAmount() {
        return this.amount;
    }

    public long getNeedId() {
        return this.needId;
    }


    // ------------------------------- SETTER ---------------------------------------------
    public Campaign setId( long id ) {
        this.id = id;
        return this;
    }

    public Campaign setNameNo( long nameNo ) {
        this.nameNo = nameNo;
        return this;
    }
    public Campaign setName( int languageId, String name ) {
        this.nameNo = PersistenceHelper.setText(languageId,name,this.nameNo);
        return this;
    }
    public Campaign setDescriptionShortNo( long descriptionShortNo ) {
        this.descriptionShortNo = descriptionShortNo;
        return this;
    }
    public Campaign setDescriptionShort( int languageId, String descriptionShort ) {
        this.descriptionShortNo = PersistenceHelper.setText(languageId,descriptionShort,this.descriptionShortNo);
        return this;
    }

    public Campaign setDescriptionLongNo( long descriptionLongNo ) {
        this.descriptionLongNo = descriptionLongNo;
        return this;
    }
    public Campaign setDescriptionLong( int languageId, String descriptionLong ) {
        this.descriptionLongNo = PersistenceHelper.setText(languageId,descriptionLong,this.descriptionLongNo);
        return this;
    }

    public Campaign setStart( LocalDateTime start ) {
        this.start = start;
        return this;
    }

    public Campaign setEnd( LocalDateTime end ) {
        this.end = end;
        return this;
    }

    public Campaign setAmount( long amount ) {
        this.amount = amount;
        return this;
    }

    public Campaign setNeedId( long needId ) {
        this.needId = needId;
        return this;
    }


}

