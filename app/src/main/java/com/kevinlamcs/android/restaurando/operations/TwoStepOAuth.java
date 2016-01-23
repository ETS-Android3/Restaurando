package com.kevinlamcs.android.restaurando.operations;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;

/**
 * Created by kevin-lam on 1/9/16.
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
