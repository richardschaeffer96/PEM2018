package com.example.richa_000.sponnect;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class GuideActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    Dialog mapOverlay;

    private static final int REQUEST_LOCATION = 1;
    private final String DEL = "deleeeting...";
    private LocationManager locationManager;
    private Button btnGetCurrentLocation;
    private GoogleMap mMap;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference spotsRef = db.collection("spots");
    private CollectionReference usersRef = db.collection("users");
    private String userID;

    private Typeface comfortaa_regular;
    private Typeface comfortaa_bold;
    private Typeface comfortaa_light;

    PlaceAutocompleteFragment placeAutoComplete;

    private Geocoder geocoder;
    private List<Address> addresses;
    private static final String TAG = "GuideActivity";
    private static final float DEFAULT_ZOOM = 15f;

    private ArrayList<Spot> spots = new ArrayList<>();
    private Place selectedPlace;
    private Spot selectedSpot;

    //marker colors
    float hueBlue = 205f;
    float hueGreen = 105f;

    private User me;
    private TextView line1;
    private TextView line2;
    private ImageView profile;

    private Runnable runnableCode;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_activity);

        comfortaa_regular = Typeface.createFromAsset(this.getAssets(), "Comfortaa-Regular.ttf");
        comfortaa_bold = Typeface.createFromAsset(this.getAssets(), "Comfortaa-Bold.ttf");
        comfortaa_light = Typeface.createFromAsset(this.getAssets(), "Comfortaa-Light.ttf");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        me = (User) getIntent().getSerializableExtra("user");
        setUserInfo(me);

        placeAutoComplete = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete);
        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                geoLocate(place);
                selectedPlace = place;
                Log.d("Maps", "Place selected: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                Log.d("Maps", "An error occurred: " + status);
            }
        });

        spotsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                    //TODO Range Query or Number restriction if too many
                    if(true){
                        Spot spot = documentSnapshot.toObject(Spot.class);
                        spots.add(spot);
                        //Log.d(TAG, "Spots loaded: "+spot.getTitle());
                        setAllSpotMarker(spots);
                    }

                }

            }
        });

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        initMap();
        btnGetCurrentLocation = findViewById(R.id.btn_guide_getCurrentLocation);
        btnGetCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });

        userID = getIntent().getStringExtra("id");

        mapOverlay = new Dialog(this);
        handler = new Handler();
    }

    /**
     * function to show the map overlay when clicked on a Spot
     * all information is loaded from the database and the button displays one of the following options:
     * JOIN, LEAVE and DELETE
     * @param spot
     */
    public void showMapOverlay(Spot spot){
        mapOverlay.setContentView(R.layout.map_overlay);
        TextView title = mapOverlay.findViewById(R.id.textView_headline);
        title.setText(spot.getTitle());

        TextView date = mapOverlay.findViewById(R.id.text_date);
        date.setText(spot.getDate() + "\n"+spot.getTime());

        Button buttonJoin = mapOverlay.findViewById(R.id.button_join);

        TextView distance = mapOverlay.findViewById(R.id.text_distance);
        Location spotLoc = new Location("spot");
        spotLoc.setLatitude(spot.getLatitude());
        spotLoc.setLongitude(spot.getLatitude());
        Location currentLoc = new Location("current");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(GuideActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                    (GuideActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "No location tracking enabled", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(GuideActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            } else {
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

                if (location != null) {
                    currentLoc = location;

                } else if (location1 != null) {
                    currentLoc = location1;

                } else if (location2 != null) {
                    currentLoc = location2;

                } else {
                    Toast.makeText(this, "Unble to Trace your location\nTry again later", Toast.LENGTH_SHORT).show();
                }
            }
        }
        float[] results = new float[1];
        Location.distanceBetween(spot.getLatitude(), spot.getLongitude(), currentLoc.getLatitude(), currentLoc.getLongitude(), results);
        double f = Math.round(results[0]/10) / 100.0;
        distance.setText(" ("+f+" km)");

        TextView address = mapOverlay.findViewById(R.id.spot_address);
        address.setText(spot.getAddress());

        TextView info = mapOverlay.findViewById(R.id.text_info);
        info.setText(spot.getInfo());

        //get creator
        String creatorID = spot.getcreator();
        usersRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                    if(creatorID.equals(documentSnapshot.getId())){
                        User creator = documentSnapshot.toObject(User.class);

                        ImageView creatorProfile = mapOverlay.findViewById(R.id.iVguider);
                        Uri uri = Uri.parse(creator.getImageUri());
                        Picasso.get().load(uri).into(creatorProfile);

                        if(creatorID.equals(userID)){
                            //LoggedIn User created the Spot
                            TextView creatorName = mapOverlay.findViewById(R.id.text_name);
                            creatorName.setText("This is your Spot!");

                            TextView creatorGender = mapOverlay.findViewById(R.id.text_gender);
                            creatorGender.setText("");

                            TextView creatorAge = mapOverlay.findViewById(R.id.text_age);
                            creatorAge.setText("");
                        } else{
                            //Show Creator's information
                            TextView creatorName = mapOverlay.findViewById(R.id.text_name);
                            creatorName.setText(creator.getNickname());

                            TextView creatorGender = mapOverlay.findViewById(R.id.text_gender);
                            creatorGender.setText(creator.getGender());

                            TextView creatorAge = mapOverlay.findViewById(R.id.text_age);
                            String age = creator.getAge()+"";
                            creatorAge.setText(age);
                        }
                    }

                }

            }
        });

        TextView numberParticipants = mapOverlay.findViewById(R.id.number_participants);
        int numberParticitpants = spot.getParticipants().size();
        numberParticipants.setText(numberParticitpants+"");

        usersRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    if(documentSnapshot.getId().equals(userID)){
                        User user = documentSnapshot.toObject(User.class);
                        if(user.getMySpots().containsKey(spot.getId())) {
                            Log.d(TAG, "Spot is already in Spot List.");
                            if(user.getMySpots().get(spot.getId())){
                                buttonJoin.setText("Delete Spot");
                            } else{
                                buttonJoin.setText("Leave Spot")
                                ;
                            }
                        }
                    }
                }
            }
        });

        selectedSpot = spot;
        mapOverlay.show();
    }

    /**
     * function to create all spot marker in the spots list
     * @param spots
     */
    private void setAllSpotMarker(ArrayList<Spot> spots){
        mMap.clear();
        //Log.d(TAG, "setAllSpotMarker: Liste in Marker Funktion mit Entry: "+spots.get(0).getTitle());
        for (int i = 0; i < spots.size(); i++) {
            LatLng pos = new LatLng(spots.get(i).getLatitude(), spots.get(i).getLongitude());
            Spot spot = spots.get(i);
            Date currentTime = Calendar.getInstance().getTime();
            String spotDate = spot.getDate()+"-"+spot.getTime();
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy-HH:mm");
            Date date = null;
            try {
                date = format.parse(spotDate);
                if(spot.getcreator().equals(userID) && !(spot.getInfo().equals(DEL))){
                    if(date.after(currentTime)) {
                        mMap.addMarker(new MarkerOptions().position(pos).title(spot.getTitle())
                                .icon(BitmapDescriptorFactory.defaultMarker(hueGreen)));
                    }else{
                       // mMap.addMarker(new MarkerOptions().position(pos).title(spot.getTitle()).icon(BitmapDescriptorFactory.defaultMarker(hueGreen)).alpha(0.2f));
                    }
                } else{
                    if(date.after(currentTime) && !(spot.getInfo().equals(DEL))) {
                        mMap.addMarker(new MarkerOptions().position(pos).title(spot.getTitle())
                                .icon(BitmapDescriptorFactory.defaultMarker(hueBlue)));
                    }else{
                        //mMap.addMarker(new MarkerOptions().position(pos).title(spot.getTitle()).icon(BitmapDescriptorFactory.defaultMarker(hueBlue)).alpha(0.2f));
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * convert Google-Places information into address
     * @param place
     */
    private void geoLocate(Place place) {

        Geocoder geocode = new Geocoder(GuideActivity.this);
        List<Address> list = new ArrayList<>();

        try {
            list = geocode.getFromLocationName((String) place.getAddress(), 1);
        } catch (IOException e) {
            Toast.makeText(this, "There seems to have been a problem.\n Please try again or enter a different address.", Toast.LENGTH_SHORT).show();
        }
        if (list.size() > 0) {

            Address address = list.get(0);
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM);
        }

    }

    /**
     * init map fragment
     */
    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.guide_map);
        mapFragment.getMapAsync(GuideActivity.this);

    }

    /**
     * get location from GPS services of smartphone
     */
    private void getLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            if (ActivityCompat.checkSelfPermission(GuideActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                    (GuideActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "No location tracking enabled", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(GuideActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            } else {
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.setMyLocationEnabled(true);
                if (location != null) {
                    LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
                    moveCamera(pos, DEFAULT_ZOOM);

                } else if (location1 != null) {
                    LatLng pos = new LatLng(location1.getLatitude(), location1.getLongitude());
                    moveCamera(pos, DEFAULT_ZOOM);

                } else if (location2 != null) {
                    LatLng pos = new LatLng(location2.getLatitude(), location2.getLongitude());
                    moveCamera(pos, DEFAULT_ZOOM);

                } else {
                    Toast.makeText(this, "Unble to Trace your location\nTry again later", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * show dialog if GPS is not working
     */
    private void buildAlertMessageNoGps() {
        Toast.makeText(this, "No location tracking enabled", Toast.LENGTH_SHORT).show();
    }

    /**
     * when map is ready init listener if clicked somewhere
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;
        getLocation();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                markerClicked(marker);

                return true;
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                onMapClicked(latLng);
            }
        });
    }

    /**
     * when clicked on a marker, the corresponding overlay with the information and the button to join is shown
     * @param marker
     */
    private void markerClicked(Marker marker) {

        CollectionReference spots = db.collection("spots");
        spots.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Spot spot = documentSnapshot.toObject(Spot.class);
                    if(marker.getPosition().latitude == spot.getLatitude() && marker.getPosition().longitude==spot.getLongitude()) {
                        showMapOverlay(spot);
                    }
                }
            }
        });

    }

    /**
     * when clicked on the map, the CreateSpot Activity is started with the autocompleted location where the user tabbed.
     * @param latLng
     */
    private void onMapClicked(LatLng latLng) {
        geocoder = new Geocoder(this, Locale.getDefault());
        try {

            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            Intent intent = new Intent(GuideActivity.this, CreateSpotActivity.class);
            intent.putExtra("Address", address);
            intent.putExtra("Lat", Double.toString(latLng.latitude));
            intent.putExtra("Lng", Double.toString(latLng.longitude));
            intent.putExtra("id", userID);
            intent.putExtra("user", me);
            startActivity(intent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * when located, map zooms in to the users position
     * @param latLng
     * @param zoom
     */
    private void moveCamera(LatLng latLng, float zoom) {
        //Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * start Activity to add a new Spot
     * @param view
     */
    public void addSpot(View view){
        Intent intent = new Intent(GuideActivity.this, CreateSpotActivity.class);
        intent.putExtra("id", userID);
        intent.putExtra("user", me);
        startActivity(intent);
    }

    /**
     * when the join/leave/delete button is clicked the respected action is performed.
     * if the spot is not in the user's Spot list, the user can join the Spot, if it is already there, the user can leave
     * if the user created this spot the Spot will be marked as deleted and will not show on the map anymore until it is deleted from the DB
     * Joining and leaving includes the modifying of the respective lists in the DB
     * @param view
     */
    public void join(View view) {

        spotsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Spot spot = documentSnapshot.toObject(Spot.class);
                    if (selectedSpot.getId().equals(spot.getId())) {
                        DocumentReference refSpot = spotsRef.document(selectedSpot.getId());
                        HashMap<String, Integer> participantMap = spot.getParticipants();
                        if (!participantMap.containsKey(userID)) {
                            participantMap.put(userID, 0);
                            refSpot.update("participants", participantMap);
                            Toast.makeText(GuideActivity.this, "You joined the spot", Toast.LENGTH_SHORT).show();
                        } else{
                            if(selectedSpot.getcreator().equals(userID)){
                                AlertDialog alertDialog = new AlertDialog.Builder(GuideActivity.this).create();
                                alertDialog.setTitle("Are your sure?");
                                alertDialog.setMessage("If you leave your Spot, the Spot will be deleted. Is that okay?");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Yes, Delete!",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                                Log.d(TAG, "Deleting Spot...");
                                participantMap.remove(userID);
                                refSpot.update("participants", participantMap);
                                refSpot.update("info", DEL);
                                Toast.makeText(GuideActivity.this, "You deleted the spot", Toast.LENGTH_SHORT).show();
                            } else{
                               // Just leave Spot
                                participantMap.remove(userID);
                                refSpot.update("participants", participantMap);
                                Toast.makeText(GuideActivity.this, "You left the spot", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                }
            }
        });

        usersRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    User user = documentSnapshot.toObject(User.class);
                    if (userID.equals(user.getId())) {
                        DocumentReference refUser = usersRef.document(userID);
                        HashMap<String, Boolean> spots = user.getMySpots();
                        if (!spots.containsKey(selectedSpot.getId())) {
                            spots.put(selectedSpot.getId(), false);
                            refUser.update("mySpots", spots);
                        } else{
                            if(spots.containsKey(selectedSpot.getId()) || selectedSpot.getInfo().equals(DEL)) {
                                // Just leave Spot
                                spots.remove(selectedSpot.getId());
                                refUser.update("mySpots", spots);
                                Log.d(TAG, "Deleting Spot...");
                            }
                        }

                    }
                }
            }
        });

        mapOverlay.hide();

    }

    /**
     * generate toolbar menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * method to handle menu input in toolbar
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                Intent mIntent = new Intent(GuideActivity.this, EditProfile.class);
                mIntent.putExtra("id", userID);
                mIntent.putExtra("user", me);
                startActivity(mIntent);
                return true;
            case R.id.contacts:
                Intent mIntent2 = new Intent(GuideActivity.this, Contacts.class);
                mIntent2.putExtra("id", userID);
                mIntent2.putExtra("user", me);
                startActivity(mIntent2);
                return true;
            case R.id.home:
                Intent mIntent3 = new Intent(GuideActivity.this, Menu.class);
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
    private void setUserInfo(User me) {
        String nickname = me.getNickname();
        String gender = me.getGender();
        int age = me.getAge();
        String info = gender + ", " + age;
        line1 = findViewById(R.id.toolbarTextView1);
        line2 = findViewById(R.id.toolbarTextView2);
        line1.setTypeface(comfortaa_bold);
        line2.setTypeface(comfortaa_regular);
        line1.setText(nickname);
        line2.setText(info);
        profile = findViewById(R.id.iV_profile);
        if (me.getImageUri() != null) {
            Uri uri = Uri.parse(me.getImageUri());
            Picasso.get().load(uri).into(profile);
        }
    }

    /**
     * stop updates when leaving this screen
     */
    @Override
    protected void onStop() {
        handler.removeCallbacks(runnableCode);
        super.onStop();
    }

    /**
     * when Activity is started RefreshSpots is called every second
     */
    @Override
    protected void onStart() {
        runnableCode = new Runnable() {
            @Override
            public void run() {
                refreshSpots();
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnableCode);
        super.onStart();
    }

    /**
     * update View to show all Spots in real time
     */
    private void refreshSpots(){
        Log.d(TAG, "refreshSpots: Updated");

        ArrayList<Spot> spotsUpdate = new ArrayList<Spot>();
        spotsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                    Spot spot = documentSnapshot.toObject(Spot.class);
                    spotsUpdate.add(spot);
                }
                setAllSpotMarker(spotsUpdate);
            }

        });

    }
}

