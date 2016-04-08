package com.kevinlamcs.android.restaurando.ui.model;

import android.os.Parcel;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by kevin-lam on 3/12/16.
 */
public class ForwardingFavoritesList<T> implements List<T> {

    private final List<T> list;
    
    public ForwardingFavoritesList(List<T> list) {
        this.list = list;
    }

    public ForwardingFavoritesList(Parcel in) {
        list = new ArrayList<>();
        in.readTypedList(list, Restaurant.CREATOR);
    }

    @Override
    public void add(int location, T object) {
        list.add(location, object);
    }

    @Override
    public boolean add(T object) {
        return list.add(object);
    }

    @Override
    public boolean addAll(int location, Collection<? extends T> collection) {
        return list.addAll(location, collection);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        return list.addAll(collection);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean contains(Object object) {
        return list.contains(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return list.containsAll(collection);
    }

    @Override
    public T get(int location) {
        return list.get(location);
    }

    @Override
    public int indexOf(Object object) {
        return list.indexOf(object);
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public int lastIndexOf(Object object) {
        return list.lastIndexOf(object);
    }

    @Override
    public ListIterator<T> listIterator() {
        return list.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator(int location) {
        return list.listIterator(location);
    }

    @Override
    public T remove(int location) {
        return list.remove(location);
    }

    @Override
    public boolean remove(Object object) {
        return list.remove(object);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return list.removeAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return list.retainAll(collection);
    }

    @Override
    public T set(int location, T object) {
        return list.set(location, object);
    }

    @Override
    public int size() {
        return list.size();
    }

    @NonNull
    @Override
    public List<T> subList(int start, int end) {
        return list.subList(start, end);
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @NonNull
    @Override
    public <T1> T1[] toArray(T1[] array) {
        return list.toArray(array);
    }

    public List<T> getList() {
        return list;
    }
}
