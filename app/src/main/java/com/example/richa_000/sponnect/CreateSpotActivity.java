package com.example.richa_000.sponnect;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CreateSpotActivity extends AppCompatActivity {

    private static final String TAG = "CreateSpotActivity";

    //private EditText address;
    private PlaceAutocompleteFragment placeAutoComplete;
    private EditText date;
    private EditText time;
    private EditText title;
    final Calendar myCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener datePicker;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference spotsRef = db.collection("spots");

    private double lat;
    private double lng;
    private String adress;

    private static final String KEY_ADDRESS = "address";
    private static final String KEY_TIME = "time";
    private static final String KEY_DATE = "date";
    private static final String KEY_LATLNG= "latlng";


    private Place selectedPlace;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_spot);
        Intent intent = getIntent();

        placeAutoComplete = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.et_address);

        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                System.out.println("PLACE SELECTED");
                selectedPlace = place;
            }

            @Override
            public void onError(Status status) {

            }
        });


        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("Address")) {
                System.out.println("THERE ARE EXTRAS");
                placeAutoComplete.setText(getIntent().getStringExtra("Address"));
                lat =  Double.parseDouble(getIntent().getStringExtra("Lat"));
                lng =  Double.parseDouble(getIntent().getStringExtra("Lng"));
                adress = getIntent().getStringExtra("Address");
            }
        }

        date = findViewById(R.id.et_date);

        datePicker = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                System.out.println("TEST: Test");
                new DatePickerDialog(CreateSpotActivity.this, datePicker, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        time = findViewById(R.id.et_time);
        time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(CreateSpotActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String min=""+selectedMinute;
                        String h=""+selectedHour;
                        if (selectedMinute<10){
                            min = "0"+selectedMinute;
                        }
                        if (selectedHour<10){
                            h = "0"+selectedHour;
                        }
                        time.setText( h + ":" + min);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        title = findViewById(R.id.et_title);
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        date.setText(sdf.format(myCalendar.getTime()));
    }



    public void save(View view){
        if(selectedPlace!=null){
            lat = selectedPlace.getLatLng().latitude;
            lng = selectedPlace.getLatLng().longitude;
            adress = (String) selectedPlace.getAddress();
        }

        final Spot spot = new Spot(title.getText().toString(),adress, date.getText().toString(), time.getText().toString(), lat, lng);

        spotsRef.add(spot).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(CreateSpotActivity.this, "Spot saved", Toast.LENGTH_LONG).show();
                Intent mIntent = new Intent(CreateSpotActivity.this, GuideActivity.class);
                startActivity(mIntent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateSpotActivity.this, "ERROR!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, e.toString());
            }
        });


        /*
        int spots = (int) (Math.random()*100);

        String SpotAddress = placeAutoComplete.toString();
        String SpotDate = date.getText().toString();
        String SpotTime = time.getText().toString();
        String SpotLatLng = spotLatLng;

        Map<String, Object> spot = new HashMap<>();
        spot.put(KEY_ADDRESS, SpotAddress);
        spot.put(KEY_DATE, SpotDate);
        spot.put(KEY_TIME, SpotTime);
        spot.put(KEY_LATLNG, SpotLatLng);


        db.collection("spots").document("spot"+spots).set(spot)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Logging in
                        Toast.makeText(CreateSpotActivity.this, "Data saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateSpotActivity.this, "ERROR!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
*/
        Intent mIntent = new Intent(CreateSpotActivity.this, Menu.class);
        startActivity(mIntent);
    }



}
