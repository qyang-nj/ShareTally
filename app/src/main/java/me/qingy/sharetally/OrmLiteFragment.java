package me.qingy.sharetally;

import android.app.Fragment;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import me.qingy.sharetally.data.DatabaseHelper;

/**
 * Created by qing on 10/31/14.
 */
public class OrmLiteFragment extends Fragment {
    private DatabaseHelper databaseHelper = null;

    protected DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper =
                    OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }
}
