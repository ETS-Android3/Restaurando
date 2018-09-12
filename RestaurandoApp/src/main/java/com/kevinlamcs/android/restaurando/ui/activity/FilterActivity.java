package com.kevinlamcs.android.restaurando.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.kevinlamcs.android.restaurando.R;
import com.kevinlamcs.android.restaurando.ui.activity.root.SingleFragmentActivity;
import com.kevinlamcs.android.restaurando.ui.fragment.FilterFragment;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Activity class for hosting FilterFragment.
 */
public class FilterActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return FilterFragment.newInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        Toolbar toolbar = (Toolbar)findViewById(R.id.activity_filter_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
