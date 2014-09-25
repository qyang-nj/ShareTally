package me.qingy.tallyfriend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import me.qingy.tallyfriend.model.Tally;

/**
 * Created by YangQ on 9/25/2014.
 */
public class TallyAdapter extends BaseAdapter {

    private Context mContext;
    private List<Tally> mTallies;

    public TallyAdapter(Context context, List<Tally> tallies) {
        mContext = context;
        mTallies = tallies;
    }

    public void setList(List<Tally> tallies) {
        mTallies = tallies;
    }

    @Override
    public int getCount() {
        return mTallies.size();
    }

    @Override
    public Object getItem(int position) {
        return mTallies.get(position);
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
            vh.text1 = (TextView) convertView.findViewById(android.R.id.text1);
            vh.text2 = (TextView) convertView.findViewById(android.R.id.text2);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.text1.setText(mTallies.get(position).getTitle());
        vh.text2.setText(mTallies.get(position).getDescription());

        return convertView;
    }

    private static class ViewHolder {
        public TextView text1;
        public TextView text2;
    }
}
