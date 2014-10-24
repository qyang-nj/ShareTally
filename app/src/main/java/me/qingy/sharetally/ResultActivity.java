package me.qingy.sharetally;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import java.util.Map;

import me.qingy.sharetally.data.DatabaseHelper;
import me.qingy.sharetally.data.Person;
import me.qingy.sharetally.data.Tally;


public class ResultActivity extends OrmLiteBaseActivity<DatabaseHelper> {

    private ListView mLvResults;
    private Tally mTally;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        mLvResults = (ListView) findViewById(R.id.list);
        mLvResults.setEnabled(false); /* Disable selection */

        int tallyId = getIntent().getIntExtra(Tally.KEY_ID, -1);
        if (tallyId < 0) {
            throw new NullPointerException("Tally should not be not");
        }
        mTally = getHelper().getTallyDao().queryForId(tallyId);

        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.setTitle(mTally.getTitle());
        }

        Map<Person, Tally.Result> result = mTally.calculate(getHelper().getPersonDao(), getHelper().getTallyParticipantDao());
        mLvResults.setAdapter(new ResultAdapter(ResultActivity.this,
                mTally.getParticipants(getHelper().getPersonDao(), getHelper().getTallyParticipantDao()), result));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
