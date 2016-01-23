package com.kevinlamcs.android.restaurando.ui.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.kevinlamcs.android.restaurando.R;
import com.kevinlamcs.android.restaurando.ui.activity.FavoritesActivity;
import com.kevinlamcs.android.restaurando.ui.adapter.SearchAdapter;
import com.kevinlamcs.android.restaurando.ui.model.Restaurant;
import com.kevinlamcs.android.restaurando.ui.model.Thought;
import com.kevinlamcs.android.restaurando.utils.BitmapUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;


/**
 * Created by kevin-lam on 1/16/16.
 */
public class AddFragment extends Fragment {

    public static final String EXTRA_MY_RESTAURANT = "com.kevinlamcs.android.restaurando.ui" +
            ".fragment.my_restaurant";

    private Restaurant mRestaurant;
    private TableLayout mTableLayout;

    private Bitmap mScaledBitmap;

    public static AddFragment newInstance() {
        AddFragment fragment = new AddFragment();
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
        mRestaurant = getActivity().getIntent().getParcelableExtra(SearchAdapter
                .EXTRA_YELP_RESTAURANT);

        new BackgroundDownloadImage((ImageView)v.findViewById(R.id.fragment_add_restaurant_image)
        ).execute(mRestaurant.getImageUrl());

        TextView name = (TextView) v.findViewById(R.id.fragment_add_restaurant_name);
        TextView rating = (TextView) v.findViewById(R.id.fragment_add_restaurant_rating);
        TextView reviews = (TextView) v.findViewById(R.id.fragment_add_restaurant_reviews);
        TextView street_address = (TextView) v.findViewById(R.id.fragment_add_restaurant_street_address);
        TextView city_state_zip = (TextView) v.findViewById(R.id.fragment_add_restaurant_city_state_zip);
        ImageButton imageButtonAddThoughts = (ImageButton) v.findViewById(
                R.id.fragment_add_restaurant_image_button_add_thoughts);

        mTableLayout = (TableLayout) v.findViewById(
                R.id.fragment_add_restaurant_bulleted_list_thoughts);

        name.setText(mRestaurant.getName());
        rating.setText(mRestaurant.getRating());
        reviews.setText(getString(R.string.review_count, mRestaurant.getReviewCount()));
        street_address.setText(mRestaurant.getStreetAddress());
        city_state_zip.setText(mRestaurant.getCityStateZip());

        imageButtonAddThoughts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thought thought = new Thought(getContext(), mTableLayout);
                thought.focusThought(getContext());
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
                Thought.retrieveThought(thoughtList);
                mRestaurant.setThoughtsList(thoughtList);

                Intent data = new Intent();
                data.putExtra(EXTRA_MY_RESTAURANT, mRestaurant);
                getActivity().setResult(Activity.RESULT_OK, data);
                getActivity().finish();
                break;
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        mScaledBitmap.recycle();
        mScaledBitmap = null;
    }

    private class BackgroundDownloadImage extends AsyncTask<String, Void, Bitmap> {
        private ImageView mImageView;

        public BackgroundDownloadImage(ImageView imageView) {
            mImageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            try {
                InputStream in = new java.net.URL(url).openStream();
                Bitmap defaultBitmap = BitmapFactory.decodeStream(in);
                mScaledBitmap = BitmapUtils.scaleBitmap(defaultBitmap, getActivity());
                defaultBitmap.recycle();
                defaultBitmap = null;
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
            return mScaledBitmap;
        }

        protected void onPostExecute(Bitmap bitmap) {
            mImageView.setImageBitmap(bitmap);
        }
    }
}
