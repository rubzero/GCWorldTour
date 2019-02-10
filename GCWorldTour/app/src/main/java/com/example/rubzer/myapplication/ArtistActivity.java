package com.example.rubzer.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;

public class ArtistActivity extends AppCompatActivity implements View.OnClickListener{

    private ScrollView svMain;
    private ArtistList artistList;
    private EventList eventList;
    private ImageView imgArtist;
    private RecyclerView recyclerView;
    private TextView txtName, txtGenre, txtProverb, txtDescription, txtDescription2
            , txtEvents, txtSocial;

    private Artist artist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        getArtistsAndEventsFromPreferences();

        if(getIntent().hasExtra("artistName")){
            String name = getIntent().getStringExtra("artistName");
            artist = artistList.getArtistByName(name);
        }

        else if(this.getIntent()!=null)
            artist = (Artist) this.getIntent()
                    .getExtras().getSerializable("artist");

        variableDeclare();
        createTextMenu();
        createSocialButtons();

        createYoutubeFragment();
        Utilities.getImageFromFirebaseStorage(null, artist, imgArtist);

        setDescriptionTextBetweenViews();
        setTextOnViews();
        createRecyclerView();
    }

    private void createRecyclerView(){
        Utilities.eventRecyclerView(this, recyclerView, eventList
                , artist.getName(), artist.getGenre(), LinearLayoutManager.HORIZONTAL
                , R.layout.recycler_view_item);
    }

    private void createYoutubeFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction
                = fragmentManager.beginTransaction();
        YoutubeFragment youtubeFragment = new YoutubeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("artist",artist);
        youtubeFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.pv_youtube_artist, youtubeFragment);
        fragmentTransaction.commit();
    }

    private void setDescriptionTextBetweenViews(){
        txtDescription.setText(artist.getDescription());
        txtDescription.post(new Runnable() {
            @Override
            public void run() {
                int count = txtDescription.getLineCount();
                int maxLines = 10;
                String desc1 = "";
                String desc2 = "";
                for (int i = 0; i < count; i++) {
                    int start = txtDescription.getLayout().getLineStart(i);
                    int end = txtDescription.getLayout().getLineEnd(i);
                    CharSequence substring = txtDescription.getText().subSequence(start, end);
                    if(i < maxLines){
                        desc1+=substring.toString();
                    }
                    else desc2+=substring.toString();
                }
                txtDescription.setText(desc1);
                txtDescription2.setText(desc2);
            }
        });
    }

    private void setTextOnViews(){
        txtName.setText(artist.getName()+":");
        txtGenre.setText(artist.getGenre());
        txtProverb.setText(artist.getProverb());
    }

    private void variableDeclare(){
        artistList = new ArtistList();
        svMain = findViewById(R.id.sv_main_artist);
        recyclerView = findViewById(R.id.rv_events_artist);
        imgArtist = findViewById(R.id.img_image_artist);;
        txtDescription = findViewById(R.id.txt_description_artist);
        txtDescription2 = findViewById(R.id.txt_description2_artist);
        txtName = findViewById(R.id.txt_name_artist);
        txtGenre = findViewById(R.id.txt_genre_artist);
        txtProverb = findViewById(R.id.txt_proverb_artist);
        txtEvents = findViewById(R.id.txt_nextevents_artist);
        txtSocial = findViewById(R.id.txt_social_artist);
    }

    private void createSocialButtons(){
        Button btnYoutube = findViewById(R.id.btn_youtube_artist);
        Button btnInstagram = findViewById(R.id.btn_instagram_artist);
        Button btnTwitter = findViewById(R.id.btn_twitter_artist);
        Button btnFacebook = findViewById(R.id.btn_facebook_artist);
        btnYoutube.setOnClickListener(this);
        btnInstagram.setOnClickListener(this);
        btnTwitter.setOnClickListener(this);
        btnFacebook.setOnClickListener(this);
    }

    private void createTextMenu(){
        TextView txtDescriptionMenu = findViewById(R.id.txt_desc_menu_artist);
        TextView txtEventsMenu = findViewById(R.id.txt_events_menu_artist);
        TextView txtSocialMenu = findViewById(R.id.txt_social_menu_artist);
        txtDescriptionMenu.setText(R.string.description);
        txtEventsMenu.setText(R.string.next_events);
        txtSocialMenu.setText(R.string.social);
        txtDescriptionMenu.setOnClickListener(this);
        txtEventsMenu.setOnClickListener(this);
        txtSocialMenu.setOnClickListener(this);
    }

    private void getArtistsAndEventsFromPreferences(){
        Gson gson = new Gson();
        SharedPreferences artistPreferences = getSharedPreferences("artistList"
                , MODE_PRIVATE);
        SharedPreferences eventPreferences = getSharedPreferences("eventList"
                , MODE_PRIVATE);
        String json = artistPreferences.getString("artistList",null);
        artistList = gson.fromJson(json, ArtistList.class);

        json = eventPreferences.getString("eventList",null);
        eventList = gson.fromJson(json, EventList.class);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.txt_desc_menu_artist:
                svMain.smoothScrollTo(0 , txtProverb.getTop());
                break;
            case R.id.txt_events_menu_artist:
                svMain.smoothScrollTo(0 , txtEvents.getTop());
                break;
            case R.id.txt_social_menu_artist:
                svMain.smoothScrollTo(0 , txtSocial.getTop());
                break;
            case R.id.btn_youtube_artist:
                startActivity(new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse(artist.getSocial().get("youtube"))));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.btn_instagram_artist:
                startActivity(new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse(artist.getSocial().get("instagram"))));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.btn_twitter_artist:
                startActivity(new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse(artist.getSocial().get("twitter"))));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.btn_facebook_artist:
                startActivity(new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse(artist.getSocial().get("facebook"))));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
        }
    }
}
