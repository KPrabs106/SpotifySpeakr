package com.kartikprabhu.spotifyspeakr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    HashMap<String, String> params = new HashMap<String, String>();
    private ImageButton playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        params.put("X-Parse-Application-Id", getResources().getString(R.string.parse_app_id));
        params.put("X-Parse-REST-API-Key", getResources().getString(R.string.api_key));
        params.put("Content-Type", "application/json");

        playButton = (ImageButton) findViewById(R.id.play_button);

        final EditText desiredTrack = (EditText) findViewById(R.id.editText);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params.put("trackuri", String.valueOf(desiredTrack.getText()));
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
        });

        Parse.initialize(this, getResources().getString(R.string.parse_app_id), getResources().getString(R.string.parse_client_key));
        ParseObject.registerSubclass(Queue.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
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
