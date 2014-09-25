package me.qingy.tallyfriend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.GetCallback;
import com.parse.ParseException;

import me.qingy.tallyfriend.Log.Logger;
import me.qingy.tallyfriend.model.Tally;


public class RecordListActivity extends Activity {

    private Tally mTally;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        String tallyId = getIntent().getStringExtra("TALLY_ID");
        if (tallyId == null) {
            Logger.e("Tally ID should not be null.");
            return;
        }

        Tally.fetchTallyInBackground(tallyId, new GetCallback<Tally>() {
            @Override
            public void done(Tally tally, ParseException e) {
                if (e == null) {
                    mTally = tally;
                    getActionBar().setTitle(mTally.getTitle());
                } else {
                    Logger.e(e.getMessage());
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.record_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_edit:
                intent = new Intent(RecordListActivity.this, TallyEditActivity.class);
                intent.putExtra("TALLY_ID", mTally.getObjectId());
                startActivity(intent);
                break;
            case R.id.action_new:
                intent = new Intent(RecordListActivity.this, RecordEditActivity.class);
                startActivity(intent);
                break;
            case R.id.action_delete:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
