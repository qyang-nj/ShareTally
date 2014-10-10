package me.qingy.sharetally;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.GetCallback;
import com.parse.ParseException;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import me.qingy.sharetally.model.Person;
import me.qingy.sharetally.model.Tally;


public class TallyEditActivity extends Activity {
    static private final int ADD_PARTICIPANT_REQ_ID = 1;

    private EditText mEtTitle;
    private EditText mEtDescription;
    private ListView mLvParticipants;

    private Mode mMode = Mode.CREATE;
    private Tally mTally;
    private PersonDeleleteAdapter mParticipantAdapter;
    private List<Person> mParticipants;
    private int mSavedNumberOfPeople = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tally_edit);

        getActionBar().setDisplayHomeAsUpEnabled(true);
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
                    intent.putStringArrayListExtra("EXCLUDED_IDS", new ArrayList<String>() {
                        {
                            for (Person p : mParticipants) {
                                add(p.getObjectId());
                            }
                        }
                    });
                }

                startActivityForResult(intent, ADD_PARTICIPANT_REQ_ID);
            }
        });

        mTally = ObjectHolder.getTally();
        if (mTally == null) { /* Create */
            mMode = Mode.CREATE;
            getActionBar().setTitle(getResources().getString(R.string.title_create_tally).toUpperCase());
            mTally = new Tally();
            setAdapter();
        } else { /* Edit */
            mMode = Mode.EDIT;
            getActionBar().setTitle(getResources().getString(R.string.title_edit_tally).toUpperCase());
            mEtTitle.setText(mTally.getTitle());
            mEtDescription.setText(mTally.getDescription());
            /* Make a copy of original list. */
            mParticipants = new ArrayList<Person>() {{
                addAll(mTally.getParticipants());
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
                mTally.setParticipants(mParticipants);
                mTally.pin();
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_PARTICIPANT_REQ_ID) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> selectedItems = data.getStringArrayListExtra("SELECTED_ITEMS");

                for (String objId : selectedItems) {
                    Person.fetchPersonInBackground(objId, new GetCallback<Person>() {
                        @Override
                        public void done(Person person, ParseException e) {
                            mParticipants.add(person);
                            mParticipantAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }
    }

    private void setAdapter() {
        if (mParticipants == null) {
            mParticipants = new ArrayList<Person>();
            mParticipants.add(Person.getMe());
        }

        if (mParticipantAdapter == null) {
            mParticipantAdapter = new PersonDeleleteAdapter(this, mParticipants);
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
