package com.kevinlamcs.android.restaurando.ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.kevinlamcs.android.restaurando.R;
import com.kevinlamcs.android.restaurando.operations.YelpSearch;
import com.kevinlamcs.android.restaurando.ui.adapter.FavoritesAdapter;
import com.kevinlamcs.android.restaurando.ui.model.Restaurant;
import com.kevinlamcs.android.restaurando.ui.model.Thought;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Created by kevin-lam on 1/24/16.
 */
public class InfoFragment extends Fragment {
    private Restaurant mRestaurant;
    private TableLayout mTableLayout;
    private JSONArray mThoughtList = new JSONArray();

    public static InfoFragment newInstance() {
        InfoFragment fragment = new InfoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_restaurant, container, false);
        mRestaurant = getActivity().getIntent().getParcelableExtra(FavoritesAdapter
                .EXTRA_INFO_RESTAURANT);

        try {
            Restaurant restaurant = new BackgroundYelpSearchByBusiness().execute(mRestaurant.getId())
                    .get();
            restaurant.setThoughtList(mRestaurant.getThoughtList());
            mRestaurant = restaurant;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        TextView name = (TextView) v.findViewById(R.id.fragment_add_restaurant_name);
        TextView rating = (TextView) v.findViewById(R.id.fragment_add_restaurant_rating);
        TextView reviews = (TextView) v.findViewById(R.id.fragment_add_restaurant_reviews);
        TextView street_address = (TextView) v.findViewById(R.id.fragment_add_restaurant_street_address);
        TextView city_state_zip = (TextView) v.findViewById(R.id.fragment_add_restaurant_city_state_zip);
        ImageButton imageButtonAddThoughts = (ImageButton) v.findViewById(
                R.id.fragment_add_restaurant_image_button_add_thoughts);
        imageButtonAddThoughts.setVisibility(View.GONE);

        name.setText(mRestaurant.getName());
        rating.setText(mRestaurant.getRating());
        reviews.setText(getString(R.string.review_count, mRestaurant.getReviewCount()));
        street_address.setText(mRestaurant.getStreetAddress());
        city_state_zip.setText(mRestaurant.getCityStateZip());


        try {
            if (mRestaurant.getThoughtList() != null) {
                JSONObject json = new JSONObject(mRestaurant.getThoughtList());
                mThoughtList = json.getJSONArray(Thought.THOUGHT);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mTableLayout = (TableLayout) v.findViewById(
                R.id.fragment_add_restaurant_bulleted_list_thoughts);
        for (int index=0; index < mThoughtList.length(); index++) {
            Thought thought = new Thought(getContext(), mTableLayout);
            try {
                thought.setupDisplayThought(mThoughtList.getString(index));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        mTableLayout.removeAllViews();
    }

    public class BackgroundYelpSearchByBusiness extends AsyncTask<String, Void, Restaurant> {

        @Override
        protected Restaurant doInBackground(String... params) {
            return new YelpSearch().queryYelpByBusiness(params[0]);
        }

        @Override
        protected void onPostExecute(Restaurant restaurant) {
            new AddFragment.BackgroundDownloadImage(getActivity(),
                    (ImageView) getActivity().findViewById(R.id.fragment_add_restaurant_image))
                    .execute(mRestaurant.getImageUrl());
        }
    }
}
