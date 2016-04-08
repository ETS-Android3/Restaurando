package com.kevinlamcs.android.restaurando.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kevinlamcs.android.restaurando.R;

/**
 * Fragment class for displaying Yelp's webpage in a WebView
 */
public class YelpViewFragment extends Fragment {

    private static final String ARG_URL = "arg YelpView url";

    private String url;
    private WebView webView;

    /**
     * Constructs a new YelpViewFragment with the url to access the webpage.
     * @param url - Yelp webpage for the restaurant
     * @return YelpViewFragment
     */
    public static YelpViewFragment newInstance(String url) {
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);

        YelpViewFragment fragment = new YelpViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getArguments().getString(ARG_URL);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
                             savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_yelpview, container, false);
        webView = (WebView) v.findViewById(R.id.fragment_yelpview_web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.requestFocus(View.FOCUS_DOWN);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView webView, String title) {
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                activity.getSupportActionBar().setSubtitle("Yelp");
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!url.startsWith("http")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        webView.loadUrl(url);
        return v;
    }

    /**
     * Allows going back in the WebView.
     * @return true if you can go back. False otherwise
     */
    public boolean onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            return true;
        } else {
            return false;
        }
    }
}
