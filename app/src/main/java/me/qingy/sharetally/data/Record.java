package me.qingy.sharetally.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.Date;

import me.qingy.sharetally.Log.Logger;

/**
 * Created by qing on 10/22/14.
 */

@DatabaseTable
public class Record {
    public static final String KEY_ID = "record_id";
    public static final int ID_NEW = 0;

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String objId;

    @DatabaseField
    private Date updateAt;

    @DatabaseField
    private double amount;

    @DatabaseField
    private String label;

    @DatabaseField
    private Date date;

    @DatabaseField(canBeNull = false, foreign = true)
    private Person payer;

    @DatabaseField(foreign = true)
    private Tally tallyId;

    public Record() {
        /* for ORMLite */
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return this.label;
    }

    public void setCaption(String label) {
        this.label = label;
    }

    public double getAmount() {
        return this.amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Person getPayer() {
        return this.payer;
    }

    public void setPayer(Person payer) {
        this.payer = payer;
    }
}
