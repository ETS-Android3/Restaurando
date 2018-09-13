package com.kevinlamcs.android.restaurando.ui.fragment;


import android.Manifest;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kevinlamcs.android.restaurando.R;
import com.kevinlamcs.android.restaurando.application.RestaurandoApplication;
import com.kevinlamcs.android.restaurando.operations.YelpSearch;
import com.kevinlamcs.android.restaurando.ui.activity.SearchActivity;
import com.kevinlamcs.android.restaurando.ui.adapter.SearchAdapter;
import com.kevinlamcs.android.restaurando.ui.model.Restaurant;
import com.kevinlamcs.android.restaurando.utils.ConnectionManager;
import com.kevinlamcs.android.restaurando.utils.LocationDependent;
import com.kevinlamcs.android.restaurando.utils.LocationManager;
import com.kevinlamcs.android.restaurando.utils.PermissionManager;

import java.util.ArrayList;
import java.util.List;


/**
 * Fragment class for searching restaurants from Yelp.
 */
public class SearchFragment extends Fragment implements LocationDependent {

    public static final int SEARCH_ACTION_CODE = 100;
    private static final String STATE_RESTAURANT_LIST = "RestaurantList state";

    private Tracker tracker;

    private EditText editTextSearchTerm;
    private EditText editTextSearchLocation;
    private ImageButton imageButtonClearTerm;
    private ImageButton imageButtonClearLocation;
    private RelativeLayout circularProgressBar;
    private RelativeLayout noConnectivityState;
    private RelativeLayout noResultState;

    private RecyclerView recyclerView;
    private List<Restaurant> restaurantList = new ArrayList<>();

    public static boolean isSearchNearby = true;
    private ConnectionManager connectionManager;
    private PermissionManager permissionManager;
    private LocationManager locationManager;

    /**
     * Constructs a new SearchFragment.
     * @return SearchFragment
     */
    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onRestoreInstanceState(savedInstanceState);

        tracker = ((RestaurandoApplication)getActivity().getApplication()).getDefaultTracker();
        tracker.setScreenName("SearchFragment");
        connectionManager = ConnectionManager.getInstance(getActivity().getApplicationContext());
        permissionManager = PermissionManager.getInstance();
        locationManager = LocationManager.getInstance(getActivity().getApplicationContext());
        locationManager.setLocationDependent(this);
        permissionManager.addPermissionAction(SEARCH_ACTION_CODE, locationManager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_restaurant_list, container, false);
        recyclerView = (RecyclerView) v.findViewById(
                R.id.fragment_search_restaurant_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new SearchAdapter(restaurantList));
        noConnectivityState = (RelativeLayout)v.findViewById(
                R.id.fragment_search_restaurant_list_empty_state);
        noResultState = (RelativeLayout)v.findViewById(
                R.id.fragment_search_restaurant_list_error_search_state);
        circularProgressBar = (RelativeLayout)getActivity().findViewById(
                R.id.activity_search_progress_bar_circular_query_yelp);

        if (connectionManager.isConnectedToNetwork()) {
            setVisibilityNoConnectivityState(false);
        } else {
            setVisibilityNoConnectivityState(true);
        }

        setUpSearchTerm();
        setUpSearchLocation();
        setUpDefaultSearch();
        return v;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(STATE_RESTAURANT_LIST, (ArrayList<Restaurant>) restaurantList);
    }

    public void onRequestPermissionsResult(int actionCode, String[] permissions, int[] grantResults) {
        permissionManager.onRequestPermissionsResult(actionCode, permissions, grantResults);
    }

    /**
     * Displays the layout when there is no internet connection.
     * @param visible - Whether it is visible or invisible
     */
    private void setVisibilityNoConnectivityState(boolean visible) {
        if (visible) {
            recyclerView.setVisibility(View.GONE);
            noResultState.setVisibility(View.GONE);
            noConnectivityState.setVisibility(View.VISIBLE);
        } else {
            noConnectivityState.setVisibility(View.GONE);
            noResultState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Retrieve saved state information.
     * @param savedState - saved state
     */
    private void onRestoreInstanceState(Bundle savedState) {
        if (savedState != null) {
            restaurantList = savedState.getParcelableArrayList(STATE_RESTAURANT_LIST);
        }
    }

    /**
     * Displays layout when the search returns no results.
     */
    private void setVisibilityNoResultState() {
        recyclerView.setVisibility(View.GONE);
        noConnectivityState.setVisibility(View.GONE);
        noResultState.setVisibility(View.VISIBLE);
    }

    /**
     * Sets up the search bar for entering in the term to be searched.
     */
    private void setUpSearchTerm() {
        // Sets up search term
        editTextSearchTerm = (EditText) getActivity().findViewById(
                R.id.activity_search_edit_text_search_term);
        editTextSearchTerm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    imageButtonClearTerm.setVisibility(View.VISIBLE);
                } else {
                    imageButtonClearTerm.setVisibility(View.GONE);
                }
            }
        });

        // Sets up the clear button for search term
        imageButtonClearTerm = (ImageButton) getActivity().findViewById(
                R.id.activity_search_clear_term);
        imageButtonClearTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextSearchTerm.setText("");
            }
        });
    }

    /**
     * Sets up the search bar for entering in the location to be searched.
     */
    private void setUpSearchLocation() {
        // Sets up search location
        editTextSearchLocation = (EditText) getActivity().findViewById(
                R.id.activity_search_edit_text_search_location);
        editTextSearchLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    imageButtonClearLocation.setVisibility(View.VISIBLE);
                } else {
                    imageButtonClearLocation.setVisibility(View.GONE);
                }
            }
        });

        // Sets up submit search when the enter button is pressed
        editTextSearchLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    onSearch();
                    handled = true;
                }
                return handled;
            }
        });

        // Sets up the clear button for search location
        imageButtonClearLocation = (ImageButton) getActivity().findViewById(
                R.id.activity_search_clear_location);
        imageButtonClearLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextSearchLocation.setText("");
            }
        });
    }

    /**
     * Sets up the default search button which has a default term of "Food" and a default location
     * of "Nearby".
     */
    private void setUpDefaultSearch() {
        ImageButton imageButtonDefaultSearch = (ImageButton) getActivity().findViewById(
                R.id.activity_search_image_button_default_search);
        imageButtonDefaultSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButtonClearTerm.performClick();
                imageButtonClearLocation.performClick();
                onSearch();
            }
        });
    }

    /**
     * Conducts a search on Yelp when there is internet connection. Otherwise, display the
     * no connectivity layout.
     */
    private void onSearch() {
        // Hide keyboard
        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextSearchLocation.getWindowToken(), 0);

        // List becomes the point of focus
        recyclerView.requestFocus();

        showProgressBar(true);

        String term = editTextSearchTerm.getText().toString();
        String location = editTextSearchLocation.getText().toString();

        if (connectionManager.isConnectedToNetwork()) {
            if (location.isEmpty()) {
                isSearchNearby = true;
                permissionManager.setActivity(getActivity());
                permissionManager.verifyPermission(Manifest.permission.ACCESS_FINE_LOCATION, SEARCH_ACTION_CODE);
            } else {
                isSearchNearby = false;
                sendYelpSearch(term);
                new BackgroundYelpSearchByLocation(getContext()).execute(term, location);
            }
        } else {
            showProgressBar(false);
            setVisibilityNoConnectivityState(true);
        }
    }

    /**
     * Display the circular progress bar which represents the loading of Yelp information.
     * @param visible - Whether the progress bar is visible or invisible
     */
    private void showProgressBar(boolean visible) {
        SearchActivity searchActivity = ((SearchActivity)getActivity());
        if (visible) {
            if (searchActivity != null) {
                circularProgressBar.setVisibility(View.VISIBLE);
                searchActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        } else {
            if (searchActivity != null) {
                searchActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                circularProgressBar.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Sends search action to Google Analytics.
     * @param term - The term being searched
     */
    private void sendYelpSearch(String term) {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Search")
                .setAction("Executed")
                .setLabel(term)
                .setValue(1)
                .build());
    }

    @Override
    public void withLocation(String location) {
        String term = editTextSearchTerm.getText().toString();
        sendYelpSearch(term);
        new BackgroundYelpSearchByLocation(getContext()).execute(term, location);
    }

    /**
     * AsyncTask class used to conduct the Yelp search.
     */
    private class BackgroundYelpSearchByLocation extends AsyncTask<String, Void, List<Restaurant>> {

        private final Context context;

        public BackgroundYelpSearchByLocation(Context context) {
            this.context = context;
        }

        @Override
        protected List<Restaurant> doInBackground(String... params) {
            return new YelpSearch().queryYelpByLocation(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(List<Restaurant> restaurantList) {
            SearchFragment.this.restaurantList = restaurantList;

            if (isAdded()) {
                ((SearchAdapter) recyclerView.getAdapter()).setRestaurantList(SearchFragment.this.restaurantList);
            }

            locationManager.cancelLocationRequest();
            showProgressBar(false);

            if (restaurantList.isEmpty()) {
                setVisibilityNoResultState();
            } else {
                setVisibilityNoConnectivityState(false);
            }
        }
    }
}
