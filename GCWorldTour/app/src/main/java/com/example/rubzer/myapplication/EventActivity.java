package com.example.rubzer.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

public class EventActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    private ScrollView svMain;
    private RelativeLayout rlHeader, rlArtist;
    private RecyclerView recyclerView;
    private Artist artist;
    private EventList eventList;
    private Event event;
    private TextView txtTitle, txtDescriptionMenu, txtArtistMenu, txtLocationMenu
            , txtArtistEvent, txtSecondTitleEvent, txtGenreEvent, txtDate, txtLocation, txtPrice
            , txtNameArtist, txtGenreArtist, txtDescriptionArtist, txtLocationTitle, txtMore;
    private Button btnAddEvent;

    private ImageView imgPoster, imgArtist;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        getEventsFromPreferences();

        if(getIntent().hasExtra("eventName")){
            String name = getIntent().getStringExtra("eventName");
            event = eventList.getEventByName(name);
            artist = event.getArtist();
        }


        else if(this.getIntent()!=null){
            event = (Event) this.getIntent()
                    .getExtras().getSerializable("event");
            artist = (Artist) this.getIntent()
                    .getExtras().getSerializable("artist");
        }

        imagesDeclare();
        variableDeclare();
        createRecyclerView();
        createYoutubeFragment();
        createMap();
    }

    private void variableDeclare(){
        eventTextDeclare();
        menuTextDeclare();
        artistTextDeclare();
        titlesTextDeclare();
        buttonDeclare();

        svMain = findViewById(R.id.sv_main_event);
        rlHeader = findViewById(R.id.rl_header_event);
        rlArtist = findViewById(R.id.rl_artist_event);
        recyclerView = findViewById(R.id.rv_related_events_event);
    }

    private void buttonDeclare(){
        btnAddEvent = findViewById(R.id.btn_addToList_event);
        btnAddEvent.setOnClickListener(this);
    }

    private void imagesDeclare(){
        imgPoster = findViewById(R.id.img_poster_event);
        imgArtist = findViewById(R.id.img_artist_event);
        Utilities.getImageFromFirebaseStorage(event, null, imgPoster);
        Utilities.getImageFromFirebaseStorage(null, artist, imgArtist);
    }

    private void createYoutubeFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction
                = fragmentManager.beginTransaction();
        YoutubeFragment youtubeFragment = new YoutubeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("event",event);
        youtubeFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fl_youtube_event, youtubeFragment);
        fragmentTransaction.commit();
    }

    private void createMap(){
        if(mMap==null) {
            SupportMapFragment mapFrag = (MapWrapper) getSupportFragmentManager()
                    .findFragmentById(R.id.map_location_event);

            mapFrag.getMapAsync(this);
            ((MapWrapper) getSupportFragmentManager().findFragmentById(R.id.map_location_event))
                    .setListener(new MapWrapper.OnTouchListener() {
                        @Override
                        public void onTouch() {
                            svMain.requestDisallowInterceptTouchEvent(true);
                        }
                    });
        }
    }

    private void createRecyclerView(){
        Utilities.eventRecyclerView(this, recyclerView, eventList
                , null, event.getGenre(), LinearLayoutManager.HORIZONTAL
                , R.layout.recycler_view_item);
    }

    private void getEventsFromPreferences(){
        Gson gson = new Gson();
        SharedPreferences eventPreferences = getSharedPreferences("eventList"
                , MODE_PRIVATE);
        String json = eventPreferences.getString("eventList",null);
        eventList = gson.fromJson(json, EventList.class);
    }

    private void titlesTextDeclare(){
        txtLocationTitle = findViewById(R.id.txt_location_title_event);
    }

    private void eventTextDeclare(){
        txtTitle = findViewById(R.id.txt_title_event);

        txtSecondTitleEvent = findViewById(R.id.txt_second_title_event);
        txtArtistEvent = findViewById(R.id.txt_artist_event);
        txtGenreEvent = findViewById(R.id.txt_genre_event);
        txtDate = findViewById(R.id.txt_date_event);
        txtLocation = findViewById(R.id.txt_localion_event);
        txtPrice = findViewById(R.id.txt_price_event);

        txtTitle.setText(event.getName());
        txtSecondTitleEvent.setText(event.getName());
        txtArtistEvent.setText(artist.getName());
        txtGenreEvent.setText(event.getGenre());
        txtDate.setText(event.getDate());
        txtLocation.setText(event.getLocation());
    }

    private void menuTextDeclare(){
        txtDescriptionMenu = findViewById(R.id.txt_description_menu_event);
        txtArtistMenu = findViewById(R.id.txt_artist_menu_event);
        txtLocationMenu = findViewById(R.id.txt_location_menu_event);

        txtDescriptionMenu.setText(getString(R.string.description));
        txtArtistMenu.setText(getString(R.string.artist));
        txtLocationMenu.setText(getString(R.string.location));

        txtDescriptionMenu.setOnClickListener(this);
        txtArtistMenu.setOnClickListener(this);
        txtLocationMenu.setOnClickListener(this);
    }

    private void artistTextDeclare(){
        txtNameArtist = findViewById(R.id.txt_artist_name_event);
        txtGenreArtist = findViewById(R.id.txt_artist_genre_event);
        txtDescriptionArtist = findViewById(R.id.txt_artist_description_event);
        txtMore = findViewById(R.id.txt_artist_more_event);

        txtNameArtist.setText(artist.getName());
        txtGenreArtist.setText(artist.getGenre());
        txtDescriptionArtist.setText(artist.getDescription());
        txtMore.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        txtDescriptionMenu = findViewById(R.id.txt_description_menu_event);
        txtArtistMenu = findViewById(R.id.txt_artist_menu_event);
        txtLocationMenu = findViewById(R.id.txt_location_menu_event);
        switch(v.getId()){
            case R.id.txt_description_menu_event:
                svMain.smoothScrollTo(0 , rlHeader.getTop());
                break;
            case R.id.txt_artist_menu_event:
                svMain.smoothScrollTo(0 , rlArtist.getTop());
                break;
            case R.id.txt_location_menu_event:
                svMain.smoothScrollTo(0 , txtLocationTitle.getTop());
                break;
            case R.id.txt_artist_more_event:
                Intent intent = new Intent(EventActivity.this, ArtistActivity.class);
                intent.putExtra("artistName", artist.getName());
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.btn_addToList_event:
                Utilities.setUserEventList(FirebaseAuth.getInstance().getCurrentUser().getEmail()
                        , event, this, HomeActivity.class);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        double[] formatedLatlong = Utilities.getFormatedLatlong(event.getLatlong());
        LatLng latlong = new LatLng(formatedLatlong[0], formatedLatlong[1]);
        Marker marker = mMap.addMarker(new MarkerOptions().position(latlong).title(event.getLocation()));
        marker.showInfoWindow();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(formatedLatlong[0], formatedLatlong[1]), 12.0f));
    }
}
