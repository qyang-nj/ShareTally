package me.qingy.tallyfriend;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.List;

import me.qingy.tallyfriend.model.Person;

/**
 * Created by YangQ on 9/25/2014.
 */
public class PersonDeleleteAdapter extends PersonAdapter {

    private View.OnClickListener mDeleteCb;

    public PersonDeleleteAdapter(Context context, List<Person> people) {
        super(context, people);
        setLayout(R.layout.item_text_2_del);
    }

    public void setDeleteCb(View.OnClickListener cb) {
        this.mDeleteCb = cb;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);

        ImageButton ib = (ImageButton) v.findViewById(R.id.del);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setTag(getItem(position));
                if (mDeleteCb != null) {
                    mDeleteCb.onClick(v);
                }
            }
        });
        return v;
    }
}
