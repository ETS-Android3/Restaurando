package com.kevinlamcs.android.restaurando.operations;


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
 * Class used to retrieve Restaurant information from the Yelp server
 */
public class YelpSearch {

    private static final String API_HOST="api.yelp.com";
    private static final String DEFAULT_CATEGORY="food,restaurants";
    private static final String SEARCH_PATH="/v2/search";
    private static final String BUSINESS_PATH="/v2/business";

    private static final String CONSUMER_KEY="VMnPW80yap0Soj7331tpXA";
    private static final String CONSUMER_SECRET="QWs9PA2QuG8t_foYHUMW8FJywAg";
    private static final String TOKEN="u3RArQ5DSBDxVdNMF8aWfVdzD234UpX1";
    private static final String TOKEN_SECRET="FZ3EQG07rvI-gYOZyqLN7LfzuFc";

    private final OAuthService service;
    private final Token accessToken;

    /**
     * Construct a new search on yelp with a token for authorization onto the Yelp server.
     */
    public YelpSearch() {
        service = new ServiceBuilder()
                .provider(TwoStepOAuth.class)
                .apiKey(CONSUMER_KEY)
                .apiSecret(CONSUMER_SECRET)
                .build();
        accessToken = new Token(TOKEN, TOKEN_SECRET);
    }

    /**
     * Creates the query request for searching by location.
     * @param term - category, food, or restaurant to be searched
     * @param location - location where the term exist
     * @return jsonString response by Yelp
     */
    private String searchForBusinessesByLocation(String term, String location) {
        OAuthRequest request = createOAuthRequest(SEARCH_PATH);

        if (term != null) {
            request.addQuerystringParameter("term", term);
        }

        if (SearchFragment.isSearchNearby) {
            request.addQuerystringParameter("ll", location);
        } else {
            request.addQuerystringParameter("location", location);
        }

        request.addQuerystringParameter("category_filter", DEFAULT_CATEGORY);
        return sendRequestAndGetResponse(request);
    }

    /**
     * Creates the query request for searching by business.
     * @param businessId - id of the restaurant
     * @return jsonString response by Yelp
     */
    private String searchByBusinessId(String businessId) {
        OAuthRequest request = createOAuthRequest(BUSINESS_PATH + "/" + businessId);
        return sendRequestAndGetResponse(request);
    }

    /**
     * Initializes the query request.
     * @param path - Url address to access Yelps' server
     * @return OAuthRequest
     */
    private OAuthRequest createOAuthRequest(String path) {
        return new OAuthRequest(Verb.GET, "http://" + API_HOST + path);
    }

    /**
     * Sends the request over to Yelp.
     * @param request - The request to send
     * @return response from Yelp
     */
    private String sendRequestAndGetResponse(OAuthRequest request) {
        service.signRequest(accessToken, request);
        Response response = request.send();
        return response.getBody();
    }

    /**
     * Retrieve json response from Yelps' Search API and deserialize the data into a
     * restaurant object.
     * @param term - term to search for
     * @param location - location in which the term exists
     * @return List of restaurants
     */
    public List<Restaurant> queryYelpByLocation(String term, String location) {
        List<Restaurant> restaurantList = new ArrayList<>();

        // Query Yelps' server for restaurant information
        String searchResponseJson = searchForBusinessesByLocation(term, location);

        // Store the information temporarily in the yelp object
        Gson gson = new GsonBuilder().create();
        Yelp yelp = gson.fromJson(searchResponseJson, Yelp.class);

        if (yelp.total == 0) {
            return restaurantList;
        }

        // Go through the yelp object and fill out the restaurant object fields
        for (Yelp.Businesses business : yelp.businesses) {
            Restaurant restaurant = new Restaurant();
            restaurant.setName(business.name);
            restaurant.setId(business.id);
            
            restaurant.setDisplayPhone(business.display_phone);

            if (business.location.address.size() != 0) {
                restaurant.setStreetAddress(business.location.address.get(0));
            }
            restaurant.setCategory((business.categories.get(0).get(0)));
            if (restaurant.getCategory() != null) {
                restaurant.setCategoryId(restaurant.getCategory().substring(0, 1));
            }
            restaurant.setCityStateZip(business.location.city + ", " + business.location.state_code
                    + " " + business.location.postal_code);
            restaurant.setRating(business.rating);
            restaurant.setReviewCount(business.review_count);

            restaurant.setUrl(business.mobile_url);

            String largeImage = StringManipulationUtils.replaceImageUrlSize
                    (business.image_url);
            restaurant.setImageUrl(largeImage);
            restaurantList.add(restaurant);
        }

        return restaurantList;
    }

    /**
     * Retrieve json response from Yelps' Business API and deserialize the data into a restaurant
     * object.
     * @param businessId - id of the restaurant to search for
     * @return Restaurant object
     */
    public Restaurant queryYelpByBusiness(String businessId) {
        String searchResponseJson = searchByBusinessId(businessId);
        Gson gson = new GsonBuilder().create();
        Yelp.Businesses business = gson.fromJson(searchResponseJson, Yelp.Businesses.class);

        Restaurant restaurant = new Restaurant();
        restaurant.setName(business.name);
        restaurant.setId(business.id);
        if (business.location.address.size() != 0) {
            restaurant.setStreetAddress(business.location.address.get(0));
        }
        restaurant.setDisplayPhone(business.display_phone);

        restaurant.setCategory((business.categories.get(0).get(0)));
        if (restaurant.getCategory() != null) {
            restaurant.setCategoryId(restaurant.getCategory().substring(0, 1));
        }
        restaurant.setCityStateZip(business.location.city + ", " + business.location.state_code
                + " " + business.location.postal_code);
        restaurant.setRating(business.rating);
        restaurant.setReviewCount(business.review_count);

        restaurant.setUrl(business.mobile_url);

        String largeImage = StringManipulationUtils.replaceImageUrlSize
                (business.image_url);
        restaurant.setImageUrl(largeImage);


        return restaurant;
    }
}
