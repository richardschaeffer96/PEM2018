package com.example.richa_000.sponnect;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Class for the main menu / Dashboard
 */
public class Menu extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private User me = new User();
    public String userID;
    public ArrayList<Spot> mySpotList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TextView line1;
    private TextView line2;
    private ImageView profile;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference spotsRef = db.collection("spots");
    private CollectionReference usersRef = db.collection("users");

    private Typeface comfortaa_regular;
    private Typeface comfortaa_bold;
    private Typeface comfortaa_light;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        comfortaa_regular = Typeface.createFromAsset(this.getAssets(), "Comfortaa-Regular.ttf");
        comfortaa_bold = Typeface.createFromAsset(this.getAssets(), "Comfortaa-Bold.ttf");
        comfortaa_light = Typeface.createFromAsset(this.getAssets(), "Comfortaa-Light.ttf");

        TextView headline = findViewById(R.id.text_my_spots);
        Button button_explore = findViewById(R.id.button_guide);


        headline.setTypeface(comfortaa_bold);
        button_explore.setTypeface(comfortaa_bold);

        userID = getIntent().getStringExtra("id");

        //Who is logged in now?
        if (getIntent().getStringExtra("login") != null){
            getLoggedInUser(userID);
        } else {
            me = (User) getIntent().getSerializableExtra("user");
            setUserInfo(me);
        }

        spotsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){

                    Spot spot = documentSnapshot.toObject(Spot.class);
                    if(spot.getParticipants().containsKey(userID)){
                        Calendar c = Calendar.getInstance();
                        Date currentTime = c.getTime();
                        String spotDate = spot.getDate()+"-"+spot.getTime();
                        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy-HH:mm");
                        Date date = null;
                        try {
                            date = format.parse(spotDate);
                            c.setTime(date);
                            c.add(Calendar.DATE, 1);
                            if(currentTime.before(c.getTime())) {
                                mySpotList.add(spot);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

                mRecyclerView = findViewById(R.id.recyclerview);
                mRecyclerView.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(mRecyclerView.getContext());

                mAdapter = new SpotAdapter(mySpotList, userID, me);

                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mAdapter);

            }
        });

    }

    /**
     * gives the user who is logged in at the moment and saves it in global User variable me
     * userid is a String which is given by the LogInActivity's context
     * @param userId
     */
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

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * toolbar buttons are sending all needed information to next intent and start it
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                Intent mIntent = new Intent(Menu.this, EditProfile.class);
                mIntent.putExtra("id", userID);
                mIntent.putExtra("user", me);
                startActivity(mIntent);
                return true;
            case R.id.contacts:
                Intent mIntent2 = new Intent(Menu.this, Contacts.class);
                mIntent2.putExtra("id", userID);
                mIntent2.putExtra("user", me);
                startActivity(mIntent2);
                return true;
            case R.id.home:
                Intent mIntent3 = new Intent(Menu.this, Menu.class);
                mIntent3.putExtra("id", userID);
                mIntent3.putExtra("user", me);
                startActivity(mIntent3);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * linked to the explore button. Opens the map.
     * @param view
     */
    public void guide(View view){
        if(isServicesOK()){
            Intent intent = new Intent(Menu.this, GuideActivity.class);
            intent.putExtra("id", userID);
            intent.putExtra("user", me);
            startActivity(intent);
        }
    }

    /**
     * Is Checking if google services are working
     * @return
     */
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
