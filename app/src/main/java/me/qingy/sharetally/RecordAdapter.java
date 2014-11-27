package me.qingy.sharetally;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import me.qingy.sharetally.data.Person;
import me.qingy.sharetally.data.Record;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;


/**
 * Created by YangQ on 9/27/2014.
 */
public class RecordAdapter extends BaseAdapter implements StickyListHeadersAdapter {
    private Context mContext;
    private List<Record> mRecords;

    public RecordAdapter(Context context, List<Record> records) {
        mContext = context;
        mRecords = records;
    }

    public void setList(List<Record> records) {
        mRecords = records;
    }

    @Override
    public int getCount() {
        return mRecords.size();
    }

    @Override
    public Object getItem(int position) {
        return mRecords.get(position);
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
            convertView = inflater.inflate(R.layout.item_record, null);

            vh = new ViewHolder();
            vh.label = (TextView) convertView.findViewById(android.R.id.text1);
            vh.payer = (TextView) convertView.findViewById(android.R.id.text2);
            vh.amount = (TextView) convertView.findViewById(R.id.amount);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.label.setText(mRecords.get(position).getLabel());
        String name = mRecords.get(position).getPayer().getName();
        vh.payer.setText(Person.CURRENT_USERNAME.equals(name) ? mContext.getString(R.string.myself) : name);
        vh.amount.setText(((Double) mRecords.get(position).getAmount()).toString());
        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder vh;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_header, null);

            vh = new HeaderViewHolder();
            vh.text = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(vh);
        } else {
            vh = (HeaderViewHolder) convertView.getTag();
        }
        vh.text.setText(DateFormat.getDateInstance().format(mRecords.get(position).getDate()));
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        Calendar cal = Calendar.getInstance(); // locale-specific
        cal.setTime(mRecords.get(position).getDate());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime().getTime();
    }

    private static class ViewHolder {
        public TextView label;
        public TextView amount;
        public TextView payer;
    }

    private static class HeaderViewHolder {
        public TextView text;
    }

}
