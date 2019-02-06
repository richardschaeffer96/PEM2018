package com.example.richa_000.sponnect;

import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
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

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                if(eMailDoesNotExist){
                    Toast.makeText(LoginActivity.this, "Your Email does not exist. How about signing up?", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void sign_up(View view){
        Intent mIntent = new Intent(LoginActivity.this, SignUp.class);
        startActivity(mIntent);
    }

}

