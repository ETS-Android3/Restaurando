package com.kevinlamcs.android.restaurando;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;

public class SearchRestaurantActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_restaurant_app_bar);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.search_container);

        if (fragment == null) {
            fragment = new SearchRestaurantFragment();
            fm.beginTransaction().add(R.id.search_container, fragment).commit();
        }

        Toolbar toolbar2 = (Toolbar) findViewById(R.id.search_toolbar2);
        toolbar2.inflateMenu(R.menu.search2);

        SearchView searchViewBottom = (SearchView)findViewById(R.id.search_bottom);
        SearchManager searchManager2 = (SearchManager) getSystemService(Context
                .SEARCH_SERVICE);
        searchViewBottom.setSearchableInfo(searchManager2.getSearchableInfo(getComponentName()));
        searchViewBottom.setQueryHint("Nearby");
        searchViewBottom.onActionViewExpanded();
        LinearLayout ll1 = (LinearLayout) searchViewBottom.getChildAt(0);
        LinearLayout ll2 = (LinearLayout) ll1.getChildAt(2);
        LinearLayout ll3 = (LinearLayout) ll2.getChildAt(1);
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) ll3.getChildAt(0);
        autoCompleteTextView.setTextSize(20);

        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
}
