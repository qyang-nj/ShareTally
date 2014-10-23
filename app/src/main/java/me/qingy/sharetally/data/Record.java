package me.qingy.sharetally.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by qing on 10/22/14.
 */

@DatabaseTable
public class Record {

    @DatabaseField(generatedId = true)
    private long id;

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

    public Record() {
        /* for ORMLite */
    }
}
