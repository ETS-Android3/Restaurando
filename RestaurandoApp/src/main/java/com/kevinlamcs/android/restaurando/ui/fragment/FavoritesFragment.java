package com.kevinlamcs.android.restaurando.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.MatrixCursor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.kevinlamcs.android.restaurando.database.RestaurantListing;
import com.kevinlamcs.android.restaurando.R;
import com.kevinlamcs.android.restaurando.application.RestaurandoApplication;
import com.kevinlamcs.android.restaurando.operations.ShakeDetector;
import com.kevinlamcs.android.restaurando.ui.activity.FilterActivity;
import com.kevinlamcs.android.restaurando.ui.activity.InfoActivity;
import com.kevinlamcs.android.restaurando.ui.activity.SearchActivity;
import com.kevinlamcs.android.restaurando.ui.adapter.FavoritesAdapter;
import com.kevinlamcs.android.restaurando.ui.adapter.ToolbarSpinnerAdapter;
import com.kevinlamcs.android.restaurando.ui.callback.FavoritesAdapterCallback;
import com.kevinlamcs.android.restaurando.ui.model.FavoritesList;
import com.kevinlamcs.android.restaurando.ui.model.FilterOptions;
import com.kevinlamcs.android.restaurando.ui.model.Restaurant;
import com.kevinlamcs.android.restaurando.ui.widget.InsetDivider;
import com.kevinlamcs.android.restaurando.ui.widget.SameSelectionSpinner;
import com.kevinlamcs.android.restaurando.ui.widget.Subheader;
import com.kevinlamcs.android.restaurando.utils.DecoderUtils;
import com.kevinlamcs.android.restaurando.utils.PermissionManager;
import com.kevinlamcs.android.restaurando.vending.IabHelper;
import com.kevinlamcs.android.restaurando.vending.IabResult;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.support.v7.app.ActionBar.*;
import static com.kevinlamcs.android.restaurando.database.RestaurantDbSchema.*;

/**
 * Fragment class for displaying and handling the favorited list.
 */
public class FavoritesFragment extends Fragment implements FavoritesAdapterCallback {

    public static final String EXTRA_FILTER_OPTIONS = "com.kevinlamcs.android.restaurando.ui" +
            ".extra.FILTER_OPTIONS";

    private static final String PREF_LIST_LISTRESTAURANT_TITLES = "pref_list_listRestaurant_titles";

    private static final String STATE_FAVORITES_LIST = "state_favoritesList";
    private static final String STATE_FILTER_OPTIONS = "state_filterOptions";
    private static final String STATE_IS_SHAKE_ENABLED = "state_isShakeEnabled";
    private static final String STATE_IS_SINGLE_SEARCH_ENABLED = "state_isSingleSearchEnabled";
    private static final String STATE_SEARCH_QUERY = "state_searchQuery";

    public static final int REQUEST_RESTAURANT = 0;
    private static final int REQUEST_FILTER = 1;
    public static final int REQUEST_DONATE = 2;

    private static final long VIBRATE_DURATION = 200;

    private static final int SINGLE_SEARCH_MAX_WIDTH = 10000;
    private static final int WRITE_EXTERNAL_ACTION_CODE = 99;

    private Tracker tracker;

    private RecyclerView recyclerView;
    private RelativeLayout emptyView;
    private FavoritesAdapter favoritesRestaurantListAdapter;

    private ToolbarSpinnerAdapter spinnerAdapter;

    private SameSelectionSpinner spinner;
    private EditText editText;

    private FloatingActionButton fab;

    private FavoritesList<Restaurant> listFavorites;
    private FilterOptions filterOptions = new FilterOptions();
    private SensorManager sensorManager;
    private ShakeDetector shakeDetector;
    private Sensor accelerometer;
    private boolean isShakeEnabled = true;
    private boolean isSingleSearchEnabled = false;
    private SearchView searchView;
    private String singleSearchQuery;

    private Menu menu;

    private MenuItem renameDone;

    private RecyclerView.ItemDecoration subheader;

    private String currListName;
    private PermissionManager permissionManager;

    /** Creates a new instance of the favorites list */
    public static FavoritesFragment newInstance() {
        return new FavoritesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        onRestoreInstanceState(savedInstanceState);
        setUpShakeForRandomize();

        tracker = ((RestaurandoApplication)getActivity().getApplication()).getDefaultTracker();
        tracker.setScreenName("FavoritesFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate and set up the scrollable favorites list.
        View view = inflater.inflate(R.layout.fragment_favorites_restaurant_list, container, false);
        emptyView = (RelativeLayout) view.findViewById(
                R.id.fragment_favorites_restaurant_list_empty_state);
        recyclerView = (RecyclerView) view.findViewById(
                R.id.fragment_favorites_restaurant_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setUpFab();
        setUpSpinnerListRestaurantList();
        setUpSwipeToDelete();
        setUpFavoritesList();

        permissionManager = PermissionManager.getInstance();
        permissionManager.addPermissionAction(WRITE_EXTERNAL_ACTION_CODE, favoritesRestaurantListAdapter);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.favorites, menu);

        this.menu = menu;
        renameDone = menu.findItem(R.id.rename_done);

        MenuItem toggleItem = menu.findItem(R.id.toggle_shake);
        setUpToggleShake(toggleItem);

        MenuItem singleSearchItem = menu.findItem(R.id.search_restaurant);
        setUpSingleSearch(menu, singleSearchItem);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:
                filter();
                return true;
            case R.id.add_restaurant_list:
                add();
                return true;
            case R.id.rename_restaurant_list:
                setUpRename();
                return true;
            case R.id.remove_restaurant_list:
                remove();
                return true;
            case R.id.rename_done:
                rename();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_FAVORITES_LIST, favoritesRestaurantListAdapter.getFavoritesList());
        outState.putParcelable(STATE_FILTER_OPTIONS, favoritesRestaurantListAdapter
                .getFilterOptions());
        outState.putBoolean(STATE_IS_SHAKE_ENABLED, isShakeEnabled);

        // Save user input in the searchview if it is still open
        if (searchView != null) {
            isSingleSearchEnabled = !searchView.isIconified();
            outState.putBoolean(STATE_IS_SINGLE_SEARCH_ENABLED, isSingleSearchEnabled);
            if (isSingleSearchEnabled) {
                singleSearchQuery = searchView.getQuery().toString();
                outState.putString(STATE_SEARCH_QUERY, searchView.getQuery().toString());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(shakeDetector);
        setListTitles();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_RESTAURANT && resultCode == Activity.RESULT_OK) {
            Restaurant restaurant = data.getParcelableExtra(AddFragment.EXTRA_MY_RESTAURANT);
            favoritesRestaurantListAdapter.setProspectiveRestaurant(restaurant);
            permissionManager.setActivity(getActivity());
            permissionManager.verifyPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_ACTION_CODE);
            //favoritesRestaurantListAdapter.addRestaurant(restaurant);
        } else if (requestCode == REQUEST_FILTER && resultCode == Activity.RESULT_OK) {
            filterOptions = data.getParcelableExtra(FilterFragment.EXTRA_FILTER);
            favoritesRestaurantListAdapter.setFilterOptions(filterOptions);
            favoritesRestaurantListAdapter.onFilter();
        } else if (requestCode == REQUEST_DONATE && resultCode == Activity.RESULT_OK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),
                    R.style.AlertDialogRemoveList);
            builder.setTitle(getContext().getString(R.string.donate_result_title));
            builder.setMessage(getContext().getString(R.string.donate_result_message));
            builder.setPositiveButton(getContext().getString(R.string.donate_result_positive_button)
                    , null);
            builder.show();
        }
    }

    @Override
    public void addSubheaderCallback() {
        if (subheader == null) {
            subheader = new Subheader(getContext(),
                    favoritesRestaurantListAdapter.getSelectedItemStartPosition());
            recyclerView.addItemDecoration(subheader);
        } else {
            removeSubheaderCallback();
            addSubheaderCallback();
        }
    }

    @Override
    public void removeSubheaderCallback() {
        if (subheader != null) {
            recyclerView.removeItemDecoration(subheader);
            subheader = null;
        }
    }

    @Override
    public void emptyStateVisibilityCallback(boolean isEmpty) {
        if (isEmpty) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    /**
     * Retrieves the saved state information.
     * @param savedState - save state
     */
    private void onRestoreInstanceState(Bundle savedState) {
        if (savedState != null) {
            listFavorites = savedState.getParcelable(STATE_FAVORITES_LIST);
            filterOptions = savedState.getParcelable(STATE_FILTER_OPTIONS);
            isShakeEnabled = savedState.getBoolean(STATE_IS_SHAKE_ENABLED);

            isSingleSearchEnabled = savedState.getBoolean(STATE_IS_SINGLE_SEARCH_ENABLED);
            if (isSingleSearchEnabled) {
                singleSearchQuery = savedState.getString(STATE_SEARCH_QUERY);
            }
        }
    }

    public void onRequestPermissionsResult(int actionCode, String[] permissions, int[] grantResults) {
        permissionManager.onRequestPermissionsResult(actionCode, permissions, grantResults);
    }

    /**
     * Starts the filter activity which allows the user to filter and narrow the favorites list.
     */
    private void filter() {
        FilterOptions filterOptions = setUpFilterOptions();
        Intent intent = new Intent(getActivity(), FilterActivity.class);
        intent.putExtra(EXTRA_FILTER_OPTIONS, filterOptions);
        getActivity().startActivityForResult(intent, REQUEST_FILTER);
    }

    /**
     * Adds a new default favorites list to the spinner.
     */
    private void add() {
        spinnerAdapter.add();
    }

    /**
     * Sets up the toolbar and floating action button for renaming by disabling and hiding their
     * functionality.
     */
    private void setUpRename() {
        // Disable all buttons and functionality unrelated to renaming and enable those related
        setRenameVisibility(true);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        final InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

        editText.setText(spinnerAdapter.getTitle());
    }

    /**
     * When renameDone button is clicked, renames the active favorites list in the spinner drop
     * down.
     */
    private void rename() {
        String oldTitleName = spinnerAdapter.getTitle();
        String newTitleName = editText.getText().toString();

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout)getActivity().findViewById(
                R.id.activity_favorites_coordinator_layout);

        // Two possible title names that should be handled
        if (spinnerAdapter.hasDuplicate(newTitleName)) {
            Snackbar.make(coordinatorLayout, R.string.duplicate_name, Snackbar.LENGTH_LONG).show();
            return;
        }
        if (newTitleName.trim().length() == 0) {
            Snackbar.make(coordinatorLayout, R.string.minimum_name_length, Snackbar.LENGTH_LONG).show();
            return;
        }

        spinnerAdapter.recycleNewTitle(oldTitleName);
        spinnerAdapter.rename(oldTitleName, newTitleName);

        currListName = (String) spinner.getSelectedItem();
        favoritesRestaurantListAdapter.setCurrentRestaurantList(currListName);

        setRenameVisibility(false);
        final InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    /**
     * When the user clicks the remove button, shows a warning dialog. Then removes a favorites
     * list title along with all the restaurants in the list.
     */
    private void remove() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),
                R.style.AlertDialogRemoveList);
        builder.setTitle(getString(R.string.remove_list, currListName));
        builder.setMessage(getContext().getString(R.string.remove_list_warning));
        builder.setPositiveButton(getContext().getString(R.string.remove_list_warning_positive_button),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        favoritesRestaurantListAdapter.removeRestaurantList();
                        spinnerAdapter.remove();
                    }
                });
        builder.setNegativeButton(getContext().getString(R.string.remove_list_warning_negative_button),
                null);
        builder.show();
    }

    /**
     * Sets up the adapter for the favorites list.
     */
    private void setUpFavoritesList() {
        favoritesRestaurantListAdapter = new FavoritesAdapter(getContext(),
                FavoritesFragment.this, currListName, listFavorites, filterOptions);
        recyclerView.addItemDecoration(new InsetDivider(getContext()));
        favoritesRestaurantListAdapter.toggleSubheader(false);
        recyclerView.setAdapter(favoritesRestaurantListAdapter);
    }

    /**
     * Sets up the floating action button.
     */
    private void setUpFab() {
        fab = (FloatingActionButton) getActivity().findViewById(
                R.id.activity_add_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivityForResult(new Intent(v.getContext(),
                        SearchActivity.class), REQUEST_RESTAURANT);
            }
        });
    }

    /**
     * Retrieves the filter options.
     * @return filterOptions
     */
    private FilterOptions setUpFilterOptions() {
        return favoritesRestaurantListAdapter.getFilterOptions();
    }

    /**
     * Sets up swipe to remove restaurants from the favorites list.
     */
    private void setUpSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Restaurant restaurant = favoritesRestaurantListAdapter.getRestaurant(
                        viewHolder.getAdapterPosition());
                favoritesRestaurantListAdapter.removeRestaurant(restaurant);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * Sets up toggle switch to enable shaking for randomized selection.
     * @param toggleItem - toggle button
     */
    private void setUpToggleShake(MenuItem toggleItem) {
        SwitchCompat toggleButton = (SwitchCompat) MenuItemCompat.getActionView(toggleItem);
        toggleButton.setBackgroundResource(android.R.color.transparent);
        toggleButton.setChecked(isShakeEnabled);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isShakeEnabled = isChecked;
            }
        });
    }

    /**
     * Sets up the ability to shake and randomly select a favorited restaurant.
     */
    private void setUpShakeForRandomize() {
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        shakeDetector = new ShakeDetector();
        shakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                if (isShakeEnabled) {
                    onShakeRandomize();
                }
            }
        });
    }

    /**
     * Selects a restaurant by random from the selected restaurants and displays it.
     */
    private void onShakeRandomize() {
        Restaurant restaurant = favoritesRestaurantListAdapter.getRandomRestaurant();

        if (restaurant != null) {
            sendShake();

            Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);

            Intent intent = new Intent(getActivity(), InfoActivity.class);
            intent.putExtra(FavoritesAdapter.EXTRA_INFO, restaurant);
            startActivity(intent);
        }
    }

    /**
     * Sets up single restaurant search.
     * @param menu - Used to hide toolbar items
     * @param singleSearchItem - single search searchview
     */
    private void setUpSingleSearch(final Menu menu, final MenuItem singleSearchItem) {
        searchView = (SearchView) MenuItemCompat.getActionView(singleSearchItem);
        searchView.setQueryHint("Search Restaurant");
        searchView.setMaxWidth(SINGLE_SEARCH_MAX_WIDTH);
        searchView.setImeOptions(searchView.getImeOptions() | EditorInfo.IME_FLAG_NO_FULLSCREEN |
                EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                displaySuggestions(newText);
                return true;
            }
        });
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                MatrixCursor cursor = (MatrixCursor) (searchView.getSuggestionsAdapter()
                        .getCursor());
                if (cursor.moveToPosition(position)) {
                    String restaurantId = cursor.getString(cursor.getColumnIndex(
                            RestaurantTable.Cols.ID));
                    favoritesRestaurantListAdapter.setSingleSearchRestaurant(restaurantId);
                    favoritesRestaurantListAdapter.onSingleSearch();
                }
                singleSearchItem.collapseActionView();
                setToolbarItemsVisibility(menu, renameDone, true);
                spinner.setVisibility(View.VISIBLE);

                return true;
            }
        });
        MenuItemCompat.setOnActionExpandListener(singleSearchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                setToolbarItemsVisibility(menu, renameDone, false);
                spinner.setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                setToolbarItemsVisibility(menu, renameDone, true);
                spinner.setVisibility(View.VISIBLE);
                return true;
            }
        });

        if (isSingleSearchEnabled) {
            MenuItemCompat.expandActionView(singleSearchItem);
            searchView.setQuery(singleSearchQuery, false);
        }

        final AutoCompleteTextView searchAutoCompleteTextView = (AutoCompleteTextView) searchView
                .findViewById(R.id.search_src_text);
        searchAutoCompleteTextView.setThreshold(1);
    }

    /**
     * Hides all of the toolbar items except for the rename done icon.
     * @param menu - Toolbar menu
     * @param exception - Rename done icon that doesn't need to be hidden.
     * @param visible - Whether to set the icons visible or invisible
     */
    private void setToolbarItemsVisibility(Menu menu, MenuItem exception, boolean visible) {
        for (int i=0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item != exception) {
                item.setVisible(visible);
            }
        }
    }

    /**
     * Retrieves a suggestion list of restaurants that match closely to the user input.
     * @param query - User input
     */
    private void displaySuggestions(String query) {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{BaseColumns._ID,
                RestaurantTable.Cols.NAME, RestaurantTable.Cols.ID});
        RestaurantListing restaurantListing = new RestaurantListing(getContext());
        restaurantListing.getRestaurantSuggestions(currListName, "Name", matrixCursor, query);

        setUpSearchViewSuggestions(matrixCursor);
    }

    /**
     * Sets up the adapter required to display the list of restaurant suggestions.
     * @param cursor - Used to navigate through the suggested restaurant list.
     */
    private void setUpSearchViewSuggestions(MatrixCursor cursor) {
        String[] from = new String[]{RestaurantTable.Cols.NAME};
        int[] to = new int[]{android.R.id.text1};
        android.support.v4.widget.SimpleCursorAdapter adapter = new android.support.v4.widget.SimpleCursorAdapter(
                getActivity().getBaseContext(),
                R.layout.search_view_suggestions_list_item,
                cursor,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        searchView.setSuggestionsAdapter(adapter);
    }

    /**
     * Sets up drop down list for the multitude of favorites list created by the user.
     */
    private void setUpSpinnerListRestaurantList() {
        // Add spinner layout view to the toolbar.
        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.activity_favorites_toolbar);
        View spinnerContainer = LayoutInflater.from(getActivity()).inflate(R.layout.spinner_toolbar,
                toolbar, false);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        toolbar.addView(spinnerContainer, layoutParams);

        // Defines editText and spinner (switchable) to enable title editing.
        editText = (EditText) spinnerContainer.findViewById(
                R.id.spinner_toolbar_edit_text_list_title);
        spinner = (SameSelectionSpinner) spinnerContainer.findViewById(
                R.id.spinner_toolbar_multiple_favorites_list);

        spinnerAdapter = new ToolbarSpinnerAdapter(getActivity().getApplicationContext(), spinner);

        // Gets all titles from Shared Preferences. If there are none, adds a blank list titled
        // "New1".
        List<String> listTitles = getListTitles();
        if (listTitles.isEmpty()) {
            spinnerAdapter.add();
        } else {
            spinnerAdapter.addItems(listTitles);
        }

        spinner.setAdapter(spinnerAdapter);
        currListName = (String)spinner.getSelectedItem();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerAdapter.setTitlePosition(position);
                currListName = (String) spinner.getSelectedItem();
                if (favoritesRestaurantListAdapter != null) {
                    favoritesRestaurantListAdapter.setCurrentRestaurantList(currListName);

                    if (spinner.isItemChanged()) {
                        favoritesRestaurantListAdapter.onListChange();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Retrieves list of the titles of favorites list from Shared Preferences.
     * @return List of listFavorites titles
     */
    private List<String> getListTitles() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String jsonListRestaurantList = preferences.getString(PREF_LIST_LISTRESTAURANT_TITLES, "");
        Gson gson = new Gson();

        if (!jsonListRestaurantList.equals("")) {
            return gson.fromJson(jsonListRestaurantList, List.class);
        }
        return new ArrayList<>();
    }

    /**
     * Stores list of the titles of favorites list to Shared Preferences.
     */
    private void setListTitles() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Gson gson = new GsonBuilder().create();
        String jsonListRestaurantList = gson.toJson(spinnerAdapter.getListRestaurantList());

        preferences.edit().putString(PREF_LIST_LISTRESTAURANT_TITLES, jsonListRestaurantList).apply();
    }

    /**
     * Sets visibility of toolbar icons for renaming.
     * @param visible - visibility of renaming
     */
    private void setRenameVisibility(boolean visible) {

        int spinnerVisibility, editTextVisibility;
        int fabColor;

        if (visible) {
            spinnerVisibility = View.GONE;
            editTextVisibility = View.VISIBLE;
            fabColor = R.color.colorWhiteBackgroundDisabled;
        } else {
            spinnerVisibility = View.VISIBLE;
            editTextVisibility = View.GONE;
            fabColor = R.color.colorAccent;
        }

        fab.setEnabled(!visible);
        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), fabColor)));
        setToolbarItemsVisibility(menu, renameDone, !visible);
        renameDone.setVisible(visible);
        spinner.setVisibility(spinnerVisibility);
        editText.setVisibility(editTextVisibility);
    }

    /**
     * Sends randomize event to Google Analytics.
     */
    private void sendShake() {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Favorites")
                .setAction("Shake")
                .setLabel("Randomize")
                .setValue(1)
                .build());
    }
}
