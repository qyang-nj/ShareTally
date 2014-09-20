package me.qingy.tallyfriend;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import me.qingy.tallyfriend.model.Person;
import me.qingy.tallyfriend.model.Record;
import me.qingy.tallyfriend.model.Tally;

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
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "JrAPNydFgOQlrzi6VYokNajNSbIhBHUZWuFWwTDA", "HvpG5ExQQ194SPAeikMo5KYfsIaih4fUlfquwdPB");
    }
}
