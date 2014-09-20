package me.qingy.tallyfriend;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.parse.GetCallback;
import com.parse.ParseException;

import org.apache.commons.lang3.StringUtils;

import me.qingy.tallyfriend.Log.Logger;
import me.qingy.tallyfriend.model.Person;


public class FriendEditActivity extends Activity {
    private Person mPerson;
    private EditText mEtName;
    private EditText mEtEmail;
    private Mode mMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_edit);

        mEtName = (EditText) findViewById(R.id.person_name);
        mEtEmail = (EditText) findViewById(R.id.person_email);

        String personId = getIntent().getStringExtra("PersonID");
        if (personId == null) { /* Create */
            mMode = Mode.CREATE;
            getActionBar().setTitle(getResources().getString(R.string.create).toUpperCase());
            mPerson = new Person();
        } else { /* Edit */
            mMode = Mode.EDIT;
            getActionBar().setTitle(getResources().getString(R.string.edit).toUpperCase());
            Person.fetchPersonInBackground(personId, new GetCallback<Person>() {
                @Override
                public void done(Person person, ParseException e) {
                    if (e == null) {
                        mPerson = person;
                        mEtName.setText(mPerson.getName());
                        mEtEmail.setText(mPerson.getEmail());
                    } else {
                        Logger.e(e.getMessage());
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.friend_edit, menu);
        if (mMode == Mode.CREATE) {
            menu.findItem(R.id.action_delete).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (StringUtils.isEmpty(mEtName.getText().toString())) {
                    return true;
                }
                mPerson.setName(mEtName.getText().toString());
                mPerson.setEmail(mEtEmail.getText().toString());
                mPerson.pin();
                onBackPressed();
                break;
            case R.id.action_delete:
                mPerson.remove();
                mPerson.pin();
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private enum Mode {
        CREATE, EDIT
    }
}
