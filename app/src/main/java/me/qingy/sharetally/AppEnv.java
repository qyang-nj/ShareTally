package me.qingy.sharetally;

import android.content.Context;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import me.qingy.sharetally.model.Person;

/**
 * Created by qing on 10/21/14.
 */
public class AppEnv {
    private static boolean isInited = false;
    private static Person mCurrentPerson;
    private static Context mContext;

    public static final String CURRENT_USER_NAME = "__USER_MYSELF__";

    public static void init(Context context) {
        if (isInited) {
            return;
        }

        mContext = context;

        final ParseUser currUser = ParseUser.getCurrentUser();
        if (currUser == null) {
            throw new NullPointerException("Current user should not be null.");
        }

        final String KEY_PERSON_ID = "person_id";
        String personId = currUser.getString(KEY_PERSON_ID);
        if (personId == null) {
            final Person me = new Person(currUser);
            me.setName(CURRENT_USER_NAME);
            mCurrentPerson = me;
            me.saveEventually(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        currUser.put(KEY_PERSON_ID, me.getObjectId());
                    }
                }
            });

        } else {
            Person.fetchPersonInBackground(personId, new GetCallback<Person>() {
                @Override
                public void done(Person person, ParseException e) {
                    mCurrentPerson = person;
                }
            });
        }

        isInited = true;
    }

    public static Person getCurrentPerson() {
        if (mCurrentPerson == null) {
            throw new NullPointerException("You have to initialize first.");
        }
        return mCurrentPerson;
    }

    public static String getCurrentPersonName() {
        return mContext.getString(R.string.myself);
    }

    private AppEnv() {

    }
}
