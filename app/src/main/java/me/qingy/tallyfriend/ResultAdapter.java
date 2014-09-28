package me.qingy.tallyfriend;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;

import java.util.List;
import java.util.Map;

import me.qingy.tallyfriend.model.Person;
import me.qingy.tallyfriend.model.Tally;

/**
 * Created by YangQ on 9/27/2014.
 */
public class ResultAdapter extends PersonAdapter {
    Map<Person, Tally.Result> mResults;

    public ResultAdapter(Context context, List<Person> people, Map<Person, Tally.Result> results) {
        super(context, people);
        this.mResults = results;
        setLayout(R.layout.item_result);
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);

        Tally.Result r = mResults.get(getItem(position));
        ((TextView) v.findViewById(R.id.final_result)).setText(((Double)(r.paid - r.toPay)).toString());
        return v;
    }
}
