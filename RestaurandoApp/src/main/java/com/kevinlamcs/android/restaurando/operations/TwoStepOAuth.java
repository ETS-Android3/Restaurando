package com.kevinlamcs.android.restaurando.operations;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;

/**
 * Class used for authorization into the Yelp server
 */
public class TwoStepOAuth extends DefaultApi10a{
    @Override
    public String getAccessTokenEndpoint() {
        return null;
    }

    @Override
    public String getAuthorizationUrl(Token requestToken) {
        return null;
    }

    @Override
    public String getRequestTokenEndpoint() {
        return null;
    }
}
