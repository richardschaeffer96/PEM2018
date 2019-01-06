package com.example.richa_000.sponnect;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.ViewHolder> {

    private ArrayList<ParticipantsExample> mParticipantList;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private TextView gender;
        private TextView age;
        private ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_name);
            gender = itemView.findViewById(R.id.text_gender);
            age = itemView.findViewById(R.id.text_age);
            img = itemView.findViewById(R.id.image_avatar);

            itemView.setOnClickListener((v) -> {
                int position = getAdapterPosition();
                Snackbar.make(v, "Click detected on item " + position, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            });
        }
    }

    public ParticipantsAdapter(ArrayList<ParticipantsExample> participantList){
        mParticipantList = participantList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.example_participants, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ParticipantsExample currentItem = mParticipantList.get(i);
        viewHolder.name.setText(currentItem.getName());
        viewHolder.age.setText(currentItem.getAge());
        viewHolder.gender.setText(currentItem.getGender());
        viewHolder.img.setImageResource(currentItem.getImg());
    }

    @Override
    public int getItemCount() {
        return mParticipantList.size();
    }
}
