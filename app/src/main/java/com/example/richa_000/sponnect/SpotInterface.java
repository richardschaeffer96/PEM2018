package com.example.richa_000.sponnect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class SpotInterface extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_interface);

        ArrayList<ParticipantsExample> exampleList = new ArrayList<>();
        //Drawable images will not be pushed, need to be copied seprately. -Nanni
        exampleList.add(new ParticipantsExample("Heinz699", "male", "22", R.drawable.user1));
        exampleList.add(new ParticipantsExample("xXDragonfighterXx", "male", "19", R.drawable.user2));
        exampleList.add(new ParticipantsExample("Julia_N.", "female", "21", R.drawable.user3));

        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ParticipantsAdapter(exampleList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }
}
