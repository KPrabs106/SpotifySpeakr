package com.kartikprabhu.spotifyspeakr;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Queue")
public class Queue extends ParseObject {
    public Queue() {

    }

    public String getTrackID() {
        return getString("trackID");
    }
}
