package me.qingy.sharetally;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import me.qingy.sharetally.data.DatabaseHelper;
import me.qingy.sharetally.data.Tally;


public class TallyListActivity extends OrmLiteBaseActivity<DatabaseHelper> {
    private TallyAdapter mAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tally_list);

        //AppEnv.init(this);

        mListView = (ListView) findViewById(R.id.tally_list);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tally p = (Tally) mAdapter.getItem(position);

                Intent intent = new Intent(TallyListActivity.this, RecordListActivity.class);
                intent.putExtra(Tally.KEY_ID, String.valueOf(p.getId()));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        List<Tally> tallies = null;

        try {
            Dao<Tally, Long> tallyDao = getHelper().getTallyDao();
            tallies = tallyDao.queryForAll();
            Log.v(this.getClass().getName(), "Fetch tallies successfully.");
        } catch (SQLException e) {
            Log.e(this.getClass().getName(), e.getMessage());
        }

        if (tallies != null) {
            if (mAdapter == null) {
                mAdapter = new TallyAdapter(TallyListActivity.this, tallies);
                mListView.setAdapter(mAdapter);
            } else {
                mAdapter.setList(tallies);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tally_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                Intent intent1 = new Intent(this, TallyEditActivity.class);
                startActivity(intent1);
                break;
            case R.id.action_friends:
                Intent intent2 = new Intent(this, FriendListActivity.class);
                startActivity(intent2);
                break;
            case R.id.action_login:
                //ParseLoginBuilder builder = new ParseLoginBuilder(this);
                //startActivityForResult(builder.build(), 0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
