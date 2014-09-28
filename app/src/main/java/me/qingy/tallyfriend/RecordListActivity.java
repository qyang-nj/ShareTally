package me.qingy.tallyfriend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;

import java.util.List;

import me.qingy.tallyfriend.Log.Logger;
import me.qingy.tallyfriend.model.Record;
import me.qingy.tallyfriend.model.Tally;


public class RecordListActivity extends Activity {

    private ListView mLvRecords;

    private Tally mTally;
    private RecordAdapter mAdapter;
    private List<Record> mRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        mLvRecords = (ListView) findViewById(R.id.list);
        mLvRecords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(RecordListActivity.this, RecordEditActivity.class);
                intent.putExtra("TALLY_ID", mTally.getObjectId());
                intent.putExtra("RECORD_ID", mRecords.get(position).getObjectId());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        String tallyId = getIntent().getStringExtra("TALLY_ID");
        if (tallyId == null) {
            Logger.e("Tally ID should not be null.");
            return;
        }

        Tally.fetchTallyInBackground(tallyId, new GetCallback<Tally>() {
            @Override
            public void done(Tally tally, ParseException e) {
                if (e != null) {
                    Logger.e(e.getMessage());
                    return;
                }

                mTally = tally;
                mRecords = tally.getRecords();
                getActionBar().setTitle(mTally.getTitle());

                if (mRecords != null) {
                    if (mAdapter == null) {
                        mAdapter = new RecordAdapter(RecordListActivity.this, mRecords);
                        mLvRecords.setAdapter(mAdapter);
                    } else {
                        mAdapter.setList(mRecords);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
                intent.putExtra("TALLY_ID", mTally.getObjectId());
                startActivity(intent);
                break;
            case R.id.action_delete:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
