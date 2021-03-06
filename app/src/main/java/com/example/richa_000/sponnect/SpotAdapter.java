package com.example.richa_000.sponnect;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Class for the recyclerview of the spot-list
 */
public class SpotAdapter extends RecyclerView.Adapter<SpotAdapter.ViewHolder> {

    private static ArrayList<Spot> mSpotList;
    private static String userID;
    private static User user;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView spot_name;
        public TextView spot_date;
        public TextView spot_time;

        /**
         * Is setting the information of a spot into the recyclerview
         * @param itemView
         */
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            spot_name = itemView.findViewById(R.id.spot_name);
            spot_date = itemView.findViewById(R.id.spot_date);
            spot_time = itemView.findViewById(R.id.spot_time);

            Typeface comfortaa_regular = Typeface.createFromAsset(itemView.getContext().getAssets(), "Comfortaa-Regular.ttf");
            Typeface comfortaa_bold = Typeface.createFromAsset(itemView.getContext().getAssets(), "Comfortaa-Bold.ttf");
            Typeface comfortaa_light = Typeface.createFromAsset(itemView.getContext().getAssets(), "Comfortaa-Light.ttf");
            
            spot_name.setTypeface(comfortaa_bold);
            spot_date.setTypeface(comfortaa_regular);
            spot_time.setTypeface(comfortaa_regular);

            itemView.setOnClickListener((v) -> {
                int position = getAdapterPosition();
                Spot clickedSpot = mSpotList.get(position);
                Snackbar.make(v, "Click detected on item " + position, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent intent = new Intent(itemView.getContext(), SpotInterface.class);
                intent.putExtra("spot", clickedSpot);
                intent.putExtra("id", userID);
                //intent.putExtra("user", user);
                itemView.getContext().startActivity(intent);

            });
        }
    }

    public SpotAdapter(ArrayList<Spot> spotList, String userID, User user){

        mSpotList = spotList;
        this.userID = userID;
        this.user = user;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.example_myspots, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    /**
     * sets information and checks if the user is the creator of the spots. If so, the color of the spot is changing.
     * @param viewHolder
     * @param i
     */
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
