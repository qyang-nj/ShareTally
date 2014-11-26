package me.qingy.sharetally;

import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import me.qingy.sharetally.data.DatabaseHelper;
import me.qingy.sharetally.data.Person;

public class ContactSelectionActivity extends OrmLiteBaseActivity<DatabaseHelper> {
    private ListView mLvContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_selection);

        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(false);
            ab.setHomeButtonEnabled(true);
            ab.setIcon(R.drawable.ic_action_accept);
            ab.setTitle(getString(R.string.done));
        }

        mLvContacts = (ListView) findViewById(R.id.list);

        Cursor cursor = getContacts();

        ListAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_multiple_choice,
                cursor,
                new String[]{ContactsContract.Contacts.DISPLAY_NAME},
                new int[]{android.R.id.text1},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        mLvContacts.setAdapter(adapter);
        mLvContacts.setItemsCanFocus(false);
        mLvContacts.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.contact_selection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                addContactsToFriends();
                finish();
                break;
            case R.id.action_cancel:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Cursor getContacts() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
        };
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

        return getContentResolver().query(uri, projection, null, null, sortOrder);
    }

    private void addContactsToFriends() {
        long[] ids = mLvContacts.getCheckedItemIds();
        for (long id : ids) {
            Person p = createPersonByContactId(id);
            getHelper().getPersonDao().createOrUpdate(p);
        }
    }

    private Person createPersonByContactId(long contactId) {
        ContentResolver cr = getContentResolver();
        Uri baseUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri dataUri = Uri.withAppendedPath(baseUri, ContactsContract.Contacts.Data.CONTENT_DIRECTORY);
        String[] projection = new String[]{
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.Data.MIMETYPE
        };

        Cursor c = cr.query(dataUri, projection, null, null, null);
        if (c == null || c.getCount() == 0) {
            return null;
        }

        c.moveToFirst();
        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

        String email = null;
        do {
            if (ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE.equals(c.getString(c.getColumnIndex(ContactsContract.Data.MIMETYPE)))) {
                email = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                break;
            }
        } while (c.moveToNext());

        Person p = new Person();
        p.setName(name);
        p.setEmail(email);
        return p;
    }
}
