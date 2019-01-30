package com.example.richa_000.sponnect;

import android.graphics.Color;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private static HashMap<String,ArrayList<String>> mContacts;

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static CollectionReference usersRef = db.collection("users");

    public static class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder{
        private TextView fb;
        private TextView twitter;
        private TextView insta;
        private TextView name;
        private TextView gender;
        private TextView age;
        private ImageView img;
        ImageButton delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fb = itemView.findViewById(R.id.tv_contact_fb);
            twitter = itemView.findViewById(R.id.tv_contact_twitter);
            insta = itemView.findViewById(R.id.tv_contact_insta);
            name = itemView.findViewById(R.id.text_contact_name);
            gender = itemView.findViewById(R.id.text_contact_gender);
            age = itemView.findViewById(R.id.text_contact_age);
            img = itemView.findViewById(R.id.image_contact_avatar);
            //delete = itemView.findViewById(R.id.btn_delete);
        }
    }

    public ContactsAdapter(HashMap<String, ArrayList<String>> contacts){
        mContacts = contacts;
    }

    @NonNull
    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contacts, viewGroup, false);
        ContactsAdapter.ViewHolder vh = new ContactsAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Map.Entry<String, ArrayList<String>>[] entries = new Map.Entry[mContacts.size()];
        int count = 0;
        for (Map.Entry<String, ArrayList<String>> e: mContacts.entrySet()) {
            if(count==i){
                viewHolder.fb.setText(e.getValue().get(0));
                viewHolder.twitter.setText(e.getValue().get(1));
                viewHolder.insta.setText(e.getValue().get(2));
                usersRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            User user = documentSnapshot.toObject(User.class);
                            if (user.getId().equals(e.getKey())) {
                                viewHolder.name.setText(user.getNickname());
                                //viewHolder.age.setText(user.getAge());
                                viewHolder.gender.setText(user.getGender());
                                //viewHolder.img.setImageResource(user.getgetImg());
                            }
                        }
                    }
                });
            }
            else count+=1;
        }


    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public static HashMap<String, ArrayList<String>> getmContacts() {
        return mContacts;
    }

    public static void setmContacts(HashMap<String, ArrayList<String>> mContacts) {
        ContactsAdapter.mContacts = mContacts;
    }
}