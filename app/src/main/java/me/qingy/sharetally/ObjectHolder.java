package me.qingy.sharetally;

import me.qingy.sharetally.model.Person;
import me.qingy.sharetally.model.Record;
import me.qingy.sharetally.model.Tally;

/**
 * Created by YangQ on 10/10/2014.
 * <p/>
 * This class is used to pass ParseObject between Activities.
 */
public class ObjectHolder {
    private static Tally mTally;
    private static Record mRecord;
    private static Person mPerson;

    public static void setTally(Tally tally) {
        mTally = tally;
    }

    public static void resetTally() {
        setTally(null);
    }

    public static Tally getTally() {
        return mTally;
    }


    public static void setRecord(Record record) {
        mRecord = record;
    }

    public static void resetRecord() {
        setRecord(null);
    }

    public static Record getRecord() {
        return mRecord;
    }


    public static void setPerson(Person person) {
        mPerson = person;
    }

    public static void resetPerson() {
        setPerson(null);
    }

    public static Person getPerson() {
        return mPerson;
    }

    public static void reset() {
        resetTally();
        resetRecord();
        resetPerson();
    }
}
