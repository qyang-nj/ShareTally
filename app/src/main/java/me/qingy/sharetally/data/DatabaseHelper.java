package me.qingy.sharetally.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by qing on 10/21/14.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "shareTally";

    private RuntimeExceptionDao<Tally, Integer> tallyDao = null;
    private RuntimeExceptionDao<Person, Integer> personDao = null;
    private RuntimeExceptionDao<Record, Integer> recordDao = null;
    private RuntimeExceptionDao<TallyParticipant, Integer> tallyParticipantDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, Tally.class);
            TableUtils.createTable(connectionSource, Person.class);
            TableUtils.createTable(connectionSource, Record.class);
            TableUtils.createTable(connectionSource, TallyParticipant.class);
            TableUtils.createTable(connectionSource, ParticipantWeight.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }

        Person currPerson = new Person();
        currPerson.setName(Person.CURRENT_USERNAME);
        getPersonDao().create(currPerson);
        Log.v(this.getClass().getName(), "Default user created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, Person.class, true /* ignoreErrors */);
            TableUtils.dropTable(connectionSource, Tally.class, true);
            TableUtils.dropTable(connectionSource, Record.class, true);
            TableUtils.dropTable(connectionSource, TallyParticipant.class, true);
            TableUtils.dropTable(connectionSource, ParticipantWeight.class, true);
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        super.close();
        tallyDao = null;
        personDao = null;
        recordDao = null;
    }

    public Person getCurrentPerson() {
        return getPersonDao().queryForId(1);
    }

    public RuntimeExceptionDao<Tally, Integer> getTallyDao() {
        if (tallyDao == null) {
            tallyDao = getRuntimeExceptionDao(Tally.class);
        }
        return tallyDao;
    }

    public RuntimeExceptionDao<Person, Integer> getPersonDao(){
        if (personDao == null) {
            personDao = getRuntimeExceptionDao(Person.class);
        }
        return personDao;
    }

    public RuntimeExceptionDao<Record, Integer> getRecordDao(){
        if (recordDao == null) {
            recordDao = getRuntimeExceptionDao(Record.class);
        }
        return recordDao;
    }

    public RuntimeExceptionDao<TallyParticipant, Integer> getTallyParticipantDao() {
        if (tallyParticipantDao == null) {
            tallyParticipantDao = getRuntimeExceptionDao(TallyParticipant.class);
        }
        return tallyParticipantDao;
    }
}
