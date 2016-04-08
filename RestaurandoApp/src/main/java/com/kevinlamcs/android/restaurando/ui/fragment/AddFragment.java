package com.kevinlamcs.android.restaurando.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.kevinlamcs.android.restaurando.R;
import com.kevinlamcs.android.restaurando.ui.activity.AddActivity;
import com.kevinlamcs.android.restaurando.ui.activity.YelpViewActivity;
import com.kevinlamcs.android.restaurando.ui.adapter.SearchAdapter;
import com.kevinlamcs.android.restaurando.ui.model.Restaurant;
import com.kevinlamcs.android.restaurando.ui.model.Thought;
import com.kevinlamcs.android.restaurando.utils.LocationUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Fragment class for adding restaurants into the favorited list.
 */
public class AddFragment extends Fragment {

    public static final String EXTRA_MY_RESTAURANT = "com.kevinlamcs.android.restaurando.ui" +
            ".extra.MY_RESTAURANT";

    private Restaurant restaurant;
    private TableLayout tableLayout;

    /**
     * Constructs a new AddFragment.
     * @return AddFragment
     */
    public static AddFragment newInstance() {
        return new AddFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Get restaurant information which was a result from the Yelp search
        restaurant = getActivity().getIntent().getParcelableExtra(SearchAdapter.EXTRA_YELP_RESTAURANT);

        final View v = inflater.inflate(R.layout.fragment_add_restaurant, container, false);

        displayRestaurantImage(v);

        TextView name = (TextView) v.findViewById(R.id.fragment_add_restaurant_name);
        TextView rating = (TextView) v.findViewById(R.id.fragment_add_restaurant_rating);
        TextView reviews = (TextView) v.findViewById(R.id.fragment_add_restaurant_reviews);
        TextView street_address = (TextView) v.findViewById(R.id.fragment_add_restaurant_street_address);
        TextView city_state_zip = (TextView) v.findViewById(R.id.fragment_add_restaurant_city_state_zip);

        ButtonRectangle buttonDial = (ButtonRectangle) v.findViewById(
                R.id.fragment_add_restaurant_button_rectangle_dial);
        buttonDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialRestaurantNumber(restaurant.getDisplayPhone());
            }
        });

        ButtonRectangle buttonDirections = (ButtonRectangle) v.findViewById(
                R.id.fragment_add_restaurant_button_rectangle_directions);
        buttonDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google" +
                        ".com/maps?daddr=" + restaurant.getStreetAddress() + "," +
                        restaurant.getCityStateZip()));
                startActivity(intent);
            }
        });

        ButtonRectangle buttonYelp = (ButtonRectangle) v.findViewById(
                R.id.fragment_add_restaurant_button_rectangle_yelp);
        buttonYelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = YelpViewActivity.newIntent(getActivity(), restaurant.getUrl());
                startActivity(intent);
            }
        });

        ImageButton imageButtonAddThoughts = (ImageButton) v.findViewById(
                R.id.fragment_add_restaurant_image_button_add_thoughts);

        tableLayout = (TableLayout) v.findViewById(
                R.id.fragment_add_restaurant_bullet_list_thoughts);

        name.setText(restaurant.getName());
        rating.setText(restaurant.getRating());
        reviews.setText(getString(R.string.review_count, restaurant.getReviewCount()));
        street_address.setText(restaurant.getStreetAddress());
        city_state_zip.setText(restaurant.getCityStateZip());

        imageButtonAddThoughts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thought thought = new Thought(getContext(), tableLayout);
                thought.setupAddThought();
                thought.focusThought();
            }
        });
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_done_check:
                List<String> thoughtList = new ArrayList<>();
                Thought.retrieveThought(thoughtList, restaurant);

                Intent data = new Intent();
                data.putExtra(EXTRA_MY_RESTAURANT, restaurant);
                getActivity().setResult(Activity.RESULT_OK, data);
                getActivity().finish();
                break;
            case android.R.id.home:
                getActivity().finish();
            default:
                return false;
        }
        return true;
    }

    /**
     * Use Glide to retrieve the restaurant image from Yelp and display it.
     * @param view - The view to display the image
     */
    private void displayRestaurantImage(View view) {
        final FrameLayout progressBarCircular = (FrameLayout) getActivity().findViewById(
                R.id.activity_add_progress_bar_circular_download_image);
        final ImageView restaurantImage = (ImageView) view.findViewById(
                R.id.fragment_add_restaurant_image);
        LinearLayout placeHolder = (LinearLayout)view.findViewById(
                R.id.fragment_add_no_connectivity_place_holder);
        LinearLayout restaurantDetails = (LinearLayout)view.findViewById(
                R.id.fragment_add_restaurant_details);

        // If there is internet, retrieve the image. Otherwise display the placeholder.
        if (LocationUtils.isConnectedToInternet(getContext())) {
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
                            ((AddActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                            return false;
                        }
                    })
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(restaurantImage);
        } else {
            progressBarCircular.setVisibility(View.GONE);
            ((AddActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(restaurant.getCategory());
            placeHolder.setBackgroundColor(color);
            placeHolder.setVisibility(View.VISIBLE);

            restaurantImage.setVisibility(View.GONE);
            restaurantDetails.setLayoutParams(new TableLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));
        }
    }

    /**
     * Starts the implicit intent for dialing the restaurant's phone number.
     * @param phoneNumber - Phone number to dial
     */
    private void dialRestaurantNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
