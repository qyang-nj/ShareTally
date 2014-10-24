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

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import java.util.List;

import me.qingy.sharetally.data.DatabaseHelper;
import me.qingy.sharetally.data.Record;
import me.qingy.sharetally.data.Tally;


public class RecordListActivity extends OrmLiteBaseActivity<DatabaseHelper> {

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
                intent.putExtra(Tally.KEY_ID, mTally.getId());
                intent.putExtra(Record.KEY_ID, mRecords.get(position).getId());
                startActivity(intent);
            }
        });

        int tallyId = getIntent().getIntExtra(Tally.KEY_ID, -1);
        if (tallyId < 0) {
            throw new NullPointerException("Tally should not be null.");
        }

        mTally = getHelper().getTallyDao().queryForId(tallyId);
        getActionBar().setTitle(mTally.getTitle());
    }

    @Override
    protected void onResume() {
        super.onResume();

        getHelper().getTallyDao().refresh(mTally);
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
        mOptionsMenu.findItem(R.id.action_calculate).setVisible(mTally.hasRecord());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_edit:
                intent = new Intent(RecordListActivity.this, TallyEditActivity.class);
                intent.putExtra(Tally.KEY_ID, mTally.getId());
                startActivity(intent);
                break;
            case R.id.action_new:
                intent = new Intent(RecordListActivity.this, RecordEditActivity.class);
                intent.putExtra(Tally.KEY_ID, mTally.getId());
                startActivity(intent);
                break;
            case R.id.action_delete: /* Delete the tally. */
                new ConfirmationDialog().setArguments(getText(R.string.warning_delete_tally), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getHelper().getTallyDao().delete(mTally);
                        onBackPressed();
                    }
                }).show(getFragmentManager(), null);
                break;
            case R.id.action_calculate:
                intent = new Intent(RecordListActivity.this, ResultActivity.class);
                intent.putExtra(Tally.KEY_ID, mTally.getId());
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
