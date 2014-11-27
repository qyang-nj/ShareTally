package me.qingy.sharetally;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import me.qingy.sharetally.data.Tally;


/**
 * Created by YangQ on 9/25/2014.
 */
public class TallyAdapter extends BaseAdapter {

    private Context mContext;
    private List<Tally> mTallies;
    private View.OnClickListener mMoreBtnCb;

    public TallyAdapter(Context context, List<Tally> tallies, View.OnClickListener moreBtnCb) {
        mContext = context;
        mTallies = tallies;
        mMoreBtnCb = moreBtnCb;
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
        return mTallies.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_tally, null);

            vh = new ViewHolder();
            vh.text = (TextView) convertView.findViewById(R.id.text);
            vh.btn = (Button) convertView.findViewById(R.id.more);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.text.setText(mTallies.get(position).getTitle());

        vh.btn.setTag(mTallies.get(position));
        vh.btn.setOnClickListener(mMoreBtnCb);

        return convertView;
    }

    private static class ViewHolder {
        public TextView text;
        public Button btn;
    }
}
