package com.example.richa_000.sponnect;

import android.*;
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class GuideActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    Dialog mapOverlay;

    private static final int REQUEST_LOCATION = 1;
    private LocationManager locationManager;
    private Button btnGetCurrentLocation;
    private GoogleMap mMap;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference spotsRef = db.collection("spots");
    private String userID;

    PlaceAutocompleteFragment placeAutoComplete;

    private Geocoder geocoder;
    private List<Address> addresses;
    private static final String TAG = "GuideActivity";
    private static final float DEFAULT_ZOOM = 15f;

    //private LinkedList<> spots;
    private ArrayList<Spot> spots = new ArrayList<>();

    private Place selectedPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_activity);

        placeAutoComplete = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete);
        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                geoLocate(place);
                System.out.println("TEST");
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
                    //TODO Range Query or Number restriction
                    if(true){
                        Spot spot = documentSnapshot.toObject(Spot.class);
                        spots.add(spot);
                        Log.d(TAG, "Spots loaded: "+spot.getTitle());
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

        mapOverlay = new Dialog(this);
    }

    public void showMapOverlay(Spot spot){
        mapOverlay.setContentView(R.layout.map_overlay);
        TextView title = mapOverlay.findViewById(R.id.textView);
        title.setText(spot.getTitle());

        TextView date = mapOverlay.findViewById(R.id.text_date);
        date.setText(spot.getDate() + "\n"+spot.getTime());

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
        float f = results[0]/1000;
        distance.setText(""+f+" km");

        TextView address = mapOverlay.findViewById(R.id.text_address);
        address.setText(spot.getAddress());

        TextView info = mapOverlay.findViewById(R.id.text_info);
        info.setText(spot.getInfo());

        mapOverlay.show();
    }

    /**
     * qfunction to create all spot marker in the spots list
     * @param spots
     */
    private void setAllSpotMarker(ArrayList<Spot> spots){
        Log.d(TAG, "setAllSpotMarker: Liste in Marker Funktion mit Entry: "+spots.get(0).getTitle());
        for (int i = 0; i < spots.size(); i++) {
            LatLng pos = new LatLng(spots.get(i).getLatitude(), spots.get(i).getLongitude());
            mMap.addMarker(new MarkerOptions().position(pos).title(spots.get(i).getTitle()));
        }
    }

    private void onMapClicked(){

    }


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


    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.guide_map);
        mapFragment.getMapAsync(GuideActivity.this);

        CollectionReference spots = db.collection("spots");
        spots.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Spot spot = documentSnapshot.toObject(Spot.class);
                    System.out.println("SPOT______________: "+spot.getAddress());
                    LatLng pos = new LatLng(spot.getLatitude(), spot.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(pos).title(spot.getTitle()));
                }
            }
        });
    }


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


    private void buildAlertMessageNoGps() {
        Toast.makeText(this, "No location tracking enabled", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
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

    private void onMapClicked(LatLng latLng) {
        geocoder = new Geocoder(this, Locale.getDefault());
        try {

            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            Intent intent = new Intent(GuideActivity.this, CreateSpotActivity.class);
            intent.putExtra("Address", address);
            intent.putExtra("Lat", Double.toString(latLng.latitude));
            intent.putExtra("Lng", Double.toString(latLng.longitude));
            startActivity(intent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public void addSpot(View view){
        System.out.println("NEW");
        Intent intent = new Intent(GuideActivity.this, CreateSpotActivity.class);
        startActivity(intent);
    }

    public void join(View view){
        Toast.makeText(this, "Spot on!", Toast.LENGTH_SHORT).show();
    }

}

