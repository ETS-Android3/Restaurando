package com.kevinlamcs.android.restaurando.operations;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kevinlamcs.android.restaurando.ui.fragment.SearchFragment;
import com.kevinlamcs.android.restaurando.ui.model.Restaurant;
import com.kevinlamcs.android.restaurando.ui.model.Yelp;
import com.kevinlamcs.android.restaurando.utils.StringManipulationUtils;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by kevin-lam on 1/9/16.
 */
public class YelpSearch {

    private static final String TAG="YelpSearch";

    private static final String API_HOST="api.yelp.com";
    private static final String DEFAULT_TERM="Restaurant";
    private static final String DEFAULT_CATEGORY="food,restaurants";
    private static final String SEARCH_PATH="/v2/search";

    private static final String CONSUMER_KEY="VMnPW80yap0Soj7331tpXA";
    private static final String CONSUMER_SECRET="QWs9PA2QuG8t_foYHUMW8FJywAg";
    private static final String TOKEN="u3RArQ5DSBDxVdNMF8aWfVdzD234UpX1";
    private static final String TOKEN_SECRET="FZ3EQG07rvI-gYOZyqLN7LfzuFc";

    OAuthService mService;
    Token mAccessToken;

    public YelpSearch() {
        mService = new ServiceBuilder().provider(TwoStepOAuth.class).apiKey(CONSUMER_KEY)
                .apiSecret(CONSUMER_SECRET).build();
        mAccessToken = new Token(TOKEN, TOKEN_SECRET);
    }

    public String searchForBusinessesByLocation(String term, String location, String category) {
        OAuthRequest request = createOAuthRequest(SEARCH_PATH);

        if (term != null) {
            request.addQuerystringParameter("term", term);
        }

        if (SearchFragment.isSearchNearby) {
            request.addQuerystringParameter("ll", location);
        } else {
            request.addQuerystringParameter("location", location);
        }

        request.addQuerystringParameter("category_filter", category);
        return sendRequestAndGetResponse(request);
    }

    public OAuthRequest createOAuthRequest(String path) {
        OAuthRequest request = new OAuthRequest(Verb.GET, "http://" + API_HOST + path);
        return request;
    }

    private String sendRequestAndGetResponse(OAuthRequest request) {
        Log.i(TAG, "Querying " + request.getCompleteUrl() + " ...");
        mService.signRequest(mAccessToken, request);
        Log.i(TAG, "Request " + mService.toString() + " ...");Response response = request.send();
        return response.getBody();
    }

    public List<Restaurant> queryYelp(String term, String location) {
        List<Restaurant> restaurantList = new ArrayList<>();

        String searchResponseJson = searchForBusinessesByLocation(term, location, DEFAULT_CATEGORY);
        Log.i(TAG, "Retrieved from Yelp json string: " + searchResponseJson);

        Gson gson = new GsonBuilder().create();
        Yelp yelp = gson.fromJson(searchResponseJson, Yelp.class);

        for (Yelp.Businesses business : yelp.businesses) {
            Restaurant restaurant = new Restaurant();
            restaurant.setName(business.name);
            if (business.location.address.size() != 0) {
                restaurant.setStreetAddress(business.location.address.get(0));
            }
            restaurant.setCityStateZip(business.location.city + ", " + business.location.state_code
                    + " " + business.location.postal_code);
            restaurant.setRating(business.rating);
            restaurant.setReviewCount(business.review_count);
            String largeImage = StringManipulationUtils.replaceImageUrlSize
                    (business.image_url);
            restaurant.setImageUrl(largeImage);
            Log.i(TAG, "This is large image url " + largeImage);
            restaurantList.add(restaurant);
        }

        return restaurantList;
    }
}
