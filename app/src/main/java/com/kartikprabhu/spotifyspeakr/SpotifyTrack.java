package com.kartikprabhu.spotifyspeakr;

/**
 * Created by prabh on 2/11/2016.
 */
public class SpotifyTrack {

    private String trackName;
    private String trackArtist;
    private String albumArtURL;
    private String trackURI;

    public SpotifyTrack(String trackName, String trackArtist, String albumArtURL, String trackURI) {
        this.trackName = trackName;
        this.trackArtist = trackArtist;
        this.albumArtURL = albumArtURL;
        this.trackURI = trackURI;
    }

    public String getTrackName() {
        return trackName;
    }

    public String getTrackArtist() {
        return trackArtist;
    }

    public String getAlbumArtURL() {
        return albumArtURL;
    }

    public String getTrackURI() {
        return trackURI;
    }
}
