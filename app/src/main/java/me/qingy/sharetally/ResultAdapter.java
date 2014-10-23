package me.qingy.sharetally;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import me.qingy.sharetally.data.Person;
import me.qingy.sharetally.data.Tally;


/**
 * Created by YangQ on 9/27/2014.
 */
public class ResultAdapter extends PersonAdapter {
    Map<Person, Tally.Result> mResults;
    DecimalFormat mFormatter = new DecimalFormat("0.00");

    public ResultAdapter(Context context, List<Person> people, Map<Person, Tally.Result> results) {
        super(context, people);
        this.mResults = results;
        setLayout(R.layout.item_result);
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);

        Tally.Result r = mResults.get(getItem(position));
        double delta = r.paid - r.toPay;

        TextView tvPaid = (TextView) v.findViewById(R.id.paid);
        TextView tvToPay = (TextView) v.findViewById(R.id.to_pay);
        TextView tvDelta = (TextView) v.findViewById(R.id.final_result);

        tvPaid.setText(mFormatter.format(r.paid));
        tvToPay.setText(mFormatter.format(r.toPay));

        tvDelta.setText(mFormatter.format(delta));
        if (delta > 0) {
            tvDelta.setTextColor(v.getResources().getColor(R.color.color_green));
        } else if (delta < 0) {
            tvDelta.setTextColor(v.getResources().getColor(R.color.color_red));
        }
        return v;
    }
}
