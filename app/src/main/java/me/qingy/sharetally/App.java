package me.qingy.sharetally;

import android.app.Application;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import me.qingy.sharetally.model.Person;
import me.qingy.sharetally.model.Record;
import me.qingy.sharetally.model.Tally;

/**
 * Created by YangQ on 9/17/2014.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Tally.class);
        ParseObject.registerSubclass(Record.class);
        ParseObject.registerSubclass(Person.class);
        //Parse.enableLocalDatastore(this);
        Parse.initialize(this, "JrAPNydFgOQlrzi6VYokNajNSbIhBHUZWuFWwTDA", "HvpG5ExQQ194SPAeikMo5KYfsIaih4fUlfquwdPB");
    }
}
