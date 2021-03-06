package com.example.richa_000.sponnect;

import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class for the recyclerview of the participant-list
 */
public class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.ViewHolder> {

    private static Spot selectedSpot;
    private static String userID;
    private static ArrayList<ParticipantsExample> mParticipantList;

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static CollectionReference usersRef = db.collection("users");

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private TextView gender;
        private TextView age;
        private ImageView img;
        ImageButton share;
        ImageButton share_real;

        /**
         * Is setting the information into the recyclerview and activating the buttons inside it
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_name);
            gender = itemView.findViewById(R.id.text_gender);
            age = itemView.findViewById(R.id.text_age);
            img = itemView.findViewById(R.id.image_avatar);
            share = itemView.findViewById(R.id.button_share);
            share_real = itemView.findViewById(R.id.button_sharereal);
            share.setImageResource(R.drawable.empty);

            Typeface comfortaa_regular = Typeface.createFromAsset(itemView.getContext().getAssets(), "Comfortaa-Regular.ttf");
            Typeface comfortaa_bold = Typeface.createFromAsset(itemView.getContext().getAssets(), "Comfortaa-Bold.ttf");
            Typeface comfortaa_light = Typeface.createFromAsset(itemView.getContext().getAssets(), "Comfortaa-Light.ttf");

            name.setTypeface(comfortaa_bold);
            age.setTypeface(comfortaa_regular);
            gender.setTypeface(comfortaa_regular);

            share_real.setOnClickListener((v) ->{
                int position = getAdapterPosition();
                ParticipantsExample currentItem = mParticipantList.get(position);
                usersRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        User currentUser= null;
                        User selectedUser = null;
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            User user = documentSnapshot.toObject(User.class);
                            if(user.getId().equals(userID)){
                                currentUser= user;
                            }
                            if(user.getId().equals(currentItem.getId())){
                                selectedUser = user;
                            }
                        }

                        HashMap<String, ArrayList<String>> contacts = null;
                        while (selectedUser==null || currentUser==null){

                        }
                        if(selectedUser!=null){
                            contacts = new HashMap<String, ArrayList<String>>();
                            if(selectedUser.getId().equals(currentUser.getId())) {
                                Snackbar.make(v, "Don't share stuff with yourself you moron!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            }else {
                                if (!selectedUser.getContacts().containsKey(currentUser.getId())) {
                                    contacts = selectedUser.getContacts();
                                    contacts.put(currentUser.getId(), currentUser.getSocialMedia());
                                    DocumentReference refUser = usersRef.document(selectedUser.getId());
                                    refUser.update("contacts", contacts);
                                    Snackbar.make(v, "You shared your contact info with " + selectedUser.getNickname(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                } else {
                                    Snackbar.make(v, "You already shared your contact info with " + selectedUser.getNickname(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                }
                            }
                        }

                    }
                });

            });

            itemView.setOnClickListener((v) -> {
                int position = getAdapterPosition();
                Snackbar.make(v, "Click detected on item " + position, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            });
        }
    }

    public ParticipantsAdapter(ArrayList<ParticipantsExample> participantList, String userID, Spot selectedSpot){
        mParticipantList = participantList;
        this.userID = userID;
        this.selectedSpot = selectedSpot;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.example_participants, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    /**
     * Switch case for different status of the user for each participant (empty, too late, there, wave).
     * Afterwards the specific picture is shown.
     * @param viewHolder
     * @param i
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        ParticipantsExample currentItem = mParticipantList.get(i);
        if(currentItem.getId().equals(selectedSpot.getcreator())){
            viewHolder.itemView.setBackgroundResource(R.color.greenAlpha);
        }
        viewHolder.name.setText(currentItem.getName());
        viewHolder.age.setText(currentItem.getAge());
        viewHolder.gender.setText(currentItem.getGender());
        Uri uri = Uri.parse(currentItem.getImg());
        Picasso.get().load(uri).into(viewHolder.img);

        switch(currentItem.getState()){
            case 0: viewHolder.share.setImageResource(R.drawable.empty);break;
            case 1: viewHolder.share.setImageResource(R.drawable.toolate);break;
            case 2: viewHolder.share.setImageResource(R.drawable.there);break;
            case 3: viewHolder.share.setImageResource(R.drawable.wave);break;
        }
    }


    // Getter & Setter
    @Override
    public int getItemCount() {
        return mParticipantList.size();
    }

    public ArrayList<ParticipantsExample> getmParticipantList() {
        return mParticipantList;
    }

    public void setmParticipantList(ArrayList<ParticipantsExample> mParticipantList) {
        this.mParticipantList = mParticipantList;
    }
}
