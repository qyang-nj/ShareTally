package me.qingy.sharetally;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by YangQ on 9/17/2014.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //Parse.enableLocalDatastore(this);
        Parse.initialize(this, "JrAPNydFgOQlrzi6VYokNajNSbIhBHUZWuFWwTDA", "HvpG5ExQQ194SPAeikMo5KYfsIaih4fUlfquwdPB");
    }
}
