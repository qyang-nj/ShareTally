package me.qingy.sharetally;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import org.apache.commons.lang3.StringUtils;

import me.qingy.sharetally.data.DatabaseHelper;
import me.qingy.sharetally.data.Person;


public class FriendEditActivity extends OrmLiteBaseActivity<DatabaseHelper> {
    private Person mPerson;
    private EditText mEtName;
    private EditText mEtEmail;
    private Mode mMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_edit);

        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(false);
            ab.setHomeButtonEnabled(true);
            ab.setIcon(R.drawable.ic_action_accept);
            ab.setTitle(getString(R.string.done));
        }

        mEtName = (EditText) findViewById(R.id.person_name);
        mEtEmail = (EditText) findViewById(R.id.person_email);

        int personId = getIntent().getIntExtra(Person.KEY_ID, -1);
        if (personId < 0) { /* Create */
            mMode = Mode.CREATE;
            mPerson = new Person();
        } else { /* Edit */
            mMode = Mode.EDIT;
            mPerson = getHelper().getPersonDao().queryForId(personId);
            mEtName.setText(mPerson.getName());
            mEtEmail.setText(mPerson.getEmail());
        }

        /* Save & New Button */
        Button btnSaveNew = (Button) findViewById(R.id.btn_save_new);
        btnSaveNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
                mPerson = new Person();
                mEtName.setText("");
                mEtEmail.setText("");
            }
        });
        btnSaveNew.setVisibility(mMode == Mode.EDIT ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.friend_edit, menu);
        if (mMode == Mode.CREATE) {
            menu.findItem(R.id.action_delete).setVisible(false);
        } else {
            menu.findItem(R.id.action_cancel).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (StringUtils.isEmpty(mEtName.getText().toString())) {
                    return true;
                }
                save();
                onBackPressed();
                break;
            case R.id.action_cancel:
                finish();
                break;
            case R.id.action_delete:
                new ConfirmationDialog().setArguments(getText(R.string.warning_delete_friend), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPerson.remove();
                        getHelper().getPersonDao().createOrUpdate(mPerson);
                        onBackPressed();
                    }
                }).show(getFragmentManager(), null);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void save() {
        if (StringUtils.isEmpty(mEtName.getText().toString())) {
            return;
        }
        mPerson.setName(mEtName.getText().toString());
        mPerson.setEmail(mEtEmail.getText().toString());
        getHelper().getPersonDao().createOrUpdate(mPerson);
    }

    private enum Mode {
        CREATE, EDIT
    }
}
