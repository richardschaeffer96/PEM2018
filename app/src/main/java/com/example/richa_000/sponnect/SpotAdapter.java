package com.example.richa_000.sponnect;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SpotAdapter extends RecyclerView.Adapter<SpotAdapter.ViewHolder> {

    private ArrayList<SpotExample> mSpotList;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView spot_name;
        public TextView spot_date;
        public TextView spot_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            spot_name = itemView.findViewById(R.id.spot_name);
            spot_date = itemView.findViewById(R.id.spot_date);
            spot_time = itemView.findViewById(R.id.spot_time);
        }
    }

    public SpotAdapter(ArrayList<SpotExample> spotList){
        mSpotList = spotList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.example_myspots, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        SpotExample currentItem = mSpotList.get(i);
        viewHolder.spot_name.setText(currentItem.getSpot_name());
        viewHolder.spot_date.setText(currentItem.getSpot_date());
        viewHolder.spot_time.setText(currentItem.getSpot_time());
    }

    @Override
    public int getItemCount() {
        return mSpotList.size();
    }
}
