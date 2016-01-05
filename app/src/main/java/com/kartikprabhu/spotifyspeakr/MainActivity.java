package com.kartikprabhu.spotifyspeakr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.PlayConfig;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PlayerNotificationCallback, ConnectionStateCallback {

    private static final String CLIENT_ID = "4723a618582148fa80b22e70b2cac6bc";
    private static final String REDIRECT_URI = "spotifyspeakr://callback/";
    private static final int REQUEST_CODE = 1738;
    PlayConfig playConfig;
    private Player mPlayer;
    private ImageButton playButton;
    private ImageButton pauseButton;
    private ImageButton previousButton;
    private ImageButton nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        playButton = (ImageButton) findViewById(R.id.play_button);
        pauseButton = (ImageButton) findViewById(R.id.pause_button);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.resume();
                playButton.setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.VISIBLE);
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.pause();
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.INVISIBLE);
            }
        });

        previousButton = (ImageButton) findViewById(R.id.previous_button);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.skipToPrevious();
                playButton.setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.VISIBLE);
            }
        });

        nextButton = (ImageButton) findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.skipToNext();
                playButton.setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.VISIBLE);
            }
        });

        Parse.initialize(this, getResources().getString(R.string.parse_app_id), getResources().getString(R.string.parse_client_key));
        ParseObject.registerSubclass(Queue.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                    @Override
                    public void onInitialized(Player player) {
                        mPlayer = player;
                        mPlayer.addConnectionStateCallback(MainActivity.this);
                        mPlayer.addPlayerNotificationCallback(MainActivity.this);
                        startPlayingMusic();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }

    public void startPlayingMusic() {
        ParseQuery<Queue> query = ParseQuery.getQuery(Queue.class);
        query.findInBackground(new FindCallback<Queue>() {
            @Override
            public void done(List<Queue> objects, ParseException e) {
                if (e == null) {
                    Log.d("no error", objects.toString());
                    ArrayList<String> trackURIs = new ArrayList<>();
                    for (int i = 0; i < objects.size(); i++) {
                        trackURIs.add("spotify:track:" + objects.get(i).getTrackID());
                    }
                    Log.d("trackURIs", trackURIs.toString());
                    playConfig = PlayConfig.createFor(trackURIs);
                    playButton.setClickable(true);
                    pauseButton.setClickable(true);
                    previousButton.setClickable(true);
                    nextButton.setClickable(true);

                    mPlayer.play(playConfig);
                    mPlayer.pause();
                } else
                    Log.e("error", e.getMessage());
            }
        });
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable throwable) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String s) {
        Log.d("MainActivity", "Received connection message: " + s);
    }


    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.d("MainActivity", "Playback event received: " + eventType.name());
        switch (eventType) {
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String s) {
        Log.d("MainActivity", "Playback error received: " + errorType.name());
        switch (errorType) {
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
