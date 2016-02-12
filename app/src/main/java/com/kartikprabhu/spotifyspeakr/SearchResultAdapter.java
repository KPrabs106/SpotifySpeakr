package com.kartikprabhu.spotifyspeakr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by prabh on 2/11/2016.
 */
public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ResultViewHolder> {
    private List<SpotifyTrack> spotifyTracks;

    public SearchResultAdapter(List<SpotifyTrack> spotifyTracks) {
        this.spotifyTracks = spotifyTracks;
    }

    public int getItemCount() {
        return spotifyTracks.size();
    }

    @Override
    public void onBindViewHolder(final ResultViewHolder holder, int position) {
        final SpotifyTrack spotifyTrack = spotifyTracks.get(position);
        holder.trackName.setText(spotifyTrack.getTrackName());
        holder.trackArtist.setText(spotifyTrack.getTrackArtist());
        Picasso.with(holder.context).load(spotifyTrack.getAlbumArtURL()).into(holder.albumArt);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("trackURI", spotifyTrack.getTrackURI());
                ((Activity) holder.context).setResult(1, intent);
                ((Activity) holder.context).finish();
            }
        });
    }

    @Override
    public ResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.results_layout, parent, false);
        return new ResultViewHolder(itemView);
    }

    public class ResultViewHolder extends RecyclerView.ViewHolder {
        ImageView albumArt;
        TextView trackName;
        TextView trackArtist;
        Context context;

        public ResultViewHolder(View itemView) {
            super(itemView);
            albumArt = (ImageView) itemView.findViewById(R.id.albumArt);
            trackName = (TextView) itemView.findViewById(R.id.trackName);
            trackArtist = (TextView) itemView.findViewById(R.id.trackArtist);
            context = itemView.getContext();

        }
    }
}
