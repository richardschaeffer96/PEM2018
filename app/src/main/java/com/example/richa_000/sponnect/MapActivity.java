package com.example.richa_000.sponnect;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{

    //TODO: DELETE!?!?!?!?!?!

    private static final int REQUEST_LOCATION = 1;
    private LocationManager locationManager;
    private Button btnGetCurrentLocation;
    private GoogleMap mMap;


    private Geocoder geocoder;
    private List<Address> addresses;
    private static final String TAG = "MapActivity";
    private static final float DEFAULT_ZOOM = 15f;

    private List<Spot> spots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_activity);

        spots = new LinkedList<>();

        Spot s1 = new Spot("Spot1","TEst","Josef-Retzer-Straße 29 81241 München", "12.12.12", "12:12",48.142330, 11.463862);
        Spot s2 = new Spot("Spot2","TEst","Weinbergerstraße 50A 81241 München", "12.12.12","12:12",48.140475, 11.465333);
        Spot s3 = new Spot("Spot3","TEst","Georg-Jais-Straße 1 81241 München", "12.12.12", "12:12",48.141349, 11.468042);

        spots.addAll(Arrays.asList(s1,s2,s3));

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        initMap();

        btnGetCurrentLocation = findViewById(R.id.btnGetCurrentLocation);
        btnGetCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });

    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }


    private void getLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
        else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                    (MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(this, "No location tracking enabled", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
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
        Toast.makeText(this,"No location tracking enabled",Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;
        getLocation();

        for(int i=0; i<spots.size(); i++){
            LatLng pos = new LatLng(spots.get(i).getLatitude(), spots.get(i).getLongitude());
            mMap.addMarker(new MarkerOptions().position(pos).title(spots.get(i).getTitle()));
        }

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
                getLocationData(latLng);
            }
        });

    }

    private void markerClicked(Marker marker) {
        Toast.makeText(MapActivity.this, "You clicked on Marker "+marker.getTitle(), Toast.LENGTH_SHORT).show();
    }

    private void getLocationData(LatLng latLng) {
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        }
        catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }
}
