package me.qingy.tallyfriend.model;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import me.qingy.tallyfriend.Log.Logger;

/**
 * Created by YangQ on 9/17/2014.
 */
@ParseClassName("Person")
public class Person extends ParseObject {
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_DELETED = "deleted";

    public Person() {

    }

    public Person(String name) {
        setName(name);
    }

    public static void fetchPersonInBackground(String id, GetCallback<Person> cb) {
        ParseQuery<Person> query = ParseQuery.getQuery(Person.class);
        query.fromLocalDatastore();
        query.getInBackground(id, cb);
    }

    public static void fetchPersonListInBackground(FindCallback<Person> cb) {
        ParseQuery<Person> query = ParseQuery.getQuery(Person.class);
        query.fromLocalDatastore();
        query.whereDoesNotExist(KEY_DELETED);
        query.findInBackground(cb);
    }

    public void pin() {
        try {
            super.pin();
            super.saveEventually();
        } catch (ParseException e) {
            Logger.e(e.getMessage());
        }
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public String getEmail() {
        return getString(KEY_EMAIL);
    }

    public void setEmail(String email) {
        put(KEY_EMAIL, email);
    }

    public void remove() {
        put(KEY_DELETED, true);
    }
    //private static final String KEY_PARSE_USER = "parseUser";

    //private ParseUser user;
}
