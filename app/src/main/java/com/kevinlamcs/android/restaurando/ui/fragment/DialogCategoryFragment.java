package com.kevinlamcs.android.restaurando.ui.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kevinlamcs.android.restaurando.R;
import com.kevinlamcs.android.restaurando.ui.adapter.FilterAdapter;
import com.kevinlamcs.android.restaurando.ui.model.FilterOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by kevin-lam on 1/29/16.
 */
public class DialogCategoryFragment extends DialogFragment {

    public static final String EXTRA_FILTER_OPTIONS = "com.kevinlamcs.android.restaurando" +
            ".ui.fragment.DialogCategoryFragment.filterOptions";

    private static final String ARG_FILTER_OPTIONS = "arg filter options";

    private RecyclerView mRecyclerView;
    private FilterAdapter mAdapter;
    private Button mButtonDone;
    private Button mButtonCancel;

    private FilterOptions filterOptions;

    public static DialogCategoryFragment newInstance(FilterOptions filterOptions) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_FILTER_OPTIONS, filterOptions);
        
        DialogCategoryFragment fragment = new DialogCategoryFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_fragment_category, null);

        // Maybe a method for custom dialog
        getDialog().setTitle("Select All Categories");

        mRecyclerView = (RecyclerView)v.findViewById(R.id.dialog_fragment_category_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        filterOptions = getArguments().getParcelable(ARG_FILTER_OPTIONS);
        mAdapter = new FilterAdapter(filterOptions);
        mRecyclerView.setAdapter(mAdapter);

        mButtonDone = (Button)v.findViewById(R.id.dialog_fragment_category_button_done);
        mButtonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterOptions = mAdapter.getFilterOptions();
                sendResult(Activity.RESULT_OK, filterOptions);
                dismiss();
            }
        });

        mButtonCancel = (Button)v.findViewById(R.id.dialog_fragment_category_button_cancel);
        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return v;
    }

    public void sendResult(int resultCode, FilterOptions filterOptions) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_FILTER_OPTIONS, filterOptions);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
