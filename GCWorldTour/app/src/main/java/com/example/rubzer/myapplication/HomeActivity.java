package com.example.rubzer.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Nullable;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager viewPager;
    private TabLayout indicator;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build();
    private CollectionReference reference;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private Toolbar toolbar;

    private ArtistList artistList;
    private EventList eventList;
    private NewList newList;

    private ArrayList<Event> myEventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.tb_main);
        setSupportActionBar(toolbar);

        drawerLayout();
        getDataFromPreferences();
        setCarouselConfig();
        createRecyclerViewNextEvents();
        createGridView();

        controlTab();
    }

    @Override
    protected void onStart() {
        createGridView();
        super.onStart();
    }

    private void createGridView(){
        GridView gridView =  findViewById(R.id.gv_mylist_home);
        GridViewAdapter gridViewAdapter = new GridViewAdapter(this, myEventList);
        gridView.setAdapter(gridViewAdapter);
        gridViewAdapter.notifyDataSetChanged();
    }

    private void renewMyListEvent(List<String> list){
        myEventList = new ArrayList<>();
        if(list != null){
            for(String eventId : list){
                for(Event event: eventList.getEventList())
                    if(event.getFirebaseId().equals(eventId))
                        myEventList.add(event);
            }
        }
    }

    private void controlTab(){
        TabHost tabs= findViewById(android.R.id.tabhost);
        tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                createGridView();
            }
        });
        tabs.setup();
        tabs.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);

        TabHost.TabSpec spec=tabs.newTabSpec("tabDesired");
        spec.setContent(R.id.tab1_home);
        spec.setIndicator(getResources().getString(R.string.near));
        tabs.addTab(spec);

        spec=tabs.newTabSpec("tabNextEvents");
        spec.setContent(R.id.tab2_home);
        spec.setIndicator(getResources().getString(R.string.myList));
        tabs.addTab(spec);

        tabs.setCurrentTab(0);
    }

    private void createRecyclerViewNextEvents(){
        RecyclerView recyclerView = findViewById(R.id.rv_events_home);
        Utilities.eventRecyclerView(this, recyclerView, eventList
                , null, null, LinearLayoutManager.VERTICAL
                , R.layout.recycler_view_item_vertical);
    }

    private void setCarouselConfig(){
        viewPager=findViewById(R.id.viewPager);
        indicator=findViewById(R.id.indicator);

        viewPager.setAdapter(new SliderAdapter(this, newList, artistList, eventList));
        indicator.setupWithViewPager(viewPager, true);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 4000, 6000);
    }

    private void getDataFromPreferences(){
        Gson gson = new Gson();
        SharedPreferences artistPreferences = getSharedPreferences("artistList"
                , MODE_PRIVATE);
        SharedPreferences eventPreferences = getSharedPreferences("eventList"
                , MODE_PRIVATE);
        SharedPreferences newPreferences = getSharedPreferences("newList"
                , MODE_PRIVATE);

        String json = artistPreferences.getString("artistList",null);
        artistList = gson.fromJson(json, ArtistList.class);

        json = eventPreferences.getString("eventList",null);
        eventList = gson.fromJson(json, EventList.class);

        json = newPreferences.getString("newList",null);
        newList = gson.fromJson(json, NewList.class);
    }

    private void drawerLayout(){
        DrawerLayout drawer = findViewById(R.id.dl_home);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        createDrawerUserHolders();
    }

    private void createDrawerUserHolders(){
        NavigationView navigationView = findViewById(R.id.nv_main);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        TextView txtUser = headerView.findViewById(R.id.txt_user_drawer);
        TextView txtEmail = headerView.findViewById(R.id.txt_email_drawer);
        ImageView imgUser = headerView.findViewById(R.id.iv_user_drawer);

        setUserData(txtUser, txtEmail, imgUser);
    }

    private void setUserData(TextView txtUser, TextView txtEmail, ImageView image){
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            txtEmail.setText(email);
            getUserDataFirestore(txtUser,image, email);
        }
        else txtUser.setText(getString(R.string.guest));
    }

    private void getUserDataFirestore(final TextView name, final ImageView image, final String email){
        firestore.setFirestoreSettings(settings);
        reference = firestore.collection("users");
        reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException e) {
                if(!queryDocumentSnapshots.isEmpty())
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        User user = snapshot.toObject(User.class);
                        if(user.getEmail().equals(email)){
                            renewMyListEvent(user.getMyEventList());
                            name.setText(user.getName());
                            Glide.with(HomeActivity.this)
                                    .asBitmap()
                                    .load(user.getPhoto())
                                    .into(image);
                            
                        }
                    }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.dl_home);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_exit:
                mAuth.signOut();
                startActivity(new Intent(HomeActivity.this
                        , LoginActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
        }

        DrawerLayout drawer = findViewById(R.id.dl_home);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class SliderTimer extends TimerTask {

        @Override
        public void run() {
            HomeActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewPager.getCurrentItem() < newList.getNewList().size() - 1) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    } else {
                        viewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }
}


