package com.kevinlamcs.android.restaurando;

import com.kevinlamcs.android.restaurando.ui.model.FavoritesList;
import com.kevinlamcs.android.restaurando.ui.model.Restaurant;

import junit.framework.TestCase;

import org.junit.Test;

import java.lang.Exception;
import java.util.ArrayList;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class FavoritesListTest extends TestCase {
    @Test
    public final void testRun() throws Exception {
        FavoritesList<Restaurant> favoritesList = new FavoritesList<>(new ArrayList());
        Restaurant restaurant1 = new Restaurant();
        restaurant1.setName("The Whale");
        restaurant1.setCategory("Seafood");
        restaurant1.setRating("5.0");

        Restaurant restaurant2 = new Restaurant();
        restaurant2.setName("Soup Kitchen");
        restaurant2.setCategory("Soups");
        restaurant2.setRating("4.0");

        Restaurant restaurant3 = new Restaurant();
        restaurant3.setName("Cherry Pop");
        restaurant3.setCategory("Dessert");
        restaurant3.setRating("3.5");

        favoritesList.add(restaurant1);
        favoritesList.add(restaurant2);
        favoritesList.add(restaurant3);

        for (Restaurant restaurant : favoritesList) {
            System.out.println(restaurant.getCategory() + ", " + restaurant.getName());
        }

        System.out.println("----------------------------");

        favoritesList.setComparator(FavoritesList.SORT_NAME);

        for (Restaurant restaurant : favoritesList) {
            System.out.println(restaurant.getCategory() + ", " + restaurant.getName());
        }
    }
}