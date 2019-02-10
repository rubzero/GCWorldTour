package com.example.rubzer.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

public class YoutubeFragment extends Fragment implements YouTubePlayer.OnInitializedListener{

    private Artist artist;
    private Event event;
    private YouTubePlayerSupportFragment mYoutubePlayerFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            artist = (Artist) getArguments().getSerializable("artist");
            event = (Event) getArguments().getSerializable("event");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentYoutubeView = inflater.inflate(R.layout.fragment_youtube, container, false);
        mYoutubePlayerFragment = new YouTubePlayerSupportFragment();
        mYoutubePlayerFragment.initialize(getResources().getString(R.string.youtubeApiKey), this);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_youtube, mYoutubePlayerFragment);
        fragmentTransaction.commit();
        return fragmentYoutubeView;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider
            , YouTubePlayer youTubePlayer, boolean b) {
        if(!b && artist != null){
            youTubePlayer.loadVideos(artist.getYoutubeVideos());
        }
        //cambiar por events
        else if(!b && event != null){
            youTubePlayer.loadVideo(event.getVideo());
        }
        youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider
            , YouTubeInitializationResult youTubeInitializationResult) {

    }
}
