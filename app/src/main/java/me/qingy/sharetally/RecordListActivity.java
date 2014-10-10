package me.qingy.sharetally;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import me.qingy.sharetally.model.Record;
import me.qingy.sharetally.model.Tally;


public class RecordListActivity extends Activity {

    private ListView mLvRecords;
    private Menu mOptionsMenu;

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
                ObjectHolder.setTally(mTally);
                ObjectHolder.setRecord(mRecords.get(position));
                startActivity(intent);
            }
        });

        mTally = ObjectHolder.getTally();
        if (mTally == null) {
            throw new NullPointerException("Tally should not be null.");
        }

        getActionBar().setTitle(mTally.getTitle());
    }

    @Override
    protected void onResume() {
        super.onResume();

        mRecords = mTally.getRecords();
        if (mRecords != null) {
            if (mAdapter == null) {
                mAdapter = new RecordAdapter(RecordListActivity.this, mRecords);
                mLvRecords.setAdapter(mAdapter);
            } else {
                mAdapter.setList(mRecords);
                mAdapter.notifyDataSetChanged();
            }
        }

        if (mOptionsMenu != null) {
            mOptionsMenu.findItem(R.id.action_calculate).setVisible(mTally.hasRecord());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.record_list, menu);
        mOptionsMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_edit:
                intent = new Intent(RecordListActivity.this, TallyEditActivity.class);
                ObjectHolder.setTally(mTally);
                startActivity(intent);
                break;
            case R.id.action_new:
                intent = new Intent(RecordListActivity.this, RecordEditActivity.class);
                ObjectHolder.setTally(mTally);
                ObjectHolder.resetRecord();
                startActivity(intent);
                break;
            case R.id.action_delete: /* Delete the tally. */
                new ConfirmationDialog().setArguments(getText(R.string.warning_delete_tally), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mTally.deleteEventually();
                        onBackPressed();
                    }
                }).show(getFragmentManager(), null);
                break;
            case R.id.action_calculate:
                intent = new Intent(RecordListActivity.this, ResultActivity.class);
                ObjectHolder.setTally(mTally);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
