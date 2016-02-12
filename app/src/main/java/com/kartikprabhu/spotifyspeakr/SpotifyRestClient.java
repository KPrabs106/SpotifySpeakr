package com.kartikprabhu.spotifyspeakr;

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
        asyncHttpClient.get(modifiedURL, requestParams, responseHandler);
    }

    public static void searchTrack(String query, RequestParams requestParams, AsyncHttpResponseHandler responseHandler) {

        String modifiedURL = BASE_URL + "search?q=" + query.replaceAll(" ", "%20") + "&type=track&market=US&limit=10";
        asyncHttpClient.get(modifiedURL, requestParams, responseHandler);
    }
}
