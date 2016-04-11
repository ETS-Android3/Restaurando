package com.kevinlamcs.android.restaurando.ui.fragment;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;

import com.kevinlamcs.android.restaurando.R;
import com.kevinlamcs.android.restaurando.ui.activity.FavoritesActivity;
import com.kevinlamcs.android.restaurando.utils.DecoderUtils;
import com.kevinlamcs.android.restaurando.vending.IabHelper;
import com.kevinlamcs.android.restaurando.vending.IabResult;
import com.kevinlamcs.android.restaurando.vending.Purchase;

/**
 * Fragment class for displaying the donation dialog.
 */
public class DonateDialogFragment extends DialogFragment {

    private static final int DONATION_DIALOG_WIDTH = 960;

    private static final String SKU_DONATE_ONE = "donation_001";
    private static final String SKU_DONATE_TWO = "donation_002";
    private static final String SKU_DONATE_THREE = "donation_003";
    private static final String SKU_DONATE_FOUR = "donation_004";
    private static final String SKU_DONATE_FIVE = "donation_005";
    private static final String SKU_DONATE_TEN = "donation_010";
    private static final String SKU_DONATE_FIFTEEN = "donation_015";
    private static final String SKU_DONATE_TWENTY = "donation_020";
    private static final String SKU_DONATE_FIFTY = "donation_050";

    private enum Donations {
        DONATE_ONE, DONATE_TWO, DONATE_THREE, DONATE_FOUR,
        DONATE_FIVE, DONATE_TEN, DONATE_FIFTEEN, DONATE_TWENTY,
        DONATE_FIFTY
    }

    private IabHelper iabHelper;

    private AlertDialog alertDialog;

    private String donationChoice;

    /**
     * Constructs a new DonateDialogFragment and stores the list of donation values.
     * @param items - donation values that the user is allowed to donate
     * @return DonateDialogFragment
     */
    public static DonateDialogFragment newInstance(CharSequence[] items) {
        DonateDialogFragment dialogFragmentDonate = new DonateDialogFragment();

        Bundle args = new Bundle();
        args.putCharSequenceArray("items", items);
        dialogFragmentDonate.setArguments(args);
        return dialogFragmentDonate;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FavoritesActivity activity = ((FavoritesActivity)getActivity());
        if (activity != null) {
            iabHelper = activity.getIabHelper();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        CharSequence[] items = getArguments().getCharSequenceArray("items");

        LayoutInflater inflater = getActivity().getLayoutInflater();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity())
                .setCustomTitle(inflater.inflate(R.layout.dialog_donate_title, null, false))
                .setPositiveButton("DONATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        iabHelper.launchPurchaseFlow(getActivity(), donationChoice,
                                FavoritesFragment.REQUEST_DONATE, new IabHelper.OnIabPurchaseFinishedListener() {
                                    @Override
                                    public void onIabPurchaseFinished(IabResult result, Purchase info) {
                                        if (result.isFailure()) {
                                            Log.e("Purchase failed", "Error purchasing: " + result);
                                            return;
                                        }

                                        if (info.getSku().equals(SKU_DONATE_ONE)
                                                || info.getSku().equals(SKU_DONATE_TWO)
                                                || info.getSku().equals(SKU_DONATE_THREE)
                                                || info.getSku().equals(SKU_DONATE_FOUR)
                                                || info.getSku().equals(SKU_DONATE_FIVE)
                                                || info.getSku().equals(SKU_DONATE_TEN)
                                                || info.getSku().equals(SKU_DONATE_FIFTEEN)
                                                || info.getSku().equals(SKU_DONATE_TWENTY)
                                                || info.getSku().equals(SKU_DONATE_FIFTY)) {
                                            iabHelper.consumeAsync(info, new IabHelper.OnConsumeFinishedListener() {
                                                @Override
                                                public void onConsumeFinished(Purchase purchase, IabResult result) {
                                                    if (iabHelper == null) {
                                                        return;
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }, "");
                    }
                })
                .setNegativeButton("CANCEL", null)
                .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);

                        Donations donations = Donations.values()[which];
                        switch (donations) {
                            case DONATE_ONE:
                                donationChoice = SKU_DONATE_ONE;
                                break;
                            case DONATE_TWO:
                                donationChoice = SKU_DONATE_TWO;
                                break;
                            case DONATE_THREE:
                                donationChoice = SKU_DONATE_THREE;
                                break;
                            case DONATE_FOUR:
                                donationChoice = SKU_DONATE_FOUR;
                                break;
                            case DONATE_FIVE:
                                donationChoice = SKU_DONATE_FIVE;
                                break;
                            case DONATE_TEN:
                                donationChoice = SKU_DONATE_TEN;
                                break;
                            case DONATE_FIFTEEN:
                                donationChoice = SKU_DONATE_FIFTEEN;
                                break;
                            case DONATE_TWENTY:
                                donationChoice = SKU_DONATE_TWENTY;
                                break;
                            case DONATE_FIFTY:
                                donationChoice = SKU_DONATE_FIFTY;
                                break;
                        }
                    }
                });
        alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setLayout(DONATION_DIALOG_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.show();

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        positiveButton.setTextColor(ContextCompat.getColorStateList(getContext(), R.drawable.donate_button));
        positiveButton.setEnabled(false);
        negativeButton.setTextColor(ContextCompat.getColorStateList(getContext(), R.drawable.donate_button));
        return alertDialog;
    }
}
