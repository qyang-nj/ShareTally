package me.qingy.tallyfriend;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.parse.GetCallback;
import com.parse.ParseException;

import java.util.List;
import java.util.Map;

import me.qingy.tallyfriend.Log.Logger;
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

        String tallyId = getIntent().getStringExtra("TALLY_ID");
        if (tallyId == null) {
            throw new NullPointerException("Tally ID should not be not");
        }

        Tally.fetchTallyInBackground(tallyId, new GetCallback<Tally>() {
            @Override
            public void done(Tally tally, ParseException e) {
                if (e != null) {
                    Logger.e(e.getMessage());
                    return;
                }

                mTally = tally;
                getActionBar().setTitle(mTally.getTitle());

                Map<Person, Tally.Result> result = mTally.calculate();
                mLvResults.setAdapter(new ResultAdapter(ResultActivity.this, mTally.getParticipants(), result));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
