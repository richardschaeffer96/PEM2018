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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private static final String KEY_MAILADDRESS = "mailaddress";
    private static final String KEY_PASSWORD = "password";

    private EditText editTextLogInMail;
    private EditText editTextLogInPassword;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextLogInMail = findViewById(R.id.edit_email);
        editTextLogInPassword = findViewById(R.id.edit_password);
    }

    public void login(View view){
        String LogInMail = editTextLogInMail.getText().toString();
        String LogInPassword = editTextLogInPassword.getText().toString();

        Map<String, Object> note = new HashMap<>();
        note.put(KEY_MAILADDRESS, LogInMail);
        note.put(KEY_PASSWORD, LogInPassword);

        db.collection("Notebook").document("My first note").set(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Logging in
                        Toast.makeText(LoginActivity.this, "Data saved", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "ERROR!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }

    public void sign_up(View view){
        Intent mIntent = new Intent(LoginActivity.this, SignUp.class);
        startActivity(mIntent);
    }

}

