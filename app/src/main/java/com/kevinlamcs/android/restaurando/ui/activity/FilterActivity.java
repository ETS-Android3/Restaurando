package com.kevinlamcs.android.restaurando.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.kevinlamcs.android.restaurando.R;
import com.kevinlamcs.android.restaurando.ui.activity.root.SingleFragmentActivity;
import com.kevinlamcs.android.restaurando.ui.fragment.FilterFragment;

/**
 * Created by kevin-lam on 1/28/16.
 */
public class FilterActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return FilterFragment.newInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }
}
