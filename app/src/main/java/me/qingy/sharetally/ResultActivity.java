package me.qingy.sharetally;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Map;

import me.qingy.sharetally.data.DatabaseHelper;
import me.qingy.sharetally.data.Person;
import me.qingy.sharetally.data.Tally;

public class ResultActivity extends OrmLiteBaseActivity<DatabaseHelper> {

    private ListView mLvResults;
    private Tally mTally;
    private Map<Person, Tally.Result> mResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        mLvResults = (ListView) findViewById(R.id.list);
        mLvResults.setEnabled(false); /* Disable selection */

        int tallyId = getIntent().getIntExtra(Tally.KEY_ID, -1);
        if (tallyId < 0) {
            throw new NullPointerException("Tally should not be not");
        }
        mTally = getHelper().getTallyDao().queryForId(tallyId);

        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.setTitle(mTally.getTitle());
        }

        mResult = mTally.calculate(getHelper().getPersonDao(), getHelper().getTallyParticipantDao());
        mLvResults.setAdapter(new ResultAdapter(ResultActivity.this,
                mTally.getParticipants(getHelper().getPersonDao(), getHelper().getTallyParticipantDao()), mResult));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.help)
                        .setMessage("The first number is the amount the person have paid, while the second one is the amount the person needs to pay.\n"
                                + "The positive total number is amount needs to be received, while negative is the amount needs to give.")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
                break;
            case R.id.action_share:
                Intent intent = getShareEmailIntent(mTally, mResult);
                startActivity(Intent.createChooser(intent, getResources().getString(R.string.share)));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getSharingString(Map<Person, Tally.Result> result) {
        final String newLine = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        sb.append("Hi,");
        sb.append(newLine);
        sb.append(newLine);
        sb.append("Below is the bill. The positive total number is amount needs to be received, while negative is the amount needs to give.");
        sb.append(newLine);
        sb.append(newLine);

        for (Person p : result.keySet()) {
            Tally.Result r = result.get(p);
            String name = p.getName();
            sb.append(Person.CURRENT_USERNAME.equals(name) ? "I" : name);
            sb.append(": ");
            sb.append(r.paid - r.toPay);
            sb.append(System.getProperty("line.separator"));
        }

        return sb.toString();
    }

    private Intent getShareEmailIntent(Tally tally, final Map<Person, Tally.Result> result) {
        String[] to = new ArrayList<String>() {{
            for (Person p : result.keySet()) {
                if (!StringUtils.isEmpty(p.getEmail())) {
                    add(p.getEmail());
                }
            }
        }}.toArray(new String[0]);

        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, tally.getTitle() + " Bill (Demo)");
        emailIntent.putExtra(Intent.EXTRA_TEXT, getSharingString(mResult));
        return emailIntent;
    }
}
