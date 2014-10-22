package me.qingy.sharetally.model;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import me.qingy.sharetally.Log.Logger;

/**
 * Created by YangQ on 9/17/2014.
 */
@ParseClassName("Person")
public class Person extends ParseObject {
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_DELETED = "deleted";
    private static final String KEY_PARSE_USER = "parseUser";
    private static final String KEY_HIDDEN = "hidden";

    public Person() {

    }

    public Person(String name) {
        super();
        setName(name);
    }

    public Person(ParseUser user) {
        super();
        setParseUser(user);
        setName(user.getUsername());
        setEmail(user.getEmail());
        put(KEY_HIDDEN, true);
    }

    public static void fetchPersonInBackground(String id, GetCallback<Person> cb) {
        ParseQuery<Person> query = ParseQuery.getQuery(Person.class);
        //query.fromLocalDatastore();
        query.getInBackground(id, cb);
    }

    public static void fetchPersonListInBackground(FindCallback<Person> cb, List<String> excludedIds) {
        ParseQuery<Person> query = ParseQuery.getQuery(Person.class);
        //query.fromLocalDatastore();
        query.whereDoesNotExist(KEY_DELETED);
        query.whereDoesNotExist(KEY_HIDDEN);
        query.orderByAscending(KEY_NAME);
        if (excludedIds != null) {
            query.whereNotContainedIn("objectId", excludedIds);
        }
        query.findInBackground(cb);
    }

    public void submit() {
        super.saveEventually();
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

    public void setParseUser(ParseUser user) {
        put(KEY_PARSE_USER, user);
    }

    public ParseUser getParseUser() {
        return getParseUser(KEY_PARSE_USER);
    }
}
