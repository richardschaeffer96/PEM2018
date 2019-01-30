package com.example.richa_000.sponnect;

import android.content.Intent;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SpotAdapter extends RecyclerView.Adapter<SpotAdapter.ViewHolder> {

    private static ArrayList<Spot> mSpotList;
    private static String userID;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView spot_name;
        public TextView spot_date;
        public TextView spot_time;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            spot_name = itemView.findViewById(R.id.spot_name);
            spot_date = itemView.findViewById(R.id.spot_date);
            spot_time = itemView.findViewById(R.id.spot_time);

            itemView.setOnClickListener((v) -> {
                int position = getAdapterPosition();
                Spot clickedSpot = mSpotList.get(position);
                Snackbar.make(v, "Click detected on item " + position, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent intent = new Intent(itemView.getContext(), SpotInterface.class);
                intent.putExtra("spot", clickedSpot);
                intent.putExtra("id", userID);
                itemView.getContext().startActivity(intent);

            });
        }
    }

    public SpotAdapter(ArrayList<Spot> spotList, String userID){

        mSpotList = spotList;
        this.userID = userID;
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
        Spot currentItem = mSpotList.get(i);
        viewHolder.spot_name.setText(currentItem.getTitle());
        viewHolder.spot_date.setText(currentItem.getDate());
        viewHolder.spot_time.setText(currentItem.getTime());
        //change color depending of creator id
        if(currentItem.getcreator().equals(userID)){
            viewHolder.itemView.setBackgroundResource(R.color.greenAlpha);
        }else{
            //stay default blue
        }
    }

    @Override
    public int getItemCount() {
        return mSpotList.size();
    }
}
