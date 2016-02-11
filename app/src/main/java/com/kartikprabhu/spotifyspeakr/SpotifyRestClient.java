package com.kartikprabhu.spotifyspeakr;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by prabh on 2/11/2016.
 */
public class SpotifyRestClient {

    private static final String BASE_URL = "https://api.spotify.com/v1/";
    private static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    public static void getTrackDetails(String trackURI, RequestParams requestParams, AsyncHttpResponseHandler responseHandler) {

        String modifiedURL = BASE_URL + "tracks/" + trackURI;
        Log.e("get request", modifiedURL);
        asyncHttpClient.get(modifiedURL, requestParams, responseHandler);
    }
}
