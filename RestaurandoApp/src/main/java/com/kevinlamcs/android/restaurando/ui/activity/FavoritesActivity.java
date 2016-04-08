package com.kevinlamcs.android.restaurando.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;


import com.kevinlamcs.android.restaurando.R;

import com.kevinlamcs.android.restaurando.ui.fragment.DonateDialogFragment;
import com.kevinlamcs.android.restaurando.ui.fragment.FavoritesFragment;
import com.kevinlamcs.android.restaurando.ui.activity.root.SingleFragmentActivity;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Activity class for hosting the FavoritesFragment.
 */
public class FavoritesActivity extends SingleFragmentActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "donate";

    private AlertDialog howToDialog;

    @Override
    protected Fragment createFragment() {
        return FavoritesFragment.newInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_favorites_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id
                .activity_favorites_drawer_layout_container);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (howToDialog != null) {
            howToDialog.dismiss();
            howToDialog = null;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_favorites_drawer_layout_container);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        LayoutInflater inflater = getLayoutInflater();

        if (id == R.id.nav_donate) {
            CharSequence[] items = getResources().getStringArray(R.array.donation_values);
            DialogFragment donateFragment = DonateDialogFragment.newInstance(items);
            donateFragment.show(getSupportFragmentManager(), TAG);
        } else if (id == R.id.how_to) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this,
                    R.style.AlertDialogRemoveList).setTitle(getString(R.string.title_how_to));
            View v = inflater.inflate(R.layout.how_to_instructions, null, false);
            builder.setView(v);
            howToDialog = builder.create();
            howToDialog.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_favorites_drawer_layout_container);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
