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
import com.parse.ParseException;

import java.util.List;

import me.qingy.tallyfriend.Log.Logger;
import me.qingy.tallyfriend.model.Person;


public class FriendListActivity extends Activity {
    private PersonAdapter mAdapter;
    private ListView mListView;
    private Mode mMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        mMode = Mode.CHOICE;

        mListView = (ListView) findViewById(R.id.friend_list);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Person p = (Person) mAdapter.getItem(position);
                Intent intent = new Intent(FriendListActivity.this, FriendEditActivity.class);
                intent.putExtra("PersonID", p.getObjectId());
                startActivity(intent);
            }
        });

        if (mMode == Mode.CHOICE) {
            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Person.fetchPersonListInBackground(new FindCallback<Person>() {
            public void done(List<Person> people, ParseException e) {
                if (e != null) {
                    Logger.e(e.getMessage());
                    return;
                }

                if (mAdapter == null) {
                    mAdapter = new PersonAdapter(FriendListActivity.this, people);
                    mListView.setAdapter(mAdapter);
                } else {
                    mAdapter.setList(people);
                    mAdapter.notifyDataSetChanged();
                }
                Logger.d("Fetch data done.");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.friend_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case R.id.action_new_friend:
                intent = new Intent(this, FriendEditActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private enum Mode {
        DISPLAY, CHOICE
    }
}
