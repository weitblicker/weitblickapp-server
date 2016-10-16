package org.weitblicker; /**
 * Created by nizzke on 15.10.16.
 */

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table( name = "needs" )
public class Need {

    // Constructor
    Need() {

    }

    Need(long nameNo, long descriptionShortNo, long descriptionLongNo, long projectId) {
        this.nameNo = nameNo;
        this.descriptionShortNo = descriptionShortNo;
        this.descriptionLongNo = descriptionLongNo;
        this.projectId = projectId;

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

    public long getProjectId() {
        return this.projectId;
    }

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
    public Need setNameText( String nameText ) {
        //this.nameNo = sql_setText(languageNo,nameText)
        return this;
    }
    public Need setDescriptionShortNo( Integer descriptionShortNo ) {
        this.descriptionShortNo = descriptionShortNo;
        return this;
    }
    public Need setDescriptionShort( String descriptionShortText ) {
        //this.descriptionShortNo = sql_setText(languageNo,descriptionShortText);
        return this;
    }

    public Need setDescriptionLongNo( Integer descriptionLongNo ) {
        this.descriptionLongNo = descriptionLongNo;
        return this;
    }
    public Need setDescriptionLong( String descriptionShortText ) {
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
    private long projectId;


}
