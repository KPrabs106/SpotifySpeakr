package com.kartikprabhu.spotifyspeakr;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by prabh on 1/5/2016.
 */
public class CustomParseReceiver extends ParsePushBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            JSONObject jsonObject = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            Log.e("json", jsonObject.toString());
            String time = jsonObject.getString("time");
            String trackURI = jsonObject.getString("trackURI");
            Log.e("trackURIRecevier", trackURI);
            Intent musicIntent = new Intent(context, SpotifyMusicPlayer.class);
            musicIntent.putExtra("trackURI", trackURI);
            musicIntent.putExtra("time", time);
            musicIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(musicIntent);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
