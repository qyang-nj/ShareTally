package me.qingy.sharetally;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import me.qingy.sharetally.Log.Logger;
import me.qingy.sharetally.data.DatabaseHelper;
import me.qingy.sharetally.data.Person;


public class FriendListActivity extends OrmLiteBaseActivity<DatabaseHelper> {

    private PersonAdapter mAdapter;
    private ListView mListView;
    private Mode mMode = Mode.DISPLAY;
    private ArrayList<Integer> mSelectedItem = new ArrayList<Integer>();
    private ArrayList<Integer> mExcludedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        String mode = getIntent().getStringExtra("MODE");
        if (mode != null) {
            mMode = Enum.valueOf(Mode.class, mode);
        }

        mExcludedItem = getIntent().getIntegerArrayListExtra("EXCLUDED_IDS");

        mListView = (ListView) findViewById(R.id.friend_list);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Person p = (Person) mAdapter.getItem(position);

                if (Mode.DISPLAY == mMode) {
                    Intent intent = new Intent(FriendListActivity.this, FriendEditActivity.class);
                    //ObjectHolder.setPerson(p);
                    startActivity(intent);
                } else if (Mode.SELECTION == mMode) {
                    CheckBox cb = (CheckBox) view.findViewById(R.id.chk);
                    boolean checked = !cb.isChecked();
                    cb.setChecked(checked);
                    if (checked) {
                        Logger.d("Add " + p.getName());
                        mSelectedItem.add(p.getId());
                    } else {
                        Logger.d("Remove " + p.getName());
                        mSelectedItem.remove(p.getId());
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Person> people = getHelper().getPersonDao().queryForAll();

        if (people != null) {
            if (mAdapter == null) {
                mAdapter = new PersonAdapter(FriendListActivity.this, people);
                if (mMode == Mode.SELECTION) {
                    mAdapter.setLayout(R.layout.item_text_2_check);
                }
                mListView.setAdapter(mAdapter);
            } else {
                mAdapter.setList(people);
                mAdapter.notifyDataSetChanged();
            }
            Logger.d("Fetch data done.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.friend_list, menu);

        MenuItem mi = menu.findItem(R.id.action_done);
        if (mi != null) {
            mi.setVisible(mMode == Mode.SELECTION);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case R.id.action_new:
                intent = new Intent(this, FriendEditActivity.class);
                ObjectHolder.reset();
                startActivity(intent);
                break;
            case R.id.action_done:
                Intent resultData = new Intent();
                resultData.putIntegerArrayListExtra("SELECTED_ITEMS", mSelectedItem);
                setResult(Activity.RESULT_OK, resultData);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    static public enum Mode {
        DISPLAY, SELECTION
    }
}
