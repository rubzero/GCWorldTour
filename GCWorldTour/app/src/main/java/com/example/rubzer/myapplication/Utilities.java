package com.example.rubzer.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.ArrayList;

import javax.annotation.Nullable;

import static android.content.Context.MODE_PRIVATE;

public class Utilities {

    public Utilities(){
    }

    public static boolean notConnected(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnected();
    }

    public static double[] getFormatedLatlong(String latlong){
        double[] formatedLatlong = new double[2];
        String[] parts = latlong.split(",");
        formatedLatlong[0] = Double.parseDouble(parts[0]);
        formatedLatlong[1] = Double.parseDouble(parts[1]);
        return formatedLatlong;
    }

    public static void hideShowPassword(EditText text, boolean b){
        if(b)
            text.setTransformationMethod
                (PasswordTransformationMethod.getInstance());
        else
            text.setTransformationMethod
                (HideReturnsTransformationMethod.getInstance());
        text.setSelection(text.length());
    }

    public static boolean isConnected(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnected();
    }

    public static void retrieveDataFromFirestore(final Context context, final Class activity) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);
        final CollectionReference artistReference = firestore.collection("artists");
        final CollectionReference eventReference = firestore.collection("events");
        final CollectionReference newReference = firestore.collection("news");

        final ArtistList artistList = new ArtistList();
        final EventList eventList = new EventList();
        final NewList newList = new NewList();

        artistReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Artist artist = document.toObject(Artist.class);
                        artistList.addArtist(artist);
                    }
                    writeArtistsInSharedPreferences(artistList, context);
                    eventReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Event event = document.toObject(Event.class);
                                    eventList.addEvent(event);
                                }
                                writeEventsInSharedPreferences(eventList, context);
                                newReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()){
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                New n = document.toObject(New.class);
                                                newList.addNew(n);
                                            }
                                            writeNewsInSharedPreferences(newList, context);
                                            if(activity!=null)
                                                context.startActivity(new Intent(context, activity));
                                            ((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    public static void hideSoftKeyboard(Context context, View view){
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    public static void setHintIconSelected(EditText editText
            , ArrayList<EditText> editTextList, ArrayList<Integer> iconIdList){
        for(int i=0; i<editTextList.size(); i++){
            editTextList.get(i).setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, iconIdList.get(i), 0);
            if(editText!=null && editText == editTextList.get(i)){
                editText.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, iconIdList.get(i+editTextList.size()), 0);
            }
        }
    }

    public static void eventRecyclerView(Context context
            , RecyclerView recyclerView, EventList eventList, String name, String genre
            , int linearLayoutManager, int itemId){

        LinearLayoutManager layoutManager = new LinearLayoutManager(context
                , linearLayoutManager, false);
        recyclerView.setLayoutManager(layoutManager);
        ArrayList<String> titles = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
        ArrayList<String> images = new ArrayList<>();
        Event event = null;
        for(int i=0; i<eventList.getEventList().size(); i++){
            event = eventList.getEventList().get(i);
            String id = event.getFirebaseId();
            if(genre != null){
                if(event.getArtist().getName().equals(name) || event.getGenre().equals(genre)){
                    titles.add(event.getName());
                    dates.add(event.getDate());
                    images.add(event.getPoster());
                }
            }
            else {
                titles.add(event.getName());
                dates.add(event.getDate());
                images.add(event.getPoster());
            }
        }
        RecyclerAdapter adapter = new RecyclerAdapter(context, images, titles, dates, itemId);
        recyclerView.setAdapter(adapter);
    }


    public static void getImageFromFirebaseStorage(Event event, Artist artist
            , final ImageView imageView){

        if(artist!=null){
            StorageReference mStorageRef = FirebaseStorage.getInstance()
                    .getReference()
                    .child("images/artists/"+artist.getFirebaseId()+".png");
            mStorageRef.getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(imageView.getContext()).load(uri).into(imageView);
                        }});
            return;
        }
        if(event!=null){
            StorageReference mStorageRef = FirebaseStorage.getInstance()
                    .getReference()
                    .child("images/events/"+event.getFirebaseId()+".png");
            mStorageRef.getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(imageView.getContext()).load(uri).into(imageView);
                        }});
        }
    }

    public static void writeArtistsInSharedPreferences(ArtistList artistList, Context context){
        SharedPreferences preferences = context.getSharedPreferences("artistList"
                , MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String jsonArtistList = gson.toJson(artistList);
        editor.putString("artistList", jsonArtistList);
        editor.apply();
    }

    public static void writeEventsInSharedPreferences(EventList eventList, Context context){
        SharedPreferences preferences = context.getSharedPreferences("eventList"
                , MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String jsonEventList = gson.toJson(eventList);
        editor.putString("eventList", jsonEventList);
        editor.apply();
    }

    public static void writeNewsInSharedPreferences(NewList newList, Context context){
        SharedPreferences preferences = context.getSharedPreferences("newList"
                , MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String jsonNewList = gson.toJson(newList);
        editor.putString("newList", jsonNewList);
        editor.apply();
    }

    public static void setUserEventList (final String email, final Event event
            , final Context context, final Class activity){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);

        final CollectionReference reference = firestore.collection("users");
        reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException e) {

                if(!queryDocumentSnapshots.isEmpty())
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        User user = snapshot.toObject(User.class);
                        if(user.getEmail().equals(email)){
                            ArrayList<String> eventList = user.getMyEventList();
                            if(eventList == null)
                                eventList = new ArrayList<>();
                            for(int i=0; i<eventList.size(); i++){
                                if(eventList.get(i).equals(event.getFirebaseId()))
                                    return;
                            }
                            eventList.add(event.getFirebaseId());
                            reference.document(user.getId()).update("myEventList",eventList);
                            context.startActivity(new Intent(context, activity));
                            ((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            return;
                        }
                    }
            }
        });
    }
}
