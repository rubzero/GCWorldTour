package com.example.rubzer.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SliderAdapter extends PagerAdapter {

    private Context context;

    private NewList newList;
    private ArtistList artistList;
    private EventList eventList;

    public SliderAdapter(Context context, NewList newList, ArtistList artistList, EventList eventList) {
        this.context = context;
        this.newList = newList;
        this.artistList = artistList;
        this.eventList = eventList;
    }

    @Override
    public int getCount() {
        return newList.getNewList().size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.item_slider, null);

        TextView textView = view.findViewById(R.id.txt_title_slider);
        final ImageView imageView = view.findViewById(R.id.img_new_slider);

        textView.setText(newList.getNewList().get(position).getTitle());

        StorageReference mStorageRef = FirebaseStorage.getInstance()
                .getReference()
                .child("images/news/"+newList.getNewList().get(position).getFirebaseId()+".png");
        mStorageRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(imageView.getContext()).load(uri).into(imageView);
                        ViewPager viewPager = (ViewPager) container;
                        viewPager.addView(view, 0);
                    }});
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(newList.getNewList().get(position).getType().equals("artist")){
                    intent = new Intent(context, ArtistActivity.class);
                    intent.putExtra("artistName", newList.getNewList().get(position).getName());
                }
                else {
                    intent = new Intent(context, EventActivity.class);
                    intent.putExtra("eventName", newList.getNewList().get(position).getName());
                }

                context.startActivity(intent);
                ((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}
