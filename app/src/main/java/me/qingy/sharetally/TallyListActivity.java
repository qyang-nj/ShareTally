package me.qingy.sharetally;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ui.ParseLoginBuilder;

import java.util.List;

import me.qingy.sharetally.Log.Logger;
import me.qingy.sharetally.model.Tally;


public class TallyListActivity extends Activity {
    private TallyAdapter mAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tally_list);

        mListView = (ListView) findViewById(R.id.tally_list);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tally p = (Tally) mAdapter.getItem(position);

                Intent intent = new Intent(TallyListActivity.this, RecordListActivity.class);
                ObjectHolder.setTally(p);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tally.fetchTallyListInBackground(new FindCallback<Tally>() {
            public void done(List<Tally> tallies, ParseException e) {
                if (e != null) {
                    Logger.e(e.getMessage());
                    return;
                }

                if (mAdapter == null) {
                    mAdapter = new TallyAdapter(TallyListActivity.this, tallies);
                    mListView.setAdapter(mAdapter);
                } else {
                    mAdapter.setList(tallies);
                    mAdapter.notifyDataSetChanged();
                }
                Logger.d("Fetch data done.");
            }
        });
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
                ObjectHolder.reset();
                startActivity(intent1);
                break;
            case R.id.action_friends:
                Intent intent2 = new Intent(this, FriendListActivity.class);
                ObjectHolder.reset();
                startActivity(intent2);
                break;
            case R.id.action_login:
                ParseLoginBuilder builder = new ParseLoginBuilder(this);
                startActivityForResult(builder.build(), 0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}