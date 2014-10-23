package me.qingy.sharetally.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by qing on 10/23/14.
 */

@DatabaseTable
public class TallyParticipant {

    public static final String FIELD_ID = "id";
    public final static String FIELD_TALLY_ID = "user_id";
    public final static String FIELD_PERSON_ID = "post_id";

    @DatabaseField(generatedId = true, columnName = FIELD_ID)
    private long id;

    @DatabaseField(foreign = true, columnName = FIELD_TALLY_ID)
    private Tally tally;

    @DatabaseField(foreign = true, columnName = FIELD_PERSON_ID)
    private Person participant;

    TallyParticipant() {
        /* for ORMLite */
    }

    public TallyParticipant(Tally t, Person p) {
        tally = t;
        participant = p;
    }
}
