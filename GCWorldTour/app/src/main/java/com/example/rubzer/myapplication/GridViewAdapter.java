package com.example.rubzer.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class GridViewAdapter extends BaseAdapter{
    Context context;
    ArrayList<Event> events;

    public GridViewAdapter(Context context, ArrayList<Event> events) {
        this.context = context;
        this.events = events;
        compareDates();
    }

    private void compareDates(){
        Collections.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event e1, Event e2) {
                return e2.getDate().compareTo(e1.getDate());
            }
        });
    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int i) {
        return events.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null)
        {
            view= LayoutInflater.from(context).inflate(R.layout.grid_view_card,viewGroup,false);
        }

        final Event event= (Event) this.getItem(i);

        ImageView img = view.findViewById(R.id.img_carditem_card);
        TextView txt =  view.findViewById(R.id.txt_date_card);

        txt.setText(event.getDate());
        Glide.with(context)
                .asBitmap()
                .load(events.get(i).getPoster())
                .into(img);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EventActivity.class);
                intent.putExtra("eventName", event.getName());
                context.startActivity(intent);
                ((Activity)context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                popup.getMenuInflater().inflate(R.menu.grid_popup, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        events.remove(event);
                        notifyDataSetChanged();
                        return false;
                    }
                });
                popup.show();
                return false;
            }
        });

        return view;
    }
}