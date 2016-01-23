package com.kevinlamcs.android.restaurando.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.Window;

import com.kevinlamcs.android.restaurando.R;
import com.kevinlamcs.android.restaurando.ui.fragment.AddFragment;
import com.kevinlamcs.android.restaurando.ui.fragment.root.SingleFragmentActivity;

/**
 * Created by kevin-lam on 1/16/16.
 */
public class AddActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return AddFragment.newInstance();
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
