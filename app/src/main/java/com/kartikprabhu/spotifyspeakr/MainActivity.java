package com.kartikprabhu.spotifyspeakr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePushBroadcastReceiver;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements PlayerNotificationCallback, ConnectionStateCallback {

    private static final String CLIENT_ID = "4723a618582148fa80b22e70b2cac6bc";
    private static final String REDIRECT_URI = "spotifyspeakr://callback/";
    private static final int SPOTIFY_REQUEST_CODE = 1738;

    private static final int PICKED_REQUEST_CODE = 1234;

    static MainActivity mainActivity;

    HashMap<String, String> params = new HashMap<>();

    String offset;

    Player mPlayer;
    Handler handler;
    private ImageView albumArt;
    private TextView trackName;
    private TextView trackArtist;
    private Button startButton;
    private ImageButton playButton;
    private ImageButton pauseButton;
    private ImageButton previousButton;
    private ImageButton nextButton;

    public static MainActivity getInstance() {
        return mainActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        handler = new Handler();

        mainActivity = this;

        params.put("X-Parse-Application-Id", getResources().getString(R.string.parse_app_id));
        params.put("X-Parse-REST-API-Key", getResources().getString(R.string.api_key));
        params.put("Content-Type", "application/json");

        albumArt = (ImageView) findViewById(R.id.albumArt);
        trackName = (TextView) findViewById(R.id.trackName);
        trackArtist = (TextView) findViewById(R.id.trackArtist);

        playButton = (ImageButton) findViewById(R.id.play_button);
        pauseButton = (ImageButton) findViewById(R.id.pause_button);
        previousButton = (ImageButton) findViewById(R.id.previous_button);
        nextButton = (ImageButton) findViewById(R.id.next_button);

        findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), SearchActivity.class), PICKED_REQUEST_CODE);
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseCloud.callFunctionInBackground("resumeMusic", params, new FunctionCallback<Object>() {
                    @Override
                    public void done(Object object, ParseException e) {
                        Toast.makeText(getApplicationContext(), "Your music will resume within 10 seconds.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseCloud.callFunctionInBackground("pauseMusic", params, new FunctionCallback<Object>() {
                    @Override
                    public void done(Object object, ParseException e) {
                        Toast.makeText(getApplicationContext(), "Your music will pause within 10 seconds.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseCloud.callFunctionInBackground("previousTrack", params, new FunctionCallback<Object>() {
                    @Override
                    public void done(Object object, ParseException e) {
                        Toast.makeText(getApplicationContext(), "The previous track will start playing within 10 seconds.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseCloud.callFunctionInBackground("nextTrack", params, new FunctionCallback<Object>() {
                    @Override
                    public void done(Object object, ParseException e) {
                        Toast.makeText(getApplicationContext(), "The next track will start playing within 10 seconds.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Parse.initialize(this, getResources().getString(R.string.parse_app_id), getResources().getString(R.string.parse_client_key));
        ParseObject.registerSubclass(Queue.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();

        ParseCloud.callFunctionInBackground("getTime", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object object, ParseException e) {
                Log.d("server time", String.valueOf(object));
                Log.d("system time", String.valueOf(System.currentTimeMillis()));
                offset = String.valueOf(Long.parseLong(String.valueOf(object)) - System.currentTimeMillis());
            }
        });

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, SPOTIFY_REQUEST_CODE, request);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPOTIFY_REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                    @Override
                    public void onInitialized(Player player) {
                        mPlayer = player;
                        mPlayer.addConnectionStateCallback(MainActivity.this);
                        mPlayer.addPlayerNotificationCallback(MainActivity.this);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        } else if (requestCode == PICKED_REQUEST_CODE) {
            String pickedTrackURI = data.getStringExtra("trackURI");
            params.put("trackuri", pickedTrackURI);
            ParseCloud.callFunctionInBackground("playMusic", params, new FunctionCallback<Object>() {
                @Override
                public void done(Object object, ParseException e) {
                    if (e != null) {
                        Log.e("Server error", e.getMessage());
                    } else {
                        Toast.makeText(getApplicationContext(), "Your track will start playing in less than 10 seconds", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void startPlayingMusic(String time, final String trackURI) throws InterruptedException {
        long timeToWait = Long.parseLong(time) - Long.parseLong(offset) - System.currentTimeMillis();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPlayer.play("spotify:track:" + trackURI);
                playButton.setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.VISIBLE);
            }
        }, timeToWait);
    }

    private void resumeMusic(String time) throws InterruptedException {
        long timeToWait = Long.parseLong(time) - Long.parseLong(offset) - System.currentTimeMillis();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPlayer.resume();
                playButton.setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.VISIBLE);
            }
        }, timeToWait);
    }

    private void pauseMusic(String time) throws InterruptedException {
        long timeToWait = Long.parseLong(time) - Long.parseLong(offset) - System.currentTimeMillis();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPlayer.pause();
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.INVISIBLE);
            }
        }, timeToWait);
    }

    private void previousTrack(String time) throws InterruptedException {
        long timeToWait = Long.parseLong(time) - Long.parseLong(offset) - System.currentTimeMillis();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPlayer.skipToPrevious();
                playButton.setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.VISIBLE);
            }
        }, timeToWait);
    }

    private void nextTrack(String time) throws InterruptedException {
        long timeToWait = Long.parseLong(time) - Long.parseLong(offset) - System.currentTimeMillis();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPlayer.skipToNext();
                playButton.setVisibility(View.INVISIBLE);
                pauseButton.setVisibility(View.VISIBLE);
            }
        }, timeToWait);

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
    public void onPlaybackEvent(PlayerNotificationCallback.EventType eventType, PlayerState playerState) {
        Log.d("MainActivity", "Playback event received: " + eventType.name());
        if (eventType.equals(EventType.TRACK_CHANGED)) {
            SpotifyRestClient.getTrackDetails(playerState.trackUri.substring(14), null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        String pictureURL = response.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");
                        trackName.setText(response.getString("name"));
                        trackArtist.setText(response.getJSONArray("artists").getJSONObject(0).getString("name"));
                        Picasso.with(getApplicationContext()).load(pictureURL).into(albumArt);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void onPlaybackError(PlayerNotificationCallback.ErrorType errorType, String s) {
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

    public static class CustomParseReceiver extends ParsePushBroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Log.d("Received push", intent.getExtras().getString("com.parse.Data"));
                JSONObject jsonObject = new JSONObject(intent.getExtras().getString("com.parse.Data"));
                String time = jsonObject.getString("time");
                String action = jsonObject.getString("action");

                switch (action) {
                    case "play":
                        MainActivity.getInstance().startPlayingMusic(time, jsonObject.getString("trackURI"));
                        break;
                    case "pause":
                        MainActivity.getInstance().pauseMusic(time);
                        break;
                    case "resume":
                        MainActivity.getInstance().resumeMusic(time);
                        break;
                    case "next":
                        MainActivity.getInstance().nextTrack(time);
                        break;
                    case "previous":
                        MainActivity.getInstance().previousTrack(time);
                        break;
                }

            } catch (JSONException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
