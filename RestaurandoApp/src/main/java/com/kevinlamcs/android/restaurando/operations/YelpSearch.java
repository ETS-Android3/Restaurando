package com.kevinlamcs.android.restaurando.operations;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kevinlamcs.android.restaurando.ui.fragment.SearchFragment;
import com.kevinlamcs.android.restaurando.ui.model.Restaurant;
import com.kevinlamcs.android.restaurando.ui.model.Yelp;
import com.kevinlamcs.android.restaurando.utils.StringManipulationUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Class used to retrieve Restaurant information from the Yelp server
 */
public class YelpSearch {

    private static final String API_KEY="tnJAgLqwI38mXi2vrpLinai0232hK3aaLDe71wIhQ44sGr7-0C2kxlI0E6qon8mYPejC2NmcBZU_WvaIeuJ_pQwmzly9MupbII4jB1GBrmiid-aklhMlviHQZaaYW3Yx";
    private static final String DEFAULT_CATEGORY="food,restaurants";
    private OkHttpClient client;
    /**
     * Construct a new search on yelp with a token for authorization onto the Yelp server.
     */
    public YelpSearch() {
        this.client = new OkHttpClient();
    }

    /**
     * Creates the query request for searching by location.
     * @param term - category, food, or restaurant to be searched
     * @param location - location where the term exist
     * @return jsonString response by Yelp
     */
    private String searchForBusinessesByLocation(String term, String location) {
        HttpUrl.Builder builder = new HttpUrl.Builder()
                .scheme("https")
                .host("api.yelp.com")
                .addPathSegment("v3")
                .addPathSegment("businesses")
                .addPathSegment("search");

        builder.addQueryParameter("categories", DEFAULT_CATEGORY);

        if (term != null) {
            builder.addQueryParameter("term", term);
        }

        if (SearchFragment.isSearchNearby && location != null) {
            String latitude = location.split(",")[0];
            String longitude = location.split(",")[1];
            builder.addQueryParameter("latitude", latitude);
            builder.addQueryParameter("longitude", longitude);
        } else {
            builder.addQueryParameter("location", location);
        }

        HttpUrl url = builder.build();
        return sendRequestForJSONStringResult(url);
    }

    /**
     * Creates the query request for searching by business.
     * @param businessId - id of the restaurant
     * @return jsonString response by Yelp
     */
    private String searchByBusinessId(String businessId) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("api.yelp.com")
                .addPathSegment("v3")
                .addPathSegment("businesses")
                .addPathSegment(businessId)
                .build();
        return sendRequestForJSONStringResult(url);
    }


    private String sendRequestForJSONStringResult(HttpUrl url) {
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + API_KEY)
                .url(url)
                .build();
        Response response = sendRequest(request);
        return retrieveResponseResult(response);
    }

    private Response sendRequest(Request request) {
        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            return null;
        }
    }

    private String retrieveResponseResult(Response response) {
        try {
            return response.body().string();
        } catch (Exception e) {
            return null;
        }
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
        if (searchResponseJson == null) return restaurantList;

        // Store the information temporarily in the yelp object
        Gson gson = new GsonBuilder().create();
        Yelp yelp = gson.fromJson(searchResponseJson, Yelp.class);

        if (yelp.businesses.size() == 0) {
            return restaurantList;
        }

        // Go through the yelp object and fill out the restaurant object fields
        for (Yelp.Business business : yelp.businesses) {
            Restaurant restaurant = new Restaurant();
            restaurant.setName(business.name);
            restaurant.setId(business.id);
            restaurant.setDisplayPhone(business.display_phone);
            restaurant.setStreetAddress(business.location.address1);
            restaurant.setCategory((business.categories.get(0).title));
            if (restaurant.getCategory() != null) {
                restaurant.setCategoryId(restaurant.getCategory().substring(0, 1));
            }
            restaurant.setCityStateZip(business.location.city + ", " + business.location.state_code
                    + " " + business.location.postal_code);
            restaurant.setRating(business.rating);
            restaurant.setReviewCount(business.review_count);

            restaurant.setUrl(business.url);
            restaurant.setImageUrl(business.image_url);

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
        System.out.println(searchResponseJson + "!!!!!!!!!!!!!!!!");
        Gson gson = new GsonBuilder().create();
        Yelp.Business business = gson.fromJson(searchResponseJson, Yelp.Business.class);

        Restaurant restaurant = new Restaurant();
        restaurant.setName(business.name);
        restaurant.setId(business.id);
        restaurant.setStreetAddress(business.location.address1);

        restaurant.setDisplayPhone(business.display_phone);

        restaurant.setCategory((business.categories.get(0).title));
        if (restaurant.getCategory() != null) {
            restaurant.setCategoryId(restaurant.getCategory().substring(0, 1));
        }
        restaurant.setCityStateZip(business.location.city + ", " + business.location.state_code
                + " " + business.location.postal_code);
        restaurant.setRating(business.rating);
        restaurant.setReviewCount(business.review_count);

        restaurant.setUrl(business.url);
        restaurant.setImageUrl(business.image_url);

        return restaurant;
    }
}
