package me.qingy.sharetally;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import me.qingy.sharetally.data.Record;
import me.qingy.sharetally.data.Tally;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


public class RecordListFragment extends OrmLiteFragment {

    private StickyListHeadersListView mLvRecords;

    private Tally mTally;
    private List<Record> mRecords;
    private RecordAdapter mAdapter;

    public static RecordListFragment newInstance(int tallyId) {
        RecordListFragment fragment = new RecordListFragment();
        Bundle args = new Bundle();
        args.putInt(Tally.KEY_ID, tallyId);
        fragment.setArguments(args);
        return fragment;
    }

    public RecordListFragment() {
        /* Required empty public constructor */
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int tallyId = getArguments().getInt(Tally.KEY_ID);
            if (tallyId < 0) {
                throw new IllegalArgumentException("Tally ID is invalid.");
            }

            mTally = getHelper().getTallyDao().queryForId(tallyId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_list, container, false);
        mLvRecords = (StickyListHeadersListView) view.findViewById(R.id.list);
        mLvRecords.setEmptyView(view.findViewById(android.R.id.empty));
        mLvRecords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), RecordEditActivity.class);
                intent.putExtra(Tally.KEY_ID, mTally.getId());
                intent.putExtra(Record.KEY_ID, mRecords.get(position).getId());
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getHelper().getTallyDao().refresh(mTally);
        mRecords = mTally.getRecords();
        if (mRecords != null) {
            if (mAdapter == null) {
                mAdapter = new RecordAdapter(getActivity(), mRecords);
                mLvRecords.setAdapter(mAdapter);
            } else {
                mAdapter.setList(mRecords);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}
