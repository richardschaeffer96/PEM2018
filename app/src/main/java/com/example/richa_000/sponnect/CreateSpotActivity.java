package com.example.richa_000.sponnect;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class CreateSpotActivity extends AppCompatActivity {

    private static final String TAG = "CreateSpotActivity";

    //private EditText address;
    private PlaceAutocompleteFragment placeAutoComplete;
    private EditText date;
    private EditText time;
    private EditText title;
    private EditText info;
    private User me;
    final Calendar myCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener datePicker;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference spotsRef = db.collection("spots");
    private CollectionReference usersRef = db.collection("users");

    private double lat;
    private double lng;
    private String address;
    private String userID;

    private Place selectedPlace;

    private Typeface comfortaa_regular;
    private Typeface comfortaa_bold;
    private Typeface comfortaa_light;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_spot);
        Intent intent = getIntent();
        userID = intent.getStringExtra("id");
        me = (User) getIntent().getSerializableExtra("user");
        System.out.println("userID is: -------------------: "+userID);

        comfortaa_regular = Typeface.createFromAsset(this.getAssets(), "Comfortaa-Regular.ttf");
        comfortaa_bold = Typeface.createFromAsset(this.getAssets(), "Comfortaa-Bold.ttf");
        comfortaa_light = Typeface.createFromAsset(this.getAssets(), "Comfortaa-Light.ttf");

        TextView text_headline = findViewById(R.id.textView_headline);
        TextView text_address = findViewById(R.id.tv_address);
        TextView text_title = findViewById(R.id.tv_title);
        TextView text_info = findViewById(R.id.tv_info);
        TextView text_date = findViewById(R.id.tv_date);
        TextView text_time = findViewById(R.id.tv_time);
        Button add_button = findViewById(R.id.button_add);

        text_headline.setTypeface(comfortaa_bold);
        text_title.setTypeface(comfortaa_regular);
        text_info.setTypeface(comfortaa_regular);
        text_date.setTypeface(comfortaa_regular);
        text_time.setTypeface(comfortaa_regular);
        add_button.setTypeface(comfortaa_regular);
        text_address.setTypeface(comfortaa_regular);

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
                address = getIntent().getStringExtra("Address");
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
        info = findViewById(R.id.et_info);
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        date.setText(sdf.format(myCalendar.getTime()));
    }



    public void save(View view) {
        if (selectedPlace != null) {
            lat = selectedPlace.getLatLng().latitude;
            lng = selectedPlace.getLatLng().longitude;
            address = (String) selectedPlace.getAddress();
        }

        if (title.getText().toString().isEmpty() || date.getText().toString().isEmpty() || time.getText().toString().isEmpty()) {
            Log.d(TAG, "Values were empty");
            Toast.makeText(CreateSpotActivity.this, "Please give your Spot a Name, a Time and a Date!", Toast.LENGTH_LONG).show();
        } else {
            final Spot spot = new Spot(title.getText().toString(), info.getText().toString(), address, date.getText().toString(), time.getText().toString(), userID, lat, lng);
            HashMap<String, Integer> participants = spot.getParticipants();
            participants.put(userID, 0);
            spot.setParticipants(participants);
            spotsRef.add(spot).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    String id = documentReference.getId();
                    DocumentReference spot = spotsRef.document(id);
                    spot.update("id", id);
                    Toast.makeText(CreateSpotActivity.this, "Spot saved", Toast.LENGTH_LONG).show();
                    usersRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                User user = documentSnapshot.toObject(User.class);
                                if (userID.equals(user.getId())) {
                                    DocumentReference refUser = usersRef.document(userID);
                                    HashMap<String, Boolean> spots = user.getMySpots();
                                    spots.put(id, true);
                                    refUser.update("mySpots", spots);
                                }
                            }
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CreateSpotActivity.this, "ERROR!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                }
            });

            Intent mIntent = new Intent(CreateSpotActivity.this, GuideActivity.class);
            mIntent.putExtra("id", userID);
            mIntent.putExtra("user", me);
            startActivity(mIntent);

        }

    }

}
