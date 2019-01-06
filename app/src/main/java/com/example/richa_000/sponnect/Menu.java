package com.example.richa_000.sponnect;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Menu extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    //private final User me = new User();
    public ArrayList<Spot> mySpotList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TextView line1;
    private TextView line2;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference spotsRef = db.collection("spots");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Probably not the right place but getting the users nick here to display in toolbox
        String me = getIntent().getStringExtra("Me");
        String gender = getIntent().getStringExtra("gender");
        int age = getIntent().getIntExtra("age", -1);
        String info = gender + ", ("+age+")";
        line1 = findViewById(R.id.toolbarTextView1);
        line2 = findViewById(R.id.toolbarTextView2);
        line1.setText(me);
        line2.setText(info);

        /*ArrayList<SpotExample> exampleList = new ArrayList<>();
        exampleList.add(new SpotExample("Weihnachtsmarkt", "15.12.2018", "12:30"));
        exampleList.add(new SpotExample("Running Sushi", "15.12.2018", "17:45"));
        exampleList.add(new SpotExample("Schlittschuhlaufen", "16.12.2018", "13:00"));
        exampleList.add(new SpotExample("Kino", "16.12.2018", "18:00"));
        exampleList.add(new SpotExample("Stadtführung Zentrum", "17.12.2018", "14:30"));
        exampleList.add(new SpotExample("Asiatisches Buffet", "17.12.2018", "19:45"));
        */

        mySpotList.add(new Spot("Weihnachtsmarkt", "iwo draußen", "15.12.2018", "12:30",80.3,65.9));

        //list is always empty, doesn't work that way
        /*spotsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                    //TODO Range Query or Number restriction
                    if(true){
                        Spot spot = documentSnapshot.toObject(Spot.class);
                        mySpotList.add(spot);
                        Log.d(TAG, "onSuccess: Found Stuff in DB: "+spot.getTitle());
                    }

                }

            }
        });

        Log.d(TAG, "First List entry: "+mySpotList.toString());
        */
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new SpotAdapter(mySpotList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);



        //Map Button


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
                Intent mIntent = new Intent(Menu.this, SignUp.class);
                startActivity(mIntent);
                return true;
            case R.id.contacts:
                Intent mIntent2 = new Intent(Menu.this, Contacts.class);
                startActivity(mIntent2);
                return true;
            case R.id.home:
                Intent mIntent3 = new Intent(Menu.this, Menu.class);
                startActivity(mIntent3);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void seek(View view){

        if(isServicesOK()){
            Intent intent = new Intent(Menu.this, MapActivity.class);
            startActivity(intent);
        }
        // To do: Felix

    }

    public void guide(View view){
        if(isServicesOK()){
            Intent intent = new Intent(Menu.this, GuideActivity.class);
            startActivity(intent);
        }
    }





    // Felix Google Maps stuff
    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(Menu.this);

        if(available==ConnectionResult.SUCCESS){
            Log.d(TAG,"isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(Menu.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
