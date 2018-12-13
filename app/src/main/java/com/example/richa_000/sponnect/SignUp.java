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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    private static final String TAG = "SignUp";

    private static final String KEY_MAILADDRESS = "mailaddress";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_NICKNAME = "nickname";
    private static final String KEY_AGE = "age";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_SM_FACEBOOK = "SM-facebook";

    private EditText editTextSignUpMail;
    private EditText editTextSignUpPassword;
    private EditText editTextSignUpNickname;
    private EditText editTextSignUpAge;
    private RadioGroup radioGroupGender;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        int members = (int) (Math.random()*100);

        String SignUpMail = editTextSignUpMail.getText().toString();
        String SignUpPassword = editTextSignUpPassword.getText().toString();
        String SignUpNickname = editTextSignUpNickname.getText().toString();
        int SignUpAge = Integer.parseInt(editTextSignUpAge.getText().toString());

        String SignUpGender = "undefined";
        radioGroupGender = (RadioGroup) findViewById(R.id.radio_gender);
        int genderId = radioGroupGender.getCheckedRadioButtonId();

        switch(genderId){
            //No fucking clue, why these numbers are that way...
            case 2131165320: SignUpGender = "m"; break;
            case 2131165319: SignUpGender = "f"; break;
            case 2131165318: SignUpGender = "o"; break;
            default: Toast.makeText(SignUp.this, "Your Gender ID is:"+genderId, Toast.LENGTH_LONG).show();
        }

        User user = new User(SignUpMail, SignUpNickname, SignUpPassword, SignUpGender, SignUpAge);
        /*
        Map<String, Object> user = new HashMap<>();
        user.put(KEY_MAILADDRESS, SignUpMail);
        user.put(KEY_PASSWORD, SignUpPassword);
        user.put(KEY_NICKNAME, SignUpNickname);
        user.put(KEY_AGE, SignUpAge);
        user.put(KEY_GENDER, SignUpGender);
        */

        db.collection("users").document("user"+members).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Logging in
                        Toast.makeText(SignUp.this, "Data saved", Toast.LENGTH_SHORT).show();
                        Toast.makeText(SignUp.this, "Logged In!", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUp.this, "ERROR!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });

        Intent mIntent = new Intent(SignUp.this, Menu.class);
        startActivity(mIntent);
    }

}
