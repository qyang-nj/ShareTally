package me.qingy.tallyfriend;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.qingy.tallyfriend.model.Person;
import me.qingy.tallyfriend.model.Record;
import me.qingy.tallyfriend.model.Tally;


public class RecordEditActivity extends FragmentActivity
        implements CalendarDatePickerDialog.OnDateSetListener {

    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker";
    /* Controls */
    private Button mBtnAmount;
    private EditText mEtLabel;
    private Button mBtnDate;
    private Button mBtnPayer;
    private ListView mLvWeights;
    /* Data */
    private double mAmount;
    private Date mDate;
    private Person mPayer;
    private List<Person> mParticipants;
    private List<Double> mWeights;
    private Tally mTally;
    private Record mRecord;
    private PersonWeightAdapter mParticipantAdapter;
    private Mode mMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_edit);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mBtnAmount = (Button) findViewById(R.id.amount);
        mEtLabel = (EditText) findViewById(R.id.label);
        mBtnDate = (Button) findViewById(R.id.date);
        mBtnPayer = (Button) findViewById(R.id.payer);
        mLvWeights = (ListView) findViewById(R.id.list);

        /* Label list */
        findViewById(R.id.label_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LabelListDialogFragment().show(getSupportFragmentManager(), "123");
            }
        });

        mTally = ObjectHolder.getTally();
        if (mTally == null) {
            throw new NullPointerException("Tally should not be null.");
        }

        mRecord = ObjectHolder.getRecord();
        if (mRecord == null) {
            mMode = Mode.CREATE;
            getActionBar().setTitle(getResources().getString(R.string.title_add_record).toUpperCase());
            mRecord = new Record();
        } else {
            mMode = Mode.EDIT;
            getActionBar().setTitle(getResources().getString(R.string.title_edit_record).toUpperCase());
        }

        fillData(mRecord);
    }

    @Override
    public void onResume() {
        super.onResume();

        CalendarDatePickerDialog calendarDatePickerDialog = (CalendarDatePickerDialog) getSupportFragmentManager()
                .findFragmentByTag(FRAG_TAG_DATE_PICKER);
        if (calendarDatePickerDialog != null) {
            calendarDatePickerDialog.setOnDateSetListener(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.record_edit, menu);

        if (mMode == Mode.CREATE) {
            menu.findItem(R.id.action_delete).setVisible(false);
        } else if (mMode == Mode.EDIT) {
            menu.findItem(R.id.action_save_new).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                save();
                finish();
                break;
            case R.id.action_save_new:
                save();
                mRecord = new Record();
                fillData(mRecord);
            case R.id.action_delete:
                mTally.delRecord(mRecord);
                mTally.pin();
                mRecord.deleteEventually();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSet(CalendarDatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        mDate = cal.getTime();
        mBtnDate.setText(DateFormat.getDateInstance().format(mDate));
    }

    /* Before this function is invoked, tally and record should be ready. */
    private void fillData(Record r) {
        /* Set amount */
        mAmount = r.getAmount();
        mBtnAmount.setText(((Double) mAmount).toString());
        mBtnAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberPickerBuilder npb = new NumberPickerBuilder()
                        .setFragmentManager(getSupportFragmentManager())
                        .setPlusMinusVisibility(View.INVISIBLE)
                        .setStyleResId(R.style.BetterPickersDialogFragment_Light)
                        .addNumberPickerDialogHandler(new AmountSetCallback());
                npb.show();
            }
        });

        /* Set label */
        mEtLabel.setText(r.getLabel());

        /* Set date */
        mDate = r.getDate() == null ? new Date() : r.getDate();
        mBtnDate.setText(DateFormat.getDateInstance().format(mDate));
        mBtnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(mDate);
                CalendarDatePickerDialog pickerDialog = CalendarDatePickerDialog.newInstance(RecordEditActivity.this,
                        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                pickerDialog.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
            }
        });

        /* Set participants */
        mParticipants = mTally.getParticipants();
        if (mParticipants == null) {
            throw new NullPointerException("Participants should not be null.");
        }

        /* Set payer */
        mPayer = r.getPayer();
        if (mPayer == null) {
            mPayer = mParticipants.get(0);
        }
        mBtnPayer.setText(mPayer.getName());
        mBtnPayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PayerSelectionDialogFragment().show(getSupportFragmentManager(), "123");
            }
        });

        /* Set weights */
        mWeights = r.getBeneficiaryWeights();
        if (mWeights == null) {
            mWeights = new ArrayList<Double>() {{
                for (int i = 0; i < mParticipants.size(); ++i) {
                    add(1.0);
                }
            }};
        }

        /* If the number of participants is larger than the size of weights, then fill up. */
        int s = mWeights.size();
        for (int i = 0; i < mParticipants.size() - s; ++i) {
            mWeights.add(0.0);
        }

        mParticipantAdapter = new PersonWeightAdapter(RecordEditActivity.this, mParticipants, mWeights, getSupportFragmentManager());
        mLvWeights.setAdapter(mParticipantAdapter);
    }

    private void save() {
        mRecord.setAmount(mAmount);
        mRecord.setCaption(mEtLabel.getText().toString());
        mRecord.setDate(mDate);
        mRecord.setPayer(mPayer);
        mRecord.setBeneficiaryWeights(mWeights);
        mTally.addRecord(mRecord);
        mTally.pin();
    }

    /* Payer selection */
    public class PayerSelectionDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getResources().getString(R.string.payer))
                    .setAdapter(new PersonNameAdapter(RecordEditActivity.this, mParticipants), new PayerSelectedCallback());
            return builder.create();
        }
    }

    public class PayerSelectedCallback implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            mPayer = mParticipants.get(which);
            mBtnPayer.setText(mPayer.getName());
        }
    }

    /* Label list */
    public class LabelListDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getResources().getString(R.string.labels))
                    .setItems(R.array.label_list, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    mEtLabel.setText(getResources().getStringArray(R.array.label_list)[which]);
                                }
                            }
                    )
                    .setNegativeButton(android.R.string.cancel, null);
            return builder.create();
        }
    }

    public class AmountSetCallback implements NumberPickerDialogFragment.NumberPickerDialogHandler {
        @Override
        public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber) {
            mAmount = fullNumber;
            mBtnAmount.setText(((Double) mAmount).toString());
        }
    }

    private enum Mode {
        CREATE, EDIT
    }
}
