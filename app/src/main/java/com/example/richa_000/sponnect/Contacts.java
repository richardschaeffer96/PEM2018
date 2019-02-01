package com.example.richa_000.sponnect;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Contacts extends AppCompatActivity {


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("users");

    private User me;
    public String userID;
    private TextView line1;
    private TextView line2;
    private ImageView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        userID = getIntent().getStringExtra("id");
        me = (User) getIntent().getSerializableExtra("user");
        setUserInfo(me);

        mLayoutManager = new LinearLayoutManager(this);

        usersRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                HashMap<String, ArrayList<String>> list = new HashMap<>();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    User user = documentSnapshot.toObject(User.class);
                    if(user.getId().equals(userID)){
                        list = user.getContacts();
                        mAdapter = new ContactsAdapter(list);
                        mRecyclerView = findViewById(R.id.recyclerview);
                        mRecyclerView.setHasFixedSize(true);
                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                }
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent mIntent = new Intent(Contacts.this, SignUp.class);
                mIntent.putExtra("id", userID);
                mIntent.putExtra("user", me);
                startActivity(mIntent);
                return true;
            case R.id.contacts:
                Intent mIntent2 = new Intent(Contacts.this, Contacts.class);
                mIntent2.putExtra("id", userID);
                mIntent2.putExtra("user", me);
                startActivity(mIntent2);
                return true;
            case R.id.home:
                Intent mIntent3 = new Intent(Contacts.this, Menu.class);
                mIntent3.putExtra("id", userID);
                mIntent3.putExtra("user", me);
                startActivity(mIntent3);
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    /**
     * sets all needed information from the user to the toolbar layout
     * @param me
     */
    private void setUserInfo(User me){
        String nickname = me.getNickname();
        String gender = me.getGender();
        int age = me.getAge();
        String info = gender + ", ("+age+")";
        line1 = findViewById(R.id.toolbarTextView1);
        line2 = findViewById(R.id.toolbarTextView2);
        line1.setText(nickname);
        line2.setText(info);
        profile = findViewById(R.id.iV_profile);
        Uri uri = Uri.parse(me.getImageUri());
        Picasso.get().load(uri).into(profile);
    }

}