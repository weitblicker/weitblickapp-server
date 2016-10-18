package org.weitblicker.database;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * App-DB Donation Table Object
 * @author Janis
 * @since 16.10.2016.
 */
@Entity
@Table( name = "donations" )
public class Donation
{
    @Id
    @GeneratedValue
    private long id;
    private long needId;
    private int amount;
    private String comment;

    public Donation()
    {
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public long getNeedId()
    {
        return needId;
    }

    public void setNeedId(long needId)
    {
        this.needId = needId;
    }

    public int getAmount()
    {
        return amount;
    }

    public void setAmount(int amount)
    {
        this.amount = amount;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }
}
