package com.example.richa_000.sponnect;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText editTextLogInMail;
    private EditText editTextLogInPassword;

    private boolean eMailDoesNotExist = false;

    private TextView text_logo;
    private TextView text_email;
    private TextView text_password;
    private Typeface comfortaa_regular;
    private Typeface comfortaa_bold;
    private Typeface comfortaa_light;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("users");

    private Button login_button;
    private Button signUp_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        comfortaa_regular = Typeface.createFromAsset(this.getAssets(), "Comfortaa-Regular.ttf");
        comfortaa_bold = Typeface.createFromAsset(this.getAssets(), "Comfortaa-Bold.ttf");
        comfortaa_light = Typeface.createFromAsset(this.getAssets(), "Comfortaa-Light.ttf");

        text_logo = findViewById(R.id.text_logo);
        text_email = findViewById(R.id.text_email);
        text_password = findViewById(R.id.text_password);
        login_button = findViewById(R.id.button_login);
        signUp_button = findViewById(R.id.button_signup);

        text_logo.setTypeface(comfortaa_bold);
        text_email.setTypeface(comfortaa_regular);
        text_password.setTypeface(comfortaa_regular);
        login_button.setTypeface(comfortaa_bold);
        signUp_button.setTypeface(comfortaa_bold);

        editTextLogInMail = findViewById(R.id.edit_email_login);
        editTextLogInPassword = findViewById(R.id.edit_password_login);
    }

    public void login(View view){
        final String LogInMail = editTextLogInMail.getText().toString();
        final String LogInPassword = editTextLogInPassword.getText().toString();

        usersRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                    String id = documentSnapshot.getId();
                    User user = documentSnapshot.toObject(User.class);

                    String eMail = user.geteMail();
                    String nickname = user.getNickname();
                    String password = user.getPassword();

                    Log.d(TAG, "Tried logging in: "+nickname);

                    if (eMail.equals(LogInMail)){
                        if(password.equals(LogInPassword)){
                            String data = "Hello: " + nickname;
                            Log.d(TAG,"Found User: "+nickname);
                            Toast.makeText(LoginActivity.this, data, Toast.LENGTH_LONG).show();
                            Intent mIntent = new Intent(LoginActivity.this, Menu.class);
                            mIntent.putExtra("id", id);
                            mIntent.putExtra("login", "login");
                            startActivity(mIntent);
                            break;
                        } else{
                            Log.d(TAG, "Wrong Password");
                            Toast.makeText(LoginActivity.this, "Wrong Password! Try again!", Toast.LENGTH_LONG).show();
                        }

                    } else{
                        //Not a match
                        eMailDoesNotExist = true;
                    }

                }
                //Did not find the mail address
                //if(eMailDoesNotExist){
                //    Toast.makeText(LoginActivity.this, "Your Email does not exist. How about signing up?", Toast.LENGTH_LONG).show();
                //}
            }
        });

    }

    public void sign_up(View view){
        Intent mIntent = new Intent(LoginActivity.this, SignUp.class);
        startActivity(mIntent);
    }

}

