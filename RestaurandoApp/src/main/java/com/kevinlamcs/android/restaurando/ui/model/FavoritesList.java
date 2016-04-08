package com.kevinlamcs.android.restaurando.ui.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class which represents the favorites list and provides functionality to manipulate its contents.
 */
public class FavoritesList<T extends Restaurant> extends ForwardingFavoritesList<T> implements Parcelable {

    private static final String SORT_CATEGORY = "Category";
    public static final String SORT_NAME = "Name";
    private static final String SORT_RATING = "Rating";

    private List<T> listSelectedRestaurants = new ArrayList<>();
    private List<T> listUnSelectedRestaurants = new ArrayList<>();
    private Map<String,Boolean> mapSelectedStates = new HashMap<>();
    private int selectedItemStartPosition;

    private Map<String, Integer> mapCategories = new HashMap<>();

    private String sortOrder;
    private Comparator<T> comparator;

    /**
     * Constructs a new FavoritesList and keeps it sorted initially by category.
     * @param list - list of restaurant objects
     */
    public FavoritesList(ArrayList<T> list) {
        super(list);
        setComparator(SORT_CATEGORY);
    }

    /**
     * Copy constructor.
     * @param c - previous FavoritesList
     */
    public FavoritesList(FavoritesList<T> c){
        super(c.getList());
        this.listSelectedRestaurants = c.listSelectedRestaurants;
        this.listUnSelectedRestaurants = c.listUnSelectedRestaurants;
        this.mapSelectedStates = c.mapSelectedStates;
        this.selectedItemStartPosition = c.selectedItemStartPosition;
        this.mapCategories = c.mapCategories;
        this.sortOrder = c.sortOrder;
        setComparator(sortOrder);
    }

    @Override
    public boolean add(T object) {
        if (this.contains(object)) {
            replaceThoughts(object);
            return false;
        }

        // Add new restaurant object as unselected. Sort it. Then insert the unselected list into
        // the FavoritesList followed by the selected list. Update selected states and categories.
        if (object != null) {
            listUnSelectedRestaurants.add(object);
        }

        Collections.sort(listUnSelectedRestaurants, comparator);
        updateFavoritesList();
        selectedItemStartPosition++;
        addSelectedState(object, false);
        addCategory(object);
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object object) {
        if (mapSelectedStates.get(((Restaurant)object).getId())) {
            listSelectedRestaurants.remove(object);
        } else {
            listUnSelectedRestaurants.remove(object);
            --selectedItemStartPosition;
        }

        Collections.sort(listSelectedRestaurants, comparator);
        updateFavoritesList();
        addSelectedState((T) object, false);
        removeCategory((T) object);
        return true;
    }

    @Override
    public boolean contains(Object object) {
        for (T restaurant : this) {
            if (restaurant.getId().equals(((Restaurant)object).getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Makes the restaurant selected.
     * @param object - restaurant that becomes selected
     */
    private void addSelected(T object) {
        listSelectedRestaurants.add(object);
        Collections.sort(listSelectedRestaurants, comparator);
    }

    /**
     * Makes all restaurants selected.
     * @param collections - all restaurants
     */
    private void addAllSelected(Collection<? extends T> collections) {
        listSelectedRestaurants.addAll(collections);
        Collections.sort(listSelectedRestaurants, comparator);
    }

    /**
     * Updates the restaurant's selected state.
     * @param object - restaurant object
     * @param state - selected state
     */
    private void addSelectedState(T object, boolean state) {
        mapSelectedStates.put(object.getId(), state);
    }

    /**
     * Updates all restaurants' selected states.
     * @param collections - all restaurants
     * @param state - selected state
     */
    private void addSelectedStates(Collection<? extends T> collections, boolean state) {
        for (T restaurant : collections) {
            addSelectedState(restaurant, state);
        }
    }

    /**
     * Adds the restaurant object's category to the list of all categories.
     * @param object - restaurant object
     */
    private void addCategory(T object) {
        String category = (object.getCategory());

        if (mapCategories.containsKey(category)) {
            int categoryNum = mapCategories.get(category);
            mapCategories.put(category, ++categoryNum);
        } else {
            mapCategories.put(category, 1);
        }
    }

    /**
     * Adds all restaurants' categories to the list of categories.
     */
    private void addAllCategories() {

        mapCategories.clear();
        for (T object : this) {
            addCategory(object);
        }
    }

    /**
     * Removes the restaurant's category from the list of categories.
     * @param object - restaurant object to be removed
     */
    private void removeCategory(T object) {
        String category = (object.getCategory());
        int categoryNum = mapCategories.get(category);
        if (categoryNum == 1) {
            mapCategories.remove(category);
        } else {
            mapCategories.put(category, --categoryNum);
        }
    }

    /**
     * Retrieves all the types of categories from the list of restaurants.
     * @param listCategories - list of all categories
     * @return Types of categories
     */
    public List<String> getAllCategories(List<T> listCategories) {
        for (T category : listCategories) {
            addCategory(category);
        }

        return getCategories();
    }

    /**
     * Gets all category types.
     * @return Types of categories
     */
    public List<String> getCategories() {
        return new ArrayList<>(mapCategories.keySet());
    }

    /**
     * Finds out if a category exists in the FavoritesList.
     * @param category - category
     * @return true if the category exists. False otherwise
     */
    public boolean hasCategory(String category) {
        return mapCategories.get(category) != null;
    }

    /**
     * Finds out whether there are any selected restaurants in the list.
     * @return true if there are selected restaurants. False otherwise
     */
    public boolean hasSelected() {
        return !listSelectedRestaurants.isEmpty();
    }

    /**
     * Sets the comparator when the user changes the sorting order. Able to sort by category,
     * name, and rating.
     * @param sortOrder - sorting order
     */
    public void setComparator(String sortOrder) {

        this.sortOrder = sortOrder;
        switch (sortOrder) {
            case SORT_CATEGORY:
                comparator = new Comparator<T>() {
                    @Override
                    public int compare(T restaurantOne, T restaurantTwo) {
                        String categoryOne = restaurantOne.getCategory();
                        String categoryTwo = restaurantTwo.getCategory();
                        int comparison = categoryOne.compareToIgnoreCase(categoryTwo);

                        String nameOne = restaurantOne.getName();
                        String nameTwo = restaurantTwo.getName();
                        if (comparison == 0) {
                            comparison = nameOne.compareToIgnoreCase(nameTwo);
                        }
                        return comparison;
                    }
                };
                break;
            case SORT_NAME:
                comparator = new Comparator<T>() {
                    @Override
                    public int compare(T restaurantOne, T restaurantTwo) {
                        String nameOne = restaurantOne.getName();
                        String nameTwo = restaurantTwo.getName();

                        return nameOne.compareToIgnoreCase(nameTwo);
                    }
                };
                break;
            case SORT_RATING:
                comparator = new Comparator<T>() {
                    @Override
                    public int compare(T restaurantOne, T restaurantTwo) {
                        String ratingOne = restaurantOne.getRating();
                        String ratingTwo = restaurantTwo.getRating();
                        int comparison = ratingOne.compareToIgnoreCase(ratingTwo);

                        String nameOne = restaurantOne.getName();
                        String nameTwo = restaurantTwo.getName();
                        if (comparison == 0) {
                            comparison = nameOne.compareToIgnoreCase(nameTwo);
                        }
                        return comparison;
                    }
                };
                break;
        }
    }

    /**
     * Retrieves the starting position of the first selected item in the FavoritesList.
     * @return starting position
     */
    public int getSelectedItemStartPosition() {
        return selectedItemStartPosition;
    }

    /**
     * Retrieves the selected restaurant at the specified position.
     * @param position - position in the selected restaurants list
     * @return selected restaurant
     */
    public T getSelected(int position) {
        return listSelectedRestaurants.get(position);
    }

    /**
     * Gets the number of selected restaurants
     * @return number of selected restaurants
     */
    public int getSelectedSize() {
        return listSelectedRestaurants.size();
    }

    /**
     * Retrieves the position of the restaurant object.
     * @param object - restaurant object
     * @return position
     */
    public int getPosition(T object) {
        for (int i=0; i < this.size(); i++) {
            if (this.get(i).getId().equals(object.getId())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the selected state of the restaurant object.
     * @param object - restaurant object
     * @return selected state
     */
    public boolean getSelectedState(T object) {
        return mapSelectedStates.get(object.getId());
    }

    /**
     * If previously unselected, marks the restaurant object at the index position as selected.
     * Otherwise, mark the object as unselected.
     * @param position - position of the restaurant object
     * @return restaurant object
     */
    public T onSelected(int position) {
        // Reverse the selected state (true -> false or false -> true)
        String restaurantId = this.get(position).getId();
        boolean postSelectedState = !mapSelectedStates.get(restaurantId);
        mapSelectedStates.put(restaurantId, postSelectedState);

        // If selected, remove from unselected list and put into selected list. Otherwise,
        // remove from selected list and put into unselected list.
        T restaurant;
        if (postSelectedState) {
            restaurant = listUnSelectedRestaurants.remove(position);
            addSelected(restaurant);
        } else {
            restaurant = listSelectedRestaurants.remove(position - listUnSelectedRestaurants.size());
            listUnSelectedRestaurants.add(restaurant);
            Collections.sort(listUnSelectedRestaurants, comparator);
        }
        selectedItemStartPosition = listUnSelectedRestaurants.size();
        updateFavoritesList();

        return restaurant;
    }

    /**
     * Replaces all the restaurant objects in the FavoritesList with new restaurant objects.
     * @param listNewFavorites - new restaurant objects
     * @param state - initial selected states for the objects
     */
    public void replaceAll(List<T> listNewFavorites, boolean state) {
        // Remove all items from favorites list and from selected list. Reset the selected states
        // to false.
        clearUnSelected();
        clearSelected();

        // If all items are to be selected, put them all in the selected list.
        if (state) {
            addAllSelected(listNewFavorites);
            selectedItemStartPosition = listUnSelectedRestaurants.size();
        } else {
            listUnSelectedRestaurants.addAll(listNewFavorites);
        }

        Collections.sort(listSelectedRestaurants, comparator);
        updateFavoritesList();

        addSelectedStates(listNewFavorites, state);
        addAllCategories();
    }

    /**
     * Replaces unselected restaurants with new restaurants.
     * @param listNewFavorites - new restaurant objects
     * @param state - initial selected state
     */
    public void replaceOld(List<T> listNewFavorites, boolean state) {

        clearUnSelected();

        List<T> listFavorites = removeDuplicateRestaurants(listNewFavorites,
                listSelectedRestaurants);

        if (state) {
            addAllSelected(listFavorites);
        } else {
            listUnSelectedRestaurants.addAll(listFavorites);
            addAll(listFavorites);
        }

        Collections.sort(listSelectedRestaurants, comparator);
        updateFavoritesList();
        selectedItemStartPosition = listUnSelectedRestaurants.size();
        addSelectedStates(listFavorites, state);
    }

    /**
     * Removes duplicates between the new unselected restaurants and the selected restaurants
     * @param filteredRestaurantList - new restaurant list
     * @param selectedRestaurantList - old selected restaurant list
     * @return Restaurant list with duplicates removed
     */
    private List<T> removeDuplicateRestaurants(List<T> filteredRestaurantList,
                                               List<T> selectedRestaurantList) {
        LinkedHashSet<T> comparisonHashSet = new LinkedHashSet<>(filteredRestaurantList);
        for (T restaurant : selectedRestaurantList) {
            if (comparisonHashSet.contains(restaurant)) {
                comparisonHashSet.remove(restaurant);
            }
        }

        return new ArrayList<>(comparisonHashSet);
    }

    /**
     * Makes all selected restaurants unselected.
     */
    private void clearSelected() {
        for (T restaurant : listSelectedRestaurants) {
            mapSelectedStates.put(restaurant.getId(), false);
        }
        listSelectedRestaurants.clear();
    }

    /**
     * Updates the thought list of the restaurant object.
     * @param object - restaurant object
     */
    private void replaceThoughts(Object object) {

        String thoughtList = ((Restaurant)object).getThoughtList();

        for (int i = 0; i < size(); i++) {
            if ((this.get(i)).getId().equals(((Restaurant)object).getId())) {
                this.get(i).setThoughtList(thoughtList);
            }
        }

        for (int j= 0; j < listUnSelectedRestaurants.size(); j++) {
            if ((listUnSelectedRestaurants.get(j)).getId().equals(((Restaurant) object).getId())) {
                listUnSelectedRestaurants.get(j).setThoughtList(thoughtList);
            }
        }

        for (int k = 0; k < listSelectedRestaurants.size(); k++) {
            if ((listSelectedRestaurants.get(k)).getId().equals(((Restaurant)object).getId())) {
                listSelectedRestaurants.get(k).setThoughtList(thoughtList);
            }
        }
    }

    /**
     * Clears all unselected restaurants.
     */
    private void clearUnSelected() {
        listUnSelectedRestaurants.clear();
    }

    /**
     * Clears the FavoritesList and reinserts unselected and selected list back in.
     */
    private void updateFavoritesList() {
        clear();
        addAll(listUnSelectedRestaurants);
        addAll(listSelectedRestaurants);
    }

    /**
     * Constructor for parcelable.
     * @param source - parcelable
     */
    private FavoritesList(Parcel source) {
        super(source);
        source.readTypedList(listSelectedRestaurants,  Restaurant.CREATOR);
        source.readTypedList(listUnSelectedRestaurants, Restaurant.CREATOR);
        selectedItemStartPosition = source.readInt();
        sortOrder = source.readString();

        List<String> listSelectedStateKeys = new ArrayList<>();
        List<String> listCategoriesKeys = new ArrayList<>();

        source.readStringList(listSelectedStateKeys);
        boolean[] boolArraySelectedStateValues = new boolean[listSelectedStateKeys.size()];
        source.readBooleanArray(boolArraySelectedStateValues);

        source.readStringList(listCategoriesKeys);
        int[] intArrayCategoryValues = new int[listCategoriesKeys.size()];
        source.readIntArray(intArrayCategoryValues);

        for (int i=0; i < listSelectedStateKeys.size(); i++) {
            mapSelectedStates.put(listSelectedStateKeys.get(i), boolArraySelectedStateValues[i]);
        }

        for (int j=0; j < listCategoriesKeys.size(); j++) {
            mapCategories.put(listCategoriesKeys.get(j), intArrayCategoryValues[j]);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(getList());
        dest.writeTypedList(listSelectedRestaurants);
        dest.writeTypedList(listUnSelectedRestaurants);
        dest.writeInt(selectedItemStartPosition);
        dest.writeString(sortOrder);

        Set<Map.Entry<String, Boolean>> setSelectedState = mapSelectedStates.entrySet();
        List<String> listSelectedStateKeys = new ArrayList<>();
        boolean[] boolArraySelectedStateValues = new boolean[setSelectedState.size()];
        int i=0;
        for(Map.Entry<String, Boolean> entry : setSelectedState) {
            listSelectedStateKeys.add(entry.getKey());
            boolArraySelectedStateValues[i++] = entry.getValue();
        }
        dest.writeStringList(listSelectedStateKeys);
        dest.writeBooleanArray(boolArraySelectedStateValues);

        Set<Map.Entry<String, Integer>> setCategories = mapCategories.entrySet();
        List<String> listCategoriesKeys = new ArrayList<>();
        int[] intArrayCategoriesValues = new int[setCategories.size()];
        int j=0;
        for(Map.Entry<String, Integer> entry : setCategories) {
            listCategoriesKeys.add(entry.getKey());
            intArrayCategoriesValues[j++] = entry.getValue();
        }
        dest.writeStringList(listCategoriesKeys);
        dest.writeIntArray(intArrayCategoriesValues);
    }

    /**
     * Making the class Parcelable.
     */
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new FavoritesList(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new FavoritesList[size];
        }
    };
}
