package me.qingy.tallyfriend;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.Map;

import me.qingy.tallyfriend.model.Person;
import me.qingy.tallyfriend.model.Tally;


public class ResultActivity extends Activity {

    private ListView mLvResults;
    private Tally mTally;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        mLvResults = (ListView) findViewById(R.id.list);
        mLvResults.setEnabled(false); /* Disable selection */

        mTally = ObjectHolder.getTally();
        if (mTally == null) {
            throw new NullPointerException("Tally should not be not");
        }

        getActionBar().setTitle(mTally.getTitle());
        Map<Person, Tally.Result> result = mTally.calculate();
        mLvResults.setAdapter(new ResultAdapter(ResultActivity.this, mTally.getParticipants(), result));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
