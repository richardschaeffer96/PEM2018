package com.example.richa_000.sponnect;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SpotInterface extends AppCompatActivity {

    private static final String TAG = "SpotInterface";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("users");
    private CollectionReference spotsRef = db.collection("spots");

    private TextView spotTitle;
    private TextView spotDate;
    private TextView spotTime;
    private TextView spotDesc;
    private TextView spotAddress;

    private ImageButton tooLateButton;
    private ImageButton checkButton;
    private ImageButton raiseHandButton;

    private Typeface comfortaa_regular;
    private Typeface comfortaa_bold;
    private Typeface comfortaa_light;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private String userID;
    private Spot selectedSpot;

    private int state;
    private boolean closeEnough;

    private Runnable runnableCode;
    private Handler handler;
    private LocationManager locationManager;
    private static final int REQUEST_LOCATION = 1;


    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            curLoc = location;
            //your code here
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private ArrayList<ParticipantsExample> exampleList;

    private Location curLoc;
    private User me;
    private TextView line1;
    private TextView line2;
    private ImageView profile;
    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_interface);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        comfortaa_regular = Typeface.createFromAsset(this.getAssets(), "Comfortaa-Regular.ttf");
        comfortaa_bold = Typeface.createFromAsset(this.getAssets(), "Comfortaa-Bold.ttf");
        comfortaa_light = Typeface.createFromAsset(this.getAssets(), "Comfortaa-Light.ttf");

        TextView text_participants = findViewById(R.id.text_participants);
        text_participants.setTypeface(comfortaa_regular);
        ImageButton copy_button = findViewById(R.id.button_copy);
        //copy_button.setTypeface(comfortaa_bold);

        userID = getIntent().getStringExtra("id");
        //me = (User) getIntent().getSerializableExtra("user");

        //setUserInfo(me);
        getLoggedInUser(userID);

        closeEnough = false;

        selectedSpot = (Spot) getIntent().getSerializableExtra("spot");
        Log.d(TAG, "onCreate: Given Spot that was clicked is: "+ selectedSpot.getTitle()+", on: "+ selectedSpot.getDate()+", "+ selectedSpot.getTime());
        setSpotInformation(selectedSpot);
        mLayoutManager = new LinearLayoutManager(this);

        HashMap<String, Integer> spotParticipants = selectedSpot.getParticipants();
        state = spotParticipants.get(userID);


        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1,
                2, mLocationListener);
        
        
        
        exampleList = new ArrayList<>();
        usersRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    User user = documentSnapshot.toObject(User.class);
                    HashMap<String, Integer> participants = selectedSpot.getParticipants();
                    for (Map.Entry<String, Integer> entry : participants.entrySet()) {
                        if (user.getId() != null && user.getId().equals(entry.getKey())) {
                            if(userID.equals(user.getId())){
                                spotsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            Spot spot = documentSnapshot.toObject(Spot.class);
                                            HashMap<String, Integer> participants = spot.getParticipants();
                                            for (Map.Entry<String, Integer> entry : participants.entrySet()) {
                                                if (entry.getKey().equals(userID)) {
                                                    state = entry.getValue();
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                            exampleList.add(new ParticipantsExample(user.getNickname(), user.getGender(), "" + user.getAge(), user.getImageUri(), 2, user.getId()));
                        }
                    }
                }
                mAdapter = new ParticipantsAdapter(exampleList, userID, selectedSpot);
                mRecyclerView = findViewById(R.id.recyclerview);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mAdapter);
            }
        });

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
                refresh();
                handler.postDelayed(this, 4000);
            }
        };
        handler.post(runnableCode);
        super.onStart();
    }

    private void refresh() {
        //works fine just always gets the same coordinates from function
        //Log.d(TAG, "refresh: HERE! " + state);
        Location loc = curLoc;//checkCurrentLocation();
        if (state==2 && selectedSpot.getcreator().equals(userID)){
            //double[] creatorLoc = getLocation();
            Log.d(TAG, "refresh: Current Location of Creator: "+loc.getLatitude()+", "+loc.getLongitude());
            spotsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot.getId().equals(selectedSpot.getId())) {
                            DocumentReference refSpot = spotsRef.document(selectedSpot.getId());
                            refSpot.update("latitude", loc.getLatitude());
                            refSpot.update("longitude", loc.getLongitude());
                        }
                    }
                }
            });
        }
        /*
        spotsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Spot spot = documentSnapshot.toObject(Spot.class);
                    HashMap<String, Integer> participants = spot.getParticipants();
                    for (Map.Entry<String, Integer> entry : participants.entrySet()) {
                        if (entry.getKey().equals(userID)) {
                            state = entry.getValue();
                        }
                    }
                }
            }
        });*/
        // Geo based button deactivating and coloring


        if(selectedSpot!=null && loc!=null) {
            Calendar c = Calendar.getInstance();
            Date currentTime = c.getTime();
            String spotDate = selectedSpot.getDate() + "-" + selectedSpot.getTime();
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy-HH:mm");
            Date date = null;
            try {
                date = format.parse(spotDate);
                c.setTime(date);
                c.add(Calendar.DATE, -1);
                if (currentTime.before(c.getTime())) {
                    checkButton.setBackgroundResource(R.drawable.buttonstylebackgrounddisabled);
                    raiseHandButton.setBackgroundResource(R.drawable.buttonstylebackgrounddisabled);
                    //tooLateButton.setBackgroundResource(R.drawable.buttonstylebackgrounddisabled);
                    checkButton.setEnabled(false);
                    raiseHandButton.setEnabled(false);
                    //tooLateButton.setEnabled(false);
                } else {
                    float[] results = new float[1];
                    Location.distanceBetween(selectedSpot.getLatitude(), selectedSpot.getLongitude(), loc.getLatitude(), loc.getLongitude(), results);
                    float distance = results[0] / 1000;
                    if (distance > 0.5) {
                        if (state == 2 || state == 3) {
                            state = 0;
                        }
                        if (state == 1) {
                            tooLateButton.setImageResource(R.drawable.toolate_checked);
                        }
                        checkButton.setBackgroundResource(R.drawable.buttonstylebackgrounddisabled);
                        raiseHandButton.setBackgroundResource(R.drawable.buttonstylebackgrounddisabled);
                        checkButton.setEnabled(false);
                        raiseHandButton.setEnabled(false);
                        closeEnough = false;
                    } else {
                        switch (state) {
                            case 0:
                                checkButton.setImageResource(R.drawable.there);
                                raiseHandButton.setImageResource(R.drawable.wave);
                                tooLateButton.setImageResource(R.drawable.toolate);
                                break;
                            case 1:
                                checkButton.setImageResource(R.drawable.there);
                                raiseHandButton.setImageResource(R.drawable.wave);
                                tooLateButton.setImageResource(R.drawable.toolate_checked);
                                break;
                            case 2:
                                checkButton.setImageResource(R.drawable.there_checked);
                                raiseHandButton.setImageResource(R.drawable.wave);
                                tooLateButton.setImageResource(R.drawable.toolate);
                                break;
                            case 3:
                                checkButton.setImageResource(R.drawable.there);
                                raiseHandButton.setImageResource(R.drawable.wave_checked);
                                tooLateButton.setImageResource(R.drawable.toolate);
                                break;
                        }
                        checkButton.setBackgroundResource(R.drawable.buttonstylebackground);
                        tooLateButton.setBackgroundResource(R.drawable.buttonstylebackground);
                        raiseHandButton.setBackgroundResource(R.drawable.buttonstylebackground);
                        checkButton.setEnabled(true);
                        raiseHandButton.setEnabled(true);
                        closeEnough = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Update Participants List
        if(mRecyclerView!=null) {
            updateParticipantsList();
        }
    }

    private void updateParticipantsList() {
        exampleList.clear();
        spotsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Spot docSpot = documentSnapshot.toObject(Spot.class);
                    if (selectedSpot.getId().equals(docSpot.getId())) {
                        HashMap<String, Integer> map = docSpot.getParticipants();
                        usersRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots2) {
                                exampleList.clear();
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots2) {
                                    User user = documentSnapshot.toObject(User.class);
                                    if (map.containsKey(user.getId())) {
                                        exampleList.add(new ParticipantsExample(user.getNickname(), user.getGender(), "" + user.getAge(), user.getImageUri(), map.get(user.getId()), user.getId()));
                                        mRecyclerView.getAdapter().notifyDataSetChanged();
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }


    private Location checkCurrentLocation() {
        //TODO Find Stuff here
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
        spotAddress = findViewById(R.id.spot_address);
        spotDesc = findViewById(R.id.spot_desc);

        spotTitle.setTypeface(comfortaa_bold);
        spotDate.setTypeface(comfortaa_regular);
        spotTime.setTypeface(comfortaa_regular);
        spotAddress.setTypeface(comfortaa_regular);
        spotDesc.setTypeface(comfortaa_regular);

        spotTitle.setText(spot.getTitle());
        spotDate.setText(spot.getDate());
        spotTime.setText(spot.getTime());
        spotDesc.setText(spot.getInfo());
        spotAddress.setText(spot.getAddress());

        raiseHandButton = findViewById(R.id.raiseHand_button);
        checkButton = findViewById(R.id.check_button);
        tooLateButton = findViewById(R.id.tooLate_button);

    }

    private void buildAlertMessageNoGps() {
        Toast.makeText(this, "No location tracking enabled", Toast.LENGTH_SHORT).show();
    }


    public void raiseHand(View view) {
        //Toast.makeText(this, "Raise Hand", Toast.LENGTH_SHORT).show();
        spotsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Spot spot = documentSnapshot.toObject(Spot.class);
                    HashMap<String, Integer> participants = spot.getParticipants();
                    for (Map.Entry<String, Integer> entry : participants.entrySet()) {
                        //get Spot with current user as participant
                        if(entry.getKey().equals(userID)&&spot.getId().equals(selectedSpot.getId())){
                            if(participants.get(entry.getKey())==3){
                                raiseHandButton.setImageResource(R.drawable.wave);
                                state=0;
                                participants.put(userID, 0);
                                DocumentReference refSpot = spotsRef.document(spot.getId());
                                refSpot.update("participants", participants);
                                exampleList.clear();
                                mRecyclerView.getAdapter().notifyDataSetChanged();
                            }else{
                                raiseHandButton.setImageResource(R.drawable.wave_checked);
                                state=3;
                                participants.put(userID, 3);
                                DocumentReference refSpot = spotsRef.document(spot.getId());
                                refSpot.update("participants", participants);
                            }
                            checkButton.setImageResource(R.drawable.there);
                            tooLateButton.setImageResource(R.drawable.toolate);
                            updateParticipantsList();
                        }
                    }
                }

            }
        });
    }
    public void tooLate(View view){
        //Toast.makeText(this, "Too Late", Toast.LENGTH_SHORT).show();
        spotsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Spot spot = documentSnapshot.toObject(Spot.class);
                    HashMap<String, Integer> participants = spot.getParticipants();
                    for (Map.Entry<String, Integer> entry : participants.entrySet()) {
                        //get Spot with current user as participant
                        if(entry.getKey().equals(userID) && spot.getId().equals(selectedSpot.getId())){
                            if(participants.get(entry.getKey())==1){
                                tooLateButton.setImageResource(R.drawable.toolate);
                                state=0;
                                participants.put(userID, 0);
                                DocumentReference refSpot = spotsRef.document(spot.getId());
                                refSpot.update("participants", participants);
                            }else{
                                tooLateButton.setImageResource(R.drawable.toolate_checked);
                                state=1;
                                participants.put(userID, 1);
                                DocumentReference refSpot = spotsRef.document(spot.getId());
                                refSpot.update("participants", participants);
                            }
                            checkButton.setImageResource(R.drawable.there);
                            raiseHandButton.setImageResource(R.drawable.wave);
                            if(closeEnough){
                                raiseHandButton.setBackgroundResource(R.drawable.buttonstylebackground);
                                checkButton.setBackgroundResource(R.drawable.buttonstylebackground);

                            }else{
                                raiseHandButton.setBackgroundResource(R.drawable.buttonstylebackgrounddisabled);
                                checkButton.setBackgroundResource(R.drawable.buttonstylebackgrounddisabled);
                            }
                            updateParticipantsList();
                        }
                    }
                }

            }
        });
    }
    public void checkButton(View view){
        //Toast.makeText(this, "Check", Toast.LENGTH_SHORT).show();
        spotsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Spot spot = documentSnapshot.toObject(Spot.class);
                    HashMap<String, Integer> participants = spot.getParticipants();
                    for (Map.Entry<String, Integer> entry : participants.entrySet()) {
                        //get Spot with current user as participant
                        if(entry.getKey().equals(userID)&&spot.getId().equals(selectedSpot.getId())){
                            if(participants.get(entry.getKey())==2){
                                checkButton.setImageResource(R.drawable.there);
                                raiseHandButton.setImageResource(R.drawable.wave);
                                tooLateButton.setImageResource(R.drawable.toolate);
                                state=0;
                                participants.put(userID, 0);
                                DocumentReference refSpot = spotsRef.document(spot.getId());
                                refSpot.update("participants", participants);
                            }else{
                                //Creator is on Location and checked himself in
                                checkButton.setImageResource(R.drawable.there_checked);
                                raiseHandButton.setImageResource(R.drawable.wave);
                                tooLateButton.setImageResource(R.drawable.toolate);
                                state=2;
                                participants.put(userID, 2);
                                DocumentReference refSpot = spotsRef.document(spot.getId());
                                refSpot.update("participants", participants);
                            }
                            updateParticipantsList();
                        }
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
        switch (item.getItemId()){
            case R.id.settings:
                Intent mIntent = new Intent(SpotInterface.this, EditProfile.class);
                mIntent.putExtra("id", userID);
                mIntent.putExtra("user", me);
                startActivity(mIntent);
                return true;
            case R.id.contacts:
                Intent mIntent2 = new Intent(SpotInterface.this, Contacts.class);
                mIntent2.putExtra("id", userID);
                mIntent2.putExtra("user", me);
                startActivity(mIntent2);
                return true;
            case R.id.home:
                Intent mIntent3 = new Intent(SpotInterface.this, Menu.class);
                mIntent3.putExtra("id", userID);
                mIntent3.putExtra("user", me);
                startActivity(mIntent3);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getLoggedInUser(String userId){

        final String id = userId;
        usersRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    if (documentSnapshot.getId().equals(id)) {

                        me = documentSnapshot.toObject(User.class);

                    }
                }
                Log.d(TAG, "onSuccess: User logged in: "+me.getNickname());
                setUserInfo(me);
            }
        });
    }

    /**
     * sets all needed information from the user to the toolbar layout
     * @param me
     */
    private void setUserInfo(User me){
        Log.d(TAG, "setUserInfo: user is created: "+me.getNickname());
        String nickname = me.getNickname();
        String gender = me.getGender();
        int age = me.getAge();
        String info = gender + ", "+age;
        line1 = findViewById(R.id.toolbarTextView1);
        line2 = findViewById(R.id.toolbarTextView2);
        line1.setTypeface(comfortaa_bold);
        line2.setTypeface(comfortaa_regular);
        line1.setText(nickname);
        line2.setText(info);
        profile = findViewById(R.id.iV_profile);
        Log.d(TAG, "User URI is: "+me.getImageUri() );
        if(me.getImageUri()!=null){
            Uri uri = Uri.parse(me.getImageUri());
            Picasso.get().load(uri).into(profile);
        }

    }

    /*
    private double[] getLocation() {

        double[] creatorCoords = new double[2];

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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

                //mMap.getUiSettings().setMyLocationButtonEnabled(false);
                //mMap.setMyLocationEnabled(true);
                if (location != null) {
                    creatorCoords[0] = location.getLatitude();
                    creatorCoords[1] = location.getLongitude();
                } else if (location1 != null) {
                    creatorCoords[0] = location1.getLatitude();
                    creatorCoords[1] = location1.getLongitude();
                } else if (location2 != null) {
                    creatorCoords[0] = location2.getLatitude();
                    creatorCoords[1] = location2.getLongitude();
                } else {
                    Toast.makeText(this, "Unble to Trace your location\nTry again later", Toast.LENGTH_SHORT).show();
                }
            }
        }
        return creatorCoords;
    }
    */

    public void copy(View v){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("address", selectedSpot.getAddress());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Copied spot address!", Toast.LENGTH_SHORT).show();
    }

}
