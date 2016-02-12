package com.kartikprabhu.spotifyspeakr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    RecyclerView results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        results = (RecyclerView) findViewById(R.id.results_recycler_view);
        results.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        results.setLayoutManager(layoutManager);

        EditText searchTextView = (EditText) findViewById(R.id.searchTextView);
        searchTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = String.valueOf(s);
                SpotifyRestClient.searchTrack(query, null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            initResults(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initResults(JSONObject response) throws JSONException {
        SearchResultAdapter adapter = new SearchResultAdapter(createList(response));
        results.setAdapter(adapter);
    }

    private List<SpotifyTrack> createList(JSONObject response) throws JSONException {
        List<SpotifyTrack> spotifyTracks = new ArrayList<>();

        int total = response.getJSONObject("tracks").getInt("total");
        int numItems = Math.min(total, 10);
        JSONArray tracks = response.getJSONObject("tracks").getJSONArray("items");

        for (int i = 0; i < numItems; i++) {
            String albumArtURL = tracks.getJSONObject(i).getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");
            String artist = tracks.getJSONObject(i).getJSONArray("artists").getJSONObject(0).getString("name");
            String name = tracks.getJSONObject(i).getString("name");
            String uri = tracks.getJSONObject(i).getString("id");
            spotifyTracks.add(new SpotifyTrack(name, artist, albumArtURL, uri));
        }

        return spotifyTracks;
    }

}
