package com.kevinlamcs.android.restaurando.ui.fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.kevinlamcs.android.restaurando.R;
import com.kevinlamcs.android.restaurando.ui.adapter.FilterAdapter;
import com.kevinlamcs.android.restaurando.ui.model.FilterOptions;

/**
 * Fragment class to display the dialog which allows the user to select categories to filter.
 */
public class CategoryDialogFragment extends DialogFragment {

    public static final String EXTRA_FILTER_SELECTION = "com.kevinlamcs.android.restaurando.ui" +
            ".extra.FILTER_SELECTION";

    private static final String ARG_FILTER_OPTIONS = "argument filter options";

    private FilterAdapter adapter;

    private FilterOptions filterOptions;

    /**
     * Constructs a new CategoryDialogFragment and stores the user's filter selection.
     * @param filterOptions - User's filter selection
     * @return CategoryDialogFragment
     */
    public static CategoryDialogFragment newInstance(FilterOptions filterOptions) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_FILTER_OPTIONS, filterOptions);
        
        CategoryDialogFragment fragment = new CategoryDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder  builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(
                R.layout.dialog_fragment_category, null);

        filterOptions = getArguments().getParcelable(ARG_FILTER_OPTIONS);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(
                R.id.dialog_fragment_category_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new FilterAdapter(filterOptions);
        recyclerView.setAdapter(adapter);

        Button buttonDone = (Button) view.findViewById(R.id.dialog_fragment_category_button_done);
        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterOptions = adapter.getFilterOptions();
                sendResult(Activity.RESULT_OK, filterOptions);
                dismiss();
            }
        });

        Button buttonCancel = (Button) view.findViewById(R.id.dialog_fragment_category_button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        builder.setView(view);
        return builder.create();
    }

    /**
     * Sends the user's selected categories back to FilterFragment.
     * @param resultCode - Used to identify this result
     * @param filterOptions - User's filter selection
     */
    private void sendResult(int resultCode, FilterOptions filterOptions) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_FILTER_SELECTION, filterOptions);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
