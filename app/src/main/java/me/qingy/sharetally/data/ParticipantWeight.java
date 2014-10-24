package me.qingy.sharetally.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by qing on 10/23/14.
 */

@DatabaseTable
public class ParticipantWeight {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true)
    private Person pariticipant;

    @DatabaseField
    private double weight;

    @DatabaseField(foreign = true)
    private Record record;

    ParticipantWeight() {
        /* for ORMLite */
    }

    public ParticipantWeight(Record r, Person p, double weight) {
        this.record = r;
        this.pariticipant = p;
        this.weight = weight;
    }

    public Person getParticipant() {
        return this.pariticipant;
    }

    public double getWeight() {
        return this.weight;
    }
}
