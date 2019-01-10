package com.example.richa_000.sponnect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

public class SpotInterface extends AppCompatActivity {

    private static final String TAG = "SpotInterface";

    private TextView spotTitle;
    private TextView spotDate;
    private TextView spotTime;
    private TextView spotDesc;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private String userID;
    private Spot spot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_interface);

        userID = getIntent().getStringExtra("id");
        spot = (Spot) getIntent().getSerializableExtra("spot");
        Log.d(TAG, "onCreate: Given Spot that was clicked is: "+spot.getTitle()+", on: "+spot.getDate()+", "+spot.getTime());
        setSpotInformation(spot);

        //Log.d(TAG, "onCreate: User is logged in"+userID);

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

    /**
     * function to set the Spot information in the Spot interface. Needs to be in own function because java is stupid and does weird things.
     * @param spot
     */
    private void setSpotInformation(Spot spot){
        //Setting information
        spotTitle = findViewById(R.id.spot_title);
        spotDate = findViewById(R.id.spot_date);
        spotTime = findViewById(R.id.spot_time);
        spotTitle.setText(spot.getTitle());
        spotDate.setText(spot.getDate());
        spotTime.setText(spot.getTime());
    }
}
