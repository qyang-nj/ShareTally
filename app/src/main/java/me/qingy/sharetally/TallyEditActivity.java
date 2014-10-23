package me.qingy.sharetally;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.parse.GetCallback;
import com.parse.ParseException;

import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import me.qingy.sharetally.data.DatabaseHelper;
import me.qingy.sharetally.data.Person;
import me.qingy.sharetally.data.Tally;

public class TallyEditActivity extends OrmLiteBaseActivity<DatabaseHelper> {
    static private final int ADD_PARTICIPANT_REQ_ID = 1;

    private EditText mEtTitle;
    private EditText mEtDescription;
    private ListView mLvParticipants;

    private Mode mMode = Mode.CREATE;
    private Tally mTally;
    private PersonDeleteAdapter mParticipantAdapter;
    private List<Person> mParticipants;
    private int mSavedNumberOfPeople = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tally_edit);

        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        /* wire up */
        mEtTitle = (EditText) findViewById(R.id.tally_title);
        mEtDescription = (EditText) findViewById(R.id.tally_description);
        mLvParticipants = (ListView) findViewById(R.id.list);

        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TallyEditActivity.this, FriendListActivity.class);
                intent.putExtra("MODE", FriendListActivity.Mode.SELECTION.toString());
                if (mParticipants != null) {
                    intent.putIntegerArrayListExtra("EXCLUDED_IDS", new ArrayList<Integer>() {
                        {
                            for (Person p : mParticipants) {
                                add(p.getId());
                            }
                        }
                    });
                }

                startActivityForResult(intent, ADD_PARTICIPANT_REQ_ID);
            }
        });

        String tallyId = getIntent().getStringExtra(Tally.KEY_ID);
        //mTally = ObjectHolder.getTally();
        if (tallyId == null) { /* Create */
            mMode = Mode.CREATE;
            getActionBar().setTitle(getResources().getString(R.string.title_create_tally).toUpperCase());
            mTally = new Tally();
            setAdapter();
        } else { /* Edit */
            try {
                mTally = getHelper().getTallyDao().queryForId(Long.parseLong(tallyId));
            } catch (SQLException e) {
                Log.e(this.getClass().getName(), e.getMessage());
                return;
            }

            mMode = Mode.EDIT;
            getActionBar().setTitle(getResources().getString(R.string.title_edit_tally).toUpperCase());
            mEtTitle.setText(mTally.getTitle());
            mEtDescription.setText(mTally.getDescription());
            /* Make a copy of original list. */
            mParticipants = new ArrayList<Person>() {{
                addAll(mTally.getParticipants(getHelper().getPersonDao(), getHelper().getTallyParticipantDao()));
            }};

            mSavedNumberOfPeople = mParticipants.size();
            setAdapter();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tally_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //this.finish();
                onBackPressed();
                return true;

            case R.id.action_save:
                if (StringUtils.isEmpty(mEtTitle.getText().toString())) {
                    return true;
                }
                mTally.setTitle(mEtTitle.getText().toString());
                mTally.setDescription(mEtDescription.getText().toString());
                mTally.setParticipants(mParticipants, getHelper().getTallyParticipantDao());
                try {
                    getHelper().getTallyDao().createOrUpdate(mTally);
                } catch (SQLException e) {
                    Log.e(this.getClass().getName(), e.getMessage());
                }
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_PARTICIPANT_REQ_ID) {
            if (resultCode == RESULT_OK) {
                ArrayList<Integer> selectedItems = data.getIntegerArrayListExtra("SELECTED_ITEMS");

                for (int id : selectedItems) {
                    Person person = getHelper().getPersonDao().queryForId(id);
                    mParticipants.add(person);
                }
                mParticipantAdapter.notifyDataSetChanged();
            }
        }
    }

    private void setAdapter() {
        if (mParticipants == null) {
            mParticipants = new ArrayList<Person>();
            mParticipants.add(getHelper().getCurrentPerson());
        }

        if (mParticipantAdapter == null) {
            mParticipantAdapter = new PersonDeleteAdapter(this, mParticipants);
            mLvParticipants.setAdapter(mParticipantAdapter);

            /* If a tally already has one or more records, current participants are not allowed to be deleted. */
            if (mTally.hasRecord()) {
                mParticipantAdapter.setNumberOfUndeletablePeople(mSavedNumberOfPeople);
            }
        }
    }

    private enum Mode {
        CREATE, EDIT
    }
}
