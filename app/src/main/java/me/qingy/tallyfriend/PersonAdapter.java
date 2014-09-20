package me.qingy.tallyfriend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import me.qingy.tallyfriend.model.Person;

/**
 * Created by YangQ on 9/19/2014.
 */
public class PersonAdapter extends BaseAdapter {
    private Context mContext;
    private List<Person> mPeople;

    public PersonAdapter(Context context, List<Person> people) {
        mContext = context;
        mPeople = people;
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
            convertView = inflater.inflate(android.R.layout.simple_list_item_2, null);

            vh = new ViewHolder();
            vh.tvName = (TextView) convertView.findViewById(android.R.id.text1);
            vh.tvEmail = (TextView) convertView.findViewById(android.R.id.text2);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.tvName.setText(mPeople.get(position).getName());
        vh.tvEmail.setText(mPeople.get(position).getEmail());

        return convertView;
    }

    public static class ViewHolder {
        public TextView tvName;
        public TextView tvEmail;
    }
}
