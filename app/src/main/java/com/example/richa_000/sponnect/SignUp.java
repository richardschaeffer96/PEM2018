package com.example.richa_000.sponnect;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    private EditText editTextSignUpMail;
    private EditText editTextSignUpPassword;
    private EditText editTextSignUpNickname;
    private EditText editTextSignUpAge;
    private RadioGroup radioGroupGender;
    private RadioButton radioButtonGender;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextSignUpMail = findViewById(R.id.edit_email_signup);
        editTextSignUpPassword = findViewById(R.id.edit_password_signup);
        editTextSignUpNickname = findViewById(R.id.edit_nickname);
        editTextSignUpAge = findViewById(R.id.edit_age);
    }

    public void add_more(View view){

    }

    public void save(View view){

        Log.d(TAG, "Saving Data...");
        String SignUpMail = editTextSignUpMail.getText().toString();
        String SignUpPassword = editTextSignUpPassword.getText().toString();
        String SignUpNickname = editTextSignUpNickname.getText().toString();
        int SignUpAge;

        if(SignUpMail.isEmpty() || SignUpNickname.isEmpty() || SignUpPassword.isEmpty()) {
            Log.d(TAG, "Values were empty");
            Toast.makeText(SignUp.this, "Please fill out EMail, Nickname and Password", Toast.LENGTH_LONG).show();
        }
        else {
            if(editTextSignUpAge.getText().toString().isEmpty()){
                SignUpAge = 0;
            } else{
                SignUpAge = Integer.parseInt(editTextSignUpAge.getText().toString());
            };

            radioGroupGender = findViewById(R.id.radio_gender);
            int genderId = radioGroupGender.getCheckedRadioButtonId();
            Log.d(TAG, "Not selecting a Gender results in ID: "+genderId);
            radioButtonGender = findViewById(genderId);
            String SignUpGender = "";
            if(genderId < 0){
                SignUpGender = "-";
            } else {
                SignUpGender = radioButtonGender.getText().toString();
            }

            Log.d(TAG, "Creating User Document");
            final User user = new User(SignUpMail, SignUpNickname, SignUpPassword, SignUpGender, SignUpAge);

            usersRef.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(SignUp.this, "Data saved and logged in!\nHello "+user.getNickname(), Toast.LENGTH_LONG).show();
                    Intent mIntent = new Intent(SignUp.this, Menu.class);
                    startActivity(mIntent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignUp.this, "ERROR!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                }
            });
        }

    }

}
