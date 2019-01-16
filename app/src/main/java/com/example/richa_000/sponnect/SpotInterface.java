package com.example.richa_000.sponnect;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SpotInterface extends AppCompatActivity {

    private static final String TAG = "SpotInterface";

    private TextView spotTitle;
    private TextView spotDate;
    private TextView spotTime;
    private TextView spotDesc;

    private ImageButton tooLateButton;
    private ImageButton checkButton;
    private ImageButton raiseHandButton;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private String userID;
    private Spot spot;

    private Runnable runnableCode;
    private Handler handler;
    private LocationManager locationManager;
    private static final int REQUEST_LOCATION = 1;

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

        handler = new Handler();
    }

    @Override
    protected void onStop() {
        handler.removeCallbacks(runnableCode);
        super.onStop();
    }

    @Override
    protected void onStart() {
        runnableCode = new Runnable() {
            @Override
            public void run() {
                Location loc = checkCurrentLocation();
                float[] results = new float[1];
                Location.distanceBetween(spot.getLatitude(), spot.getLongitude(), loc.getLatitude(), loc.getLongitude(), results);
                float distance = results[0]/1000;
                if (distance > 0.5){
                    checkButton.setBackgroundColor(Color.GRAY);
                    raiseHandButton.setBackgroundColor(Color.GRAY);
                    checkButton.setEnabled(false);
                    raiseHandButton.setEnabled(false);
                }else{
                    checkButton.setBackgroundColor(Color.parseColor("#FF74E2F1"));
                    raiseHandButton.setBackgroundColor(Color.parseColor("#FF74E2F1"));
                    checkButton.setEnabled(true);
                    raiseHandButton.setEnabled(true);
                }
                handler.postDelayed(this, 2000);
            }
        };
        handler.post(runnableCode);
        super.onStart();
    }

    private Location checkCurrentLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location resultLocation = null;

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            if (ActivityCompat.checkSelfPermission(SpotInterface.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                    (SpotInterface.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "No location tracking enabled", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(SpotInterface.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            } else {
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

                if (location != null) {
                    resultLocation = location;

                } else if (location1 != null) {
                    resultLocation = location1;

                } else if (location2 != null) {
                    resultLocation = location2;

                } else {
                    Toast.makeText(this, "Unble to Trace your location\nTry again later", Toast.LENGTH_SHORT).show();
                }
            }
        }
        return resultLocation;
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

        raiseHandButton = findViewById(R.id.raiseHand_button);
        checkButton = findViewById(R.id.check_button);
        tooLateButton = findViewById(R.id.tooLate_button);

    }

    private void buildAlertMessageNoGps() {
        Toast.makeText(this, "No location tracking enabled", Toast.LENGTH_SHORT).show();
    }
}
