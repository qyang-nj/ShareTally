package me.qingy.tallyfriend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

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
            convertView = inflater.inflate(R.layout.person_list_item, null);

            vh = new ViewHolder();
            vh.text1 = (TextView) convertView.findViewById(R.id.text1);
            vh.text2 = (TextView) convertView.findViewById(R.id.text2);
            vh.checkBox = (CheckBox) convertView.findViewById(R.id.chk);
            vh.del = (ImageView) convertView.findViewById(R.id.del);

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.text1.setText(mPeople.get(position).getName());

        String email = mPeople.get(position).getEmail();
        if (StringUtils.isEmpty(email)) {
            hideText2(vh);
        } else {
            vh.text2.setText(mPeople.get(position).getEmail());
        }
        //hideText2(vh);

        return convertView;
    }

    private void hideText2(ViewHolder vh) {
        if (vh.text2 != null) {
            vh.text2.setHeight(0);
        }
    }

    private void hideCheckBox(ViewHolder vh) {
        if (vh.checkBox != null) {
            vh.checkBox.setVisibility(View.INVISIBLE);
        }
    }

    private void hideDelete(ViewHolder vh) {
        if (vh.del != null) {
            vh.del.setVisibility(View.INVISIBLE);
        }
    }

    private static class ViewHolder {
        public TextView text1;
        public TextView text2;
        public CheckBox checkBox;
        public ImageView del;
    }
}
