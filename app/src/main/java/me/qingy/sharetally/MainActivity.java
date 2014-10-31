package me.qingy.sharetally;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.List;

import me.qingy.sharetally.data.DatabaseHelper;
import me.qingy.sharetally.data.Tally;


public class MainActivity extends OrmLiteBaseActivity<DatabaseHelper> {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private TallyAdapter mDrawerListAdapter;

    private Tally mSelectedTally = null;

    /* Click on drawer item. */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Tally tally = getHelper().getTallyDao().queryForId((int) id);

            if (mSelectedTally == null || !mSelectedTally.equals(tally)) {
                mSelectedTally = tally;

                Fragment fragment = RecordListFragment.newInstance(tally.getId());
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                mDrawerList.setItemChecked(position, true);
                setTitle(tally.getTitle());
            }
            mDrawerLayout.closeDrawers();
            invalidateOptionsMenu();
        }
    }

    /* Long press on drawer item. */
    private class DrawerItemLongClickListener implements ListView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, final long id) {
            final Tally tally = getHelper().getTallyDao().queryForId((int) id);

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(tally.getTitle());
            builder.setItems(R.array.tally_action, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0: /* Edit */
                            Intent intent = new Intent(MainActivity.this, TallyEditActivity.class);
                            intent.putExtra(Tally.KEY_ID, mSelectedTally.getId());
                            startActivity(intent);
                            break;
                        case 1: /* Deleted */
                            new ConfirmationDialog().setArguments(getText(R.string.warning_delete_tally), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getHelper().getTallyDao().delete(tally);
                                    onResume(); /* Refresh */
                                }
                            }).show(getFragmentManager(), null);
                            break;
                        default:
                    }
                }
            });

            builder.create().show();
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        /* Create Tally Button */
        findViewById(R.id.btn_create_tally).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(MainActivity.this, TallyEditActivity.class);
                startActivity(intent1);
            }
        });

        /* Manage Friends Button */
        findViewById(R.id.btn_manage_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FriendListActivity.class);
                startActivity(intent);
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerList.setOnItemLongClickListener(new DrawerItemLongClickListener());

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.action_friend, R.string.action_login) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onResume() {
        super.onResume();

        RuntimeExceptionDao<Tally, Integer> tallyDao = getHelper().getTallyDao();
        List<Tally> tallies = tallyDao.queryForAll();

        if (tallies != null) {
            if (mDrawerListAdapter == null) {
                mDrawerListAdapter = new TallyAdapter(MainActivity.this, tallies);
                mDrawerList.setAdapter(mDrawerListAdapter);
            } else {
                mDrawerListAdapter.setList(tallies);
                mDrawerListAdapter.notifyDataSetChanged();
            }

            if (mSelectedTally == null && tallies.size() > 0) {
                new DrawerItemClickListener().onItemClick(null, null, 0, tallies.get(0).getId());
            }
        }

        if (mSelectedTally != null) {
            getHelper().getTallyDao().refresh(mSelectedTally);
            invalidateOptionsMenu();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        /* Sync the toggle state after onRestoreInstanceState has occurred. */
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_list, menu);
        boolean hasTally = !(mSelectedTally == null);
        boolean hasRecord = hasTally && mSelectedTally.hasRecord();
        menu.findItem(R.id.action_new).setVisible(hasTally);
        menu.findItem(R.id.action_calculate).setVisible(hasRecord);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_new:
                intent = new Intent(this, RecordEditActivity.class);
                intent.putExtra(Tally.KEY_ID, mSelectedTally.getId());
                startActivity(intent);
                break;
            case R.id.action_calculate:
                intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra(Tally.KEY_ID, mSelectedTally.getId());
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
