package com.example.richa_000.sponnect;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.DocumentKey;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private static final String KEY_MAILADDRESS = "mailaddress";
    private static final String KEY_PASSWORD = "password";

    private EditText editTextLogInMail;
    private EditText editTextLogInPassword;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference users = db.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextLogInMail = findViewById(R.id.edit_email_login);
        editTextLogInPassword = findViewById(R.id.edit_password_login);
    }

    public void login(View view){
        String LogInMail = editTextLogInMail.getText().toString();
        String LogInPassword = editTextLogInPassword.getText().toString();

        //TODO NANNI: Understand how querying workd
        //Query q = users.whereEqualTo(KEY_MAILADDRESS, LogInMail);
        //QuerySnapshot qSnapshot = q.get();
        if (LogInMail.equals("N@nni-s.de")){
            if(LogInPassword.equals("test123")){
                Toast.makeText(LoginActivity.this, "Welcome Nanni", Toast.LENGTH_LONG).show();
            } else{
                Toast.makeText(LoginActivity.this, "Wrong Password! Try again!", Toast.LENGTH_LONG).show();
            }
        } else{
            Toast.makeText(LoginActivity.this, "Your Email does not exist. How about signing up?", Toast.LENGTH_LONG).show();
        }
    }

    public void sign_up(View view){
        Intent mIntent = new Intent(LoginActivity.this, SignUp.class);
        startActivity(mIntent);
    }

}

