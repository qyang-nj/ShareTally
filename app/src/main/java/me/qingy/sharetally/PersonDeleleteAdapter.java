package me.qingy.sharetally;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.List;

import me.qingy.sharetally.model.Person;

/**
 * Created by YangQ on 9/25/2014.
 */
public class PersonDeleleteAdapter extends PersonAdapter {

    private int mNumberOfUndeletablePeople = 0;

    public PersonDeleleteAdapter(Context context, List<Person> people) {
        super(context, people);
        setLayout(R.layout.item_text_2_del);
    }

    public void setNumberOfUndeletablePeople(int n) {
        mNumberOfUndeletablePeople = n;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);

        ImageButton ib = (ImageButton) v.findViewById(R.id.del);
        if (position < mNumberOfUndeletablePeople) {
            ib.setVisibility(View.INVISIBLE);
        } else {
            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPeople.remove(position);
                    notifyDataSetChanged();
                }
            });
        }
        return v;
    }
}
