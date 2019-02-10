package com.example.rubzer.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements View.OnClickListener {

    private ArrayList<String> images;
    private ArrayList<String> titles;
    private ArrayList<String> dates;
    private Context context;
    private int itemId ;

    public RecyclerAdapter(Context context, ArrayList<String> images
            , ArrayList<String> titles, ArrayList<String> dates, int itemId){
        this.context = context;
        this.images = images;
        this.titles = titles;
        this.dates = dates;
        this.itemId = itemId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(itemId, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        Glide.with(context)
                .asBitmap()
                .load(images.get(i))
                .into(viewHolder.imgImage);
        viewHolder.txtTitle.setText(titles.get(i));
        viewHolder.txtDate.setText(dates.get(i));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EventActivity.class);
                intent.putExtra("eventName",titles.get(i));
                context.startActivity(intent);
                ((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    @Override
    public void onClick(View v) {
        images.get(getItemCount());
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgImage;
        TextView txtTitle, txtDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgImage = itemView.findViewById(R.id.img_image_recycler);
            txtTitle = itemView.findViewById(R.id.txt_title_recycler);
            txtDate = itemView.findViewById(R.id.txt_date_recycler);
        }
    }
}
