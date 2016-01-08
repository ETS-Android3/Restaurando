package com.kevinlamcs.android.restaurando;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class SearchRestaurantActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return SearchRestaurantFragment.newInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
}
