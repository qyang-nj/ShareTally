package me.qingy.tallyfriend;

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

import me.qingy.tallyfriend.Log.Logger;
import me.qingy.tallyfriend.model.Person;
import me.qingy.tallyfriend.model.Tally;


public class TallyEditActivity extends Activity {
    static private final int ADD_PARTICIPANT_REQ_ID = 1;

    private EditText mEtTitle;
    private EditText mEtDescription;
    private ListView mLvParticipants;

    private Mode mMode = Mode.CREATE;
    private Tally mTally;
    private PersonDelAdapter mParticipantAdapter;
    private List<Person> mParticipants;

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

        String tallyId = getIntent().getStringExtra("TALLY_ID");
        if (tallyId == null) { /* Create */
            mMode = Mode.CREATE;
            getActionBar().setTitle(getResources().getString(R.string.create).toUpperCase());
            mTally = new Tally();
            setAdapter();
        } else { /* Edit */
            mMode = Mode.EDIT;
            getActionBar().setTitle(getResources().getString(R.string.edit).toUpperCase());
            Tally.fetchTallyInBackground(tallyId, new GetCallback<Tally>() {
                @Override
                public void done(Tally tally, ParseException e) {
                    if (e == null) {
                        mTally = tally;
                        mEtTitle.setText(mTally.getTitle());
                        mEtDescription.setText(mTally.getDescription());
                        mParticipants = mTally.getParticipants();
                        setAdapter();
                    } else {
                        Logger.e(e.getMessage());
                    }
                }
            });
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
            mParticipantAdapter = new PersonDelAdapter(this, mParticipants);
            mParticipantAdapter.setDeleteCb(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mParticipants.remove(v.getTag());
                    mParticipantAdapter.notifyDataSetChanged();
                }
            });
            mLvParticipants.setAdapter(mParticipantAdapter);
        }
    }

    private enum Mode {
        CREATE, EDIT
    }
}
