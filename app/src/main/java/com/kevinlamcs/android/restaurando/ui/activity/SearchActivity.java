package com.kevinlamcs.android.restaurando.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.kevinlamcs.android.restaurando.R;
import com.kevinlamcs.android.restaurando.ui.fragment.SearchFragment;
import com.kevinlamcs.android.restaurando.ui.activity.root.SingleFragmentActivity;
import com.kevinlamcs.android.restaurando.utils.LocationUtils;

public class SearchActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return SearchFragment.newInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbarSearchTerm = (Toolbar) findViewById(R.id.activity_search_search_toolbar_term);
        setSupportActionBar(toolbarSearchTerm);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Toolbar toolbarSearchLocation = (Toolbar) findViewById(R.id.activity_search_search_toolbar_location);
        toolbarSearchLocation.inflateMenu(R.menu.search_bottom);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationUtils.endGPS(getApplicationContext());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == FavoritesActivity.REQUEST_RESTAURANT) &&
                (resultCode == Activity.RESULT_OK)) {
            setResult(Activity.RESULT_OK, data);
            finish();
        }
    }
}
