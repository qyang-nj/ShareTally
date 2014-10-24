package me.qingy.sharetally;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;

import java.text.DecimalFormat;
import java.util.List;

import me.qingy.sharetally.data.Person;


/**
 * Created by YangQ on 9/26/2014.
 */
public class PersonWeightAdapter extends PersonAdapter implements NumberPickerDialogFragment.NumberPickerDialogHandler {
    private List<Double> mWeights;
    private boolean mEnabled = true;
    private NumberPickerBuilder mNumberPickerBuilder;
    private DecimalFormat mFormatter = new DecimalFormat("0.#");

    public PersonWeightAdapter(Context context, List<Person> people, List<Double> weights, FragmentManager fm) {
        super(context, people);
        this.mWeights = weights;
        setLayout(R.layout.item_text_button);

        mNumberPickerBuilder = new NumberPickerBuilder()
                .setFragmentManager(fm)
                .setStyleResId(R.style.BetterPickersDialogFragment_Light);
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);

        /* weights */
        Button btn = (Button) v.findViewById(R.id.button);

        if (position < mWeights.size()) {
            btn.setText(mFormatter.format(mWeights.get(position)));
        } else {
            btn.setText(mFormatter.format(0.0));
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ((Person) getItem(position)).getName();
                mNumberPickerBuilder.setReference(position)
                        .setLabelText(Person.CURRENT_USERNAME.equals(name) ? mContext.getString(R.string.myself) : name)
                        .addNumberPickerDialogHandler(PersonWeightAdapter.this);
                mNumberPickerBuilder.show();
            }
        });
        btn.setEnabled(isEnabled(position));

        /* checkbox */
        final CheckBox chk = (CheckBox) v.findViewById(R.id.chk);
        chk.setChecked(mWeights.get(position) > 0.0);
        chk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWeights.set(position, chk.isChecked() ? 1.0 : 0.0);
                notifyDataSetChanged();
            }
        });

        return v;
    }

    @Override
    public boolean isEnabled(int position) {
        return mEnabled;
    }

    @Override
    public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber) {
        mWeights.set(reference, fullNumber);
        notifyDataSetChanged();
    }
}
