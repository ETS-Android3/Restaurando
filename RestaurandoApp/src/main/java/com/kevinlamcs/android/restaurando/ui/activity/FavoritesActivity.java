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
import com.kevinlamcs.android.restaurando.utils.DecoderUtils;
import com.kevinlamcs.android.restaurando.vending.IabHelper;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Activity class for hosting the FavoritesFragment.
 */
public class FavoritesActivity extends SingleFragmentActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "donate";

    private static final String ENCODED_PUBLIC_KEY = "Dyg6J39eBCAhCA8UDDscJVUeUwkkKGUiJCIgJyRxahIMaT"
            + "k3IQsiIGE6IDgyDUUDcWEADFIcADE0Yx5RFh9IBAcWcS0HLQVPC1gTBRNOAAcGNABWUw0KSnQHQCQGeig5LR0"
            + "1Ez4GPDoOLFUFNklWHi0xLi5WdwY8OFgjGidDOTtvESg4ECdYA3FlJilSPiIBUjIvDkcOCw0OVloAVVJELApm"
            + "GxYjS0gnSQoBAhQKLDAmWBkQc1orDCldESgFCBQ1GA8ZABE3RQI3IgQnUHUjEXNVMh8vWQ4NeCAhKCwkQRxcV"
            + "RwmNQAdNFY7TQsPEDcnJiFwXEoVHhw8ciMaG0M2Ji0PXysPCBw0KhsLSnFbMl4BLQAkAwZMFgYRKCg1L0MqFy"
            + "JAEBVFZGUAGF80Gz0tDlQkEhgoBD48ZHcADEghVQgSGgAoGyhMDxEhTwcWKhwQHVcBIiNVORojPRhWShE/N3ME"
            + "Eh9CYAIWByJWUzJoPSYDAiAkPRsQAww0Gx4xCxB4B3E5MSM9KCE=";

    private static final String DECODER_KEY = "Base64EncodedPublicKey decode RSA public key";


    private AlertDialog howToDialog;

    private DonateDialogFragment donateDialogFragment;

    private IabHelper iabHelper;

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

        DrawerLayout drawer = (DrawerLayout) findViewById(
                R.id.activity_favorites_drawer_layout_container);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        String base64EncodedPublicKey = DecoderUtils.decode(ENCODED_PUBLIC_KEY, DECODER_KEY);
        iabHelper = new IabHelper(this, base64EncodedPublicKey);
        iabHelper.startSetup(null);
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
            donateDialogFragment = DonateDialogFragment.newInstance(items);
            donateDialogFragment.show(getSupportFragmentManager(), TAG);
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

        if (donateDialogFragment != null) {
            if (iabHelper.handleActivityResult(requestCode, resultCode, data)) {
                super.onActivityResult(requestCode, resultCode, data);
            }
            donateDialogFragment = null;
        }

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (iabHelper != null) {
            iabHelper.dispose();
            iabHelper = null;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public IabHelper getIabHelper() {
        return iabHelper;
    }
}
