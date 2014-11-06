package me.qingy.sharetally;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;
import me.qingy.sharetally.data.DatabaseHelper;
import me.qingy.sharetally.data.Person;
import me.qingy.sharetally.data.Record;
import me.qingy.sharetally.data.Tally;


public class RecordEditActivity extends FragmentActivity
        implements CalendarDatePickerDialog.OnDateSetListener {

    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker";
    /* Controls */
    private Button mBtnAmount;
    private EditText mEtLabel;
    private Button mBtnDate;
    private Button mBtnPayer;
    private ImageView mBtnReceipt;
    private ListView mLvWeights;
    /* Data */
    private double mAmount;
    private Date mDate;
    private Person mPayer;
    private List<Person> mParticipants;
    private List<Double> mWeights;
    private Tally mTally;
    private Record mRecord;
    private Bitmap mReceiptImage;
    private PersonWeightAdapter mParticipantAdapter;
    private Mode mMode;
    private DatabaseHelper databaseHelper = null;
    private ImageChooserManager receiptChooser;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_edit);

        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(false);
            ab.setHomeButtonEnabled(true);
            ab.setIcon(R.drawable.ic_action_accept);
            ab.setTitle(getString(R.string.done));
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mBtnAmount = (Button) findViewById(R.id.amount);
        mEtLabel = (EditText) findViewById(R.id.label);
        mBtnDate = (Button) findViewById(R.id.date);
        mBtnPayer = (Button) findViewById(R.id.payer);
        mBtnReceipt = (ImageView) findViewById(R.id.receipt);
        mLvWeights = (ListView) findViewById(R.id.list);

        /* Label list */
        findViewById(R.id.label_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LabelListDialogFragment().show(getSupportFragmentManager(), null);
            }
        });

        int tallyId = getIntent().getIntExtra(Tally.KEY_ID, -1);
        if (tallyId < 0) {
            throw new NullPointerException("Tally should not be null.");
        }
        mTally = getHelper().getTallyDao().queryForId(tallyId);

        int recordId = getIntent().getIntExtra(Record.KEY_ID, -1);
        if (recordId < 0) {
            mMode = Mode.CREATE;
            mRecord = new Record();
        } else {
            mMode = Mode.EDIT;
            mRecord = getHelper().getRecordDao().queryForId(recordId);
        }

        /* Save & New Button */
        Button btnSaveNew = (Button) findViewById(R.id.btn_save_new);
        btnSaveNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAmount == 0) {
                    return;
                }
                save();
                mRecord = new Record();
                fillData(mRecord);
            }
        });
        btnSaveNew.setVisibility(mMode == Mode.EDIT ? View.GONE : View.VISIBLE);

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
            menu.findItem(R.id.action_cancel).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                if (mAmount == 0) {
                    return true;
                }
                save();
                finish();
                break;
            case R.id.action_cancel:
                finish();
                break;
            case R.id.action_delete:
                mTally.delRecord(mRecord);
                getHelper().getTallyDao().update(mTally);
                getHelper().getRecordDao().delete(mRecord);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE) {
                receiptChooser.submit(requestCode, data);
            }
        }
    }

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
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
        mParticipants = mTally.getParticipants(getHelper().getPersonDao(), getHelper().getTallyParticipantDao());
        if (mParticipants == null) {
            throw new NullPointerException("Participants should not be null.");
        }

        /* Set payer */
        mPayer = r.getPayer();
        if (mPayer == null && mParticipants.size() > 0) {
            mPayer = mParticipants.get(0);
        }
        String name = mPayer.getName();
        mBtnPayer.setText(Person.CURRENT_USERNAME.equals(name) ? getString(R.string.myself) : name);
        mBtnPayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PayerSelectionDialogFragment().show(getSupportFragmentManager(), null);
            }
        });

        /* Set receipt */
        mReceiptImage = r.getReceiptImage();
        if (mReceiptImage != null) {
            mBtnReceipt.setImageBitmap(mReceiptImage);
        }
        mBtnReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mReceiptImage == null) {
                    getImageChooseDialog().show();
                } else {
                    AlertDialog ad = getImageShowingDialog();
                    ad.show();
                }
            }
        });

        /* Set weights */
        Map<Person, Double> weights = mRecord.getBeneficiaryWeights();
        if (weights == null) {
            mWeights = new ArrayList<Double>() {{
                for (int i = 0; i < mParticipants.size(); ++i) {
                    add(1.0);
                }
            }};
        } else {
            mWeights = new ArrayList<Double>() {{
                Map<Person, Double> weights = mRecord.getBeneficiaryWeights();
                for (Person p : mParticipants) {
                    add(weights.containsKey(p) ? weights.get(p) : 0.0);
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
        mRecord.setReceiptImage(mReceiptImage);

        if (mRecord.getId() == Record.ID_NEW) {
            mTally.addRecord(mRecord);
            getHelper().getTallyDao().update(mTally);
        } else {
            getHelper().getRecordDao().update(mRecord);
        }

        getHelper().getRecordDao().refresh(mRecord);
        mRecord.setBeneficiaryWeights(mParticipants, mWeights);
        getHelper().getRecordDao().update(mRecord);
    }

    private AlertDialog getImageChooseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.add_receipt));
        builder.setItems(R.array.image_choose_action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) { /* Take Photo */
                    receiptChooser = new ImageChooserManager(RecordEditActivity.this, ChooserType.REQUEST_CAPTURE_PICTURE);
                    receiptChooser.setImageChooserListener(new ReceiptChooserListener());
                    try {
                        receiptChooser.choose();
                    } catch (Exception e) {
                        Log.e(this.getClass().getName(), e.getMessage());
                    }
                } else if (item == 1) { /* Choose from Gallery */
                    receiptChooser = new ImageChooserManager(RecordEditActivity.this, ChooserType.REQUEST_PICK_PICTURE);
                    receiptChooser.setImageChooserListener(new ReceiptChooserListener());
                    try {
                        receiptChooser.choose();
                    } catch (Exception e) {
                        Log.e(this.getClass().getName(), e.getMessage());
                    }
                } else {
                    dialog.dismiss();
                }
            }
        });
        return builder.create();
    }

    private AlertDialog getImageShowingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        ImageViewTouch image = new ImageViewTouch(this, null);
        image.setImageBitmap(mReceiptImage);
        image.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);

        builder.setView(image)
                .setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        getImageChooseDialog().show();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
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
            String name = mPayer.getName();
            mBtnPayer.setText(Person.CURRENT_USERNAME.equals(name) ? getString(R.string.myself) : name);
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

    private class ReceiptChooserListener implements ImageChooserListener {

        @Override
        public void onImageChosen(ChosenImage image) {
            if (image != null) {
                Bitmap bmp = BitmapFactory.decodeFile(image.getFilePathOriginal());
                float bitmapRatio = (float) bmp.getWidth() / (float) bmp.getHeight();
                mReceiptImage = Bitmap.createScaledBitmap(bmp, (int) (960 * bitmapRatio), 960, true);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mBtnReceipt.setImageBitmap(mReceiptImage);
                    }
                });
            }
        }

        @Override
        public void onError(String s) {
            Log.e(this.getClass().getName(), s);
        }
    }

    private enum Mode {
        CREATE, EDIT
    }
}
