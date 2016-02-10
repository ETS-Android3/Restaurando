package com.kevinlamcs.android.restaurando.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.kevinlamcs.android.restaurando.R;
import com.kevinlamcs.android.restaurando.ui.fragment.InfoFragment;
import com.kevinlamcs.android.restaurando.ui.activity.root.SingleFragmentActivity;

/**
 * Created by kevin-lam on 1/24/16.
 */
public class InfoActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return InfoFragment.newInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = (Toolbar)findViewById(R.id.activity_add_restaurant_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
}
