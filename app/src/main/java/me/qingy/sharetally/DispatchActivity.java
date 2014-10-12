package me.qingy.sharetally;

import com.parse.ui.ParseLoginDispatchActivity;

/**
 * Created by Qing on 10/11/2014.
 */
public class DispatchActivity extends ParseLoginDispatchActivity {

    @Override
    protected Class<?> getTargetClass() {
        return TallyListActivity.class;
    }
}
