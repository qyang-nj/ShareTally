package me.qingy.tallyfriend;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import me.qingy.tallyfriend.model.Person;

/**
 * Created by YangQ on 9/26/2014.
 */
public class PersonWeightAdapter extends PersonAdapter {
    private View.OnClickListener mButtonClickCb;
    private List<Double> mWeights;

    public PersonWeightAdapter(Context context, List<Person> people, List<Double> weights) {
        super(context, people);
        this.mWeights = weights;
        setLayout(R.layout.item_text_button);
    }

    public void setButtonClickCb(View.OnClickListener cb) {
        this.mButtonClickCb = cb;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);

        Button btn = (Button) v.findViewById(R.id.button);

        if (position < mWeights.size()) {
            btn.setText(mWeights.get(position).toString());
        } else {
            btn.setText("0");
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setTag(getItem(position));
                if (mButtonClickCb != null) {
                    mButtonClickCb.onClick(v);
                }
            }
        });
        return v;
    }
}
