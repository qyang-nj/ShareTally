package me.qingy.sharetally;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import me.qingy.sharetally.model.Person;

/**
 * Created by YangQ on 9/19/2014.
 */
public class PersonAdapter extends BaseAdapter {
    protected Context mContext;
    protected List<Person> mPeople;
    private int mLayoutId = android.R.layout.simple_list_item_2;

    public PersonAdapter(Context context, List<Person> people) {
        mContext = context;
        mPeople = people;
    }

    public void setLayout(int layoutId) {
        mLayoutId = layoutId;
    }

    public void setList(List<Person> people) {
        mPeople = people;
    }

    @Override
    public int getCount() {
        return mPeople.size();
    }

    @Override
    public Object getItem(int position) {
        return mPeople.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mLayoutId, null);

            vh = new ViewHolder();
            vh.text1 = (TextView) convertView.findViewById(android.R.id.text1);
            vh.text2 = (TextView) convertView.findViewById(android.R.id.text2);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.text1.setText(mPeople.get(position).getName());
        if (vh.text2 != null) {
            vh.text2.setText(mPeople.get(position).getEmail());
        }
        return convertView;
    }

    private static class ViewHolder {
        public TextView text1;
        public TextView text2;
    }
}
