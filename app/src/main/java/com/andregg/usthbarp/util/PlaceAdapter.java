package com.andregg.usthbarp.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andregg.usthbarp.R;

import java.util.ArrayList;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>{

    ArrayList<Place> places;
    SelectListener listener;

    public PlaceAdapter( ArrayList<Place> places, SelectListener l) {
        this.places = places;
        this.listener= l;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View v  = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
        return new PlaceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        Place p= places.get(position);
        holder.bindPlace(p);
        holder.showModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnShowModelClicked(p);
            }
        });
        holder.showLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnShowLocationClicked(p);
            }
        });
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    class PlaceViewHolder extends RecyclerView.ViewHolder {

        ImageView place_img;
        TextView place_title;
        RelativeLayout parentLayout;
        LinearLayout showModel, showLocation;
        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            place_img = itemView.findViewById(R.id.place_img);
            place_title = itemView.findViewById(R.id.place_title);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            showModel = itemView.findViewById(R.id.show_model_layout);
            showLocation = itemView.findViewById(R.id.show_location_layout);
        }
        void bindPlace(final Place place){
            place_img.setImageResource(place.image);
            place_title.setText(place.name);
            parentLayout.getBackground().setLevel(place.bgColor);
        }
    }

    public ArrayList<Place> getPlaces() {
        return places;
    }

    public void setPlaces(ArrayList<Place> places) {
        this.places = places;
    }
}
