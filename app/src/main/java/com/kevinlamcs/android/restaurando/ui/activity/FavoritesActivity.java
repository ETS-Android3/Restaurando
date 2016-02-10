package com.kevinlamcs.android.restaurando.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;

import com.kevinlamcs.android.restaurando.Database.RestaurantListing;
import com.kevinlamcs.android.restaurando.R;

import com.kevinlamcs.android.restaurando.operations.ShakeDetector;
import com.kevinlamcs.android.restaurando.ui.adapter.FavoritesAdapter;
import com.kevinlamcs.android.restaurando.ui.fragment.FavoritesFragment;
import com.kevinlamcs.android.restaurando.ui.activity.root.SingleFragmentActivity;
import com.kevinlamcs.android.restaurando.ui.model.FilterOptions;
import com.kevinlamcs.android.restaurando.ui.model.Restaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.kevinlamcs.android.restaurando.utils.TextStyleUtils.setFavoritesListTextStyle;


public class FavoritesActivity extends SingleFragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String EXTRA_FILTER_OPTIONS = "filter options";

    public static final int REQUEST_RESTAURANT = 0;
    public static final int REQUEST_FILTER = 1;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    private SwitchCompat mToggleShake;

    private AppCompatButton mToggleToolTip;

    private boolean mIsToggleShakeLongPressed = false;

    private static boolean sIsShakeEnabled = true;


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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.activity_search_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(v.getContext(), SearchActivity.class), REQUEST_RESTAURANT);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_favorites_drawer_layout_container);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.activity_favorites_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mToggleToolTip = (AppCompatButton) findViewById(R.id.button_tool_tip_custom);
        mToggleToolTip.setText("Toggle Shake");
        setFavoritesListTextStyle(getApplicationContext(), mToggleToolTip, "font/Roboto-Medium.ttf");
        mToggleToolTip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                if (sIsShakeEnabled) {
                    onShakeRandomize(count);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.favorites, menu);

        MenuItem item = menu.findItem(R.id.toggle_shake);
        mToggleShake = (SwitchCompat) MenuItemCompat.getActionView(item);
        mToggleShake.setBackgroundResource(android.R.color.transparent);
        mToggleShake.setChecked(sIsShakeEnabled);
        mToggleShake.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sIsShakeEnabled = isChecked;
            }
        });
        mToggleShake.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mToggleToolTip.setVisibility(View.VISIBLE);
                mIsToggleShakeLongPressed = true;
                return false;
            }
        });
        mToggleShake.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (mIsToggleShakeLongPressed) {
                        mToggleToolTip.setVisibility(View.INVISIBLE);
                        mIsToggleShakeLongPressed = false;
                    }
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:
                FavoritesFragment fragment =
                        (FavoritesFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                FilterOptions filterOptions = fragment.setupFilterOptions();

                Intent intent = new Intent(this, FilterActivity.class);
                intent.putExtra(EXTRA_FILTER_OPTIONS, filterOptions);
                startActivityForResult(intent, REQUEST_FILTER);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Log.i("FavoritesActivity", "onBackPressed in FavoritesActivity");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_favorites_drawer_layout_container);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_favorites_drawer_layout_container);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager
                .SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }


    private void onShakeRandomize(int count) {
        RestaurantListing restaurantListing = RestaurantListing.get(this);
        List<Restaurant> restaurantList = restaurantListing.getRestaurantList();
        Random random = new Random();
        Restaurant restaurant = restaurantList.get(random.nextInt(restaurantList.size()));

        Intent intent = new Intent(this, InfoActivity.class);
        intent.putExtra(FavoritesAdapter.EXTRA_INFO_RESTAURANT, restaurant);
        startActivity(intent);

    }
}
