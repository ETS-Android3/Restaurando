package com.kevinlamcs.android.restaurando.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.gc.materialdesign.views.ButtonRectangle;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kevinlamcs.android.restaurando.R;
import com.kevinlamcs.android.restaurando.application.RestaurandoApplication;
import com.kevinlamcs.android.restaurando.operations.YelpSearch;
import com.kevinlamcs.android.restaurando.ui.activity.InfoActivity;
import com.kevinlamcs.android.restaurando.ui.activity.YelpViewActivity;
import com.kevinlamcs.android.restaurando.ui.adapter.FavoritesAdapter;
import com.kevinlamcs.android.restaurando.ui.model.Restaurant;
import com.kevinlamcs.android.restaurando.ui.model.Thought;
import com.kevinlamcs.android.restaurando.utils.ConnectionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Fragment class for displaying restaurant information.
 */
public class InfoFragment extends Fragment {

    private Tracker tracker;

    private Restaurant restaurant;
    private JSONArray thoughtList = new JSONArray();

    private FrameLayout parentContainer;

    private FrameLayout progressBarCircular;
    private ImageView image;
    private TextView name;
    private LinearLayout reviewsAndRatings;
    private TextView reviews;
    private TextView ratings;
    private TextView streetAddress;
    private TextView cityStateZip;
    private ConnectionManager connectionManager;

    /**
     * Constructs a new InfoFragment.
     * @return InfoFragment
     */
    public static InfoFragment newInstance() {
        return new InfoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        tracker = ((RestaurandoApplication)getActivity().getApplication()).getDefaultTracker();
        tracker.setScreenName("InfoFragment");

        connectionManager = ConnectionManager.getInstance(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        restaurant = getActivity().getIntent().getParcelableExtra(FavoritesAdapter.EXTRA_INFO);

        View view = inflater.inflate(R.layout.fragment_add_restaurant, container, false);
        parentContainer = (FrameLayout) getActivity().findViewById(
                R.id.activity_add_parent_container);
        progressBarCircular = (FrameLayout) getActivity().findViewById(
                R.id.activity_add_progress_bar_circular_download_image);
        image = (ImageView) view.findViewById(R.id.fragment_add_restaurant_image);
        name = (TextView) view.findViewById(R.id.fragment_add_restaurant_name);
        reviewsAndRatings = (LinearLayout) view.findViewById(R.id.fragment_add_restaurant_reviews_and_ratings);
        reviews = (TextView) view.findViewById(R.id.fragment_add_restaurant_reviews);
        ratings = (TextView) view.findViewById(R.id.fragment_add_restaurant_rating);
        streetAddress = (TextView) view.findViewById(R.id.fragment_add_restaurant_street_address);
        cityStateZip = (TextView) view.findViewById(R.id.fragment_add_restaurant_city_state_zip);

        sendInfoName();
        retrieveRestaurantDetails(view);

        return view;
    }

    /**
     * Retrieve restaurant image from Yelp.
     */
    private void displayRestaurantImage() {
        if (isAdded()){
            Glide.with(this)
                    .load(restaurant.getImageUrl())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target,
                                                   boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
                                                       boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBarCircular.setVisibility(View.GONE);
                            ((InfoActivity) getActivity()).getSupportActionBar()
                                    .setDisplayHomeAsUpEnabled(true);
                            return false;
                        }
                    })
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(image);
        }
    }

    /**
     * Display the default image when there is no internet.
     * @param view - View to display the image
     */
    private void displayDefaultImage(View view) {
        LinearLayout placeHolder = (LinearLayout)view.findViewById(
                R.id.fragment_add_no_connectivity_place_holder);

        progressBarCircular.setVisibility(View.GONE);
        ((InfoActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(restaurant.getCategory());
        placeHolder.setBackgroundColor(color);
        placeHolder.setVisibility(View.VISIBLE);

        image.setVisibility(View.GONE);
    }

    /**
     * Sets the restaurant details such as address, rating, and reviews.
     * @param view - View to display thoughts
     */
    private void retrieveRestaurantDetails(View view) {

        name.setText(restaurant.getName());
        displayThoughts(view);

        // If internet, retrieve restaurant information and display it. Otherwise, set the default
        // image and hide the other restaurant details.
        if (connectionManager.isConnectedToNetwork()) {
            new BackgroundYelpSearchByBusiness(view).execute(restaurant.getId());
        } else {
            displayLimitedInformation(view);
        }
    }

    /**
     * Sets restaurant details.
     */
    private void setRestaurantDetails() {
        ratings.setText(restaurant.getRating());
        reviews.setText(getString(R.string.review_count, restaurant.getReviewCount()));
        streetAddress.setText(restaurant.getStreetAddress());
        cityStateZip.setText(restaurant.getCityStateZip());
    }

    /**
     * Sets up the information buttons that allows the user to call the restaurant, search for
     * directions, and find more details on Yelp.
     * @param view - Parent view of the buttons
     */
    private void setUpInfoButtons(View view) {

        ButtonRectangle buttonDial = (ButtonRectangle) view.findViewById(
                R.id.fragment_add_restaurant_button_rectangle_dial);
        ButtonRectangle buttonDirections = (ButtonRectangle) view.findViewById(
                R.id.fragment_add_restaurant_button_rectangle_directions);
        ButtonRectangle buttonYelp = (ButtonRectangle) view.findViewById(
                R.id.fragment_add_restaurant_button_rectangle_yelp);

        if (connectionManager.isConnectedToNetwork()) {
            buttonDial.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialRestaurantNumber(restaurant.getDisplayPhone());
                }
            });

            buttonDirections.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google" +
                            ".com/maps?daddr=" + restaurant.getStreetAddress() + "," +
                            restaurant.getCityStateZip()));
                    startActivity(intent);
                }
            });

            buttonYelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = YelpViewActivity.newIntent(getActivity(), restaurant.getUrl());
                    startActivity(intent);
                }
            });
        } else {
            buttonDial.setEnabled(false);
            buttonDial.setBackgroundColor(ContextCompat.getColor(getContext(),
                    R.color.colorWhiteBackgroundDisabled));
            buttonDial.setTextColor(ContextCompat.getColor(getContext(),
                    R.color.colorDarkTextDisabled));

            buttonDirections.setEnabled(false);
            buttonDirections.setBackgroundColor(ContextCompat.getColor(getContext(),
                    R.color.colorAccentDisabled));
            buttonDirections.setTextColor(ContextCompat.getColor(getContext(),
                    R.color.colorDarkTextDisabled));

            buttonYelp.setEnabled(false);
            buttonYelp.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDisabled));
            buttonYelp.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhiteTextDisabled));
        }
    }

    /**
     * Starts the implicit intent to dial the restaurants phone number.
     * @param phoneNumber - Restaurant's phone number
     */
    private void dialRestaurantNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * Displays the user's thoughts of the restaurant.
     * @param view - View of the table to display the thoughts
     */
    private void displayThoughts(View view) {
        ImageButton imageButtonAddThoughts = (ImageButton) view.findViewById(
                R.id.fragment_add_restaurant_image_button_add_thoughts);
        imageButtonAddThoughts.setVisibility(View.GONE);


        try {
            if (restaurant.getThoughtList() != null) {
                JSONObject json = new JSONObject(restaurant.getThoughtList());
                thoughtList = json.getJSONArray(Thought.THOUGHT);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TableLayout tableLayout = (TableLayout) view.findViewById(
                R.id.fragment_add_restaurant_bullet_list_thoughts);
        for (int index=0; index < thoughtList.length(); index++) {
            Thought thought = new Thought(getContext(), tableLayout);
            try {
                thought.setupDisplayThought(thoughtList.getString(index));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Shows limited restaurant information if Yelp does not have the image url.
     * @param view - used to display restaurant image and buttons
     */
    private void displayLimitedInformation(View view) {
        reviewsAndRatings.setVisibility(View.GONE);
        streetAddress.setVisibility(View.GONE);
        cityStateZip.setVisibility(View.GONE);
        displayDefaultImage(view);
        setUpInfoButtons(view);

        Snackbar snackBar = Snackbar.make(parentContainer, R.string.no_internet_limited_details,
                Snackbar.LENGTH_LONG);
        snackBar.show();
    }

    /**
     * Sends restaurant name to Google Analytics.
     */
    private void sendInfoName() {
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Info")
                .setAction("View")
                .setLabel(restaurant.getName())
                .build());
    }

    /**
     * AsyncTask class used to retrieve restaurant information from Yelp.
     */
    public class BackgroundYelpSearchByBusiness extends AsyncTask<String, Void, Restaurant> {

        private final View view;

        public BackgroundYelpSearchByBusiness(View view) {
            this.view = view;
        }

        @Override
        protected Restaurant doInBackground(String... params) {
            return new YelpSearch().queryYelpByBusiness(params[0]);
        }

        @Override
        protected void onPostExecute(Restaurant restaurant) {
            InfoFragment.this.restaurant = restaurant;

            if (restaurant.getImageUrl() != null) {
                displayRestaurantImage();
                setRestaurantDetails();
                setUpInfoButtons(view);
            } else {
                displayLimitedInformation(view);
            }
        }
    }
}
