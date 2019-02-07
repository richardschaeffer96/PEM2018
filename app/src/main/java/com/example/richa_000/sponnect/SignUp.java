package com.example.richa_000.sponnect;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class SignUp extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    private static int PICK_IMAGE = 100;
    Uri imageUri;
    ImageView profile_picture;

    private EditText editTextSignUpMail;
    private EditText editTextSignUpPassword;
    private EditText editTextSignUpPassword2;
    private EditText editTextSignUpNickname;
    private EditText editTextSignUpAge;
    private RadioGroup radioGroupGender;
    private RadioButton radioButtonGender;
    private String SignUpGender = null;
    private String SignUpAge;

    private Typeface comfortaa_regular;
    private Typeface comfortaa_bold;
    private Typeface comfortaa_light;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextSignUpMail = findViewById(R.id.edit_email_edit);
        editTextSignUpPassword = findViewById(R.id.edit_password_edit);
        editTextSignUpPassword2 = findViewById(R.id.edit_password_edit2);
        editTextSignUpNickname = findViewById(R.id.edit_nickname_edit);
        editTextSignUpAge = findViewById(R.id.edit_age);

        comfortaa_regular = Typeface.createFromAsset(this.getAssets(), "Comfortaa-Regular.ttf");
        comfortaa_bold = Typeface.createFromAsset(this.getAssets(), "Comfortaa-Bold.ttf");
        comfortaa_light = Typeface.createFromAsset(this.getAssets(), "Comfortaa-Light.ttf");

        TextView text_headline_signup = findViewById(R.id.text_editprofile);
        TextView text_email = findViewById(R.id.text_email);
        TextView text_nickname = findViewById(R.id.text_nickname);
        TextView text_password = findViewById(R.id.text_password);
        TextView text_password2 = findViewById(R.id.text_password2);
        TextView text_age = findViewById(R.id.text_age);
        TextView text_gender = findViewById(R.id.text_gender);
        Button next_button = findViewById(R.id.button_next);
        RadioButton male = findViewById(R.id.radioButton_male);
        RadioButton female = findViewById(R.id.radioButton_female);
        RadioButton other = findViewById(R.id.radioButton_other);

        text_headline_signup.setTypeface(comfortaa_bold);
        text_email.setTypeface(comfortaa_regular);
        text_nickname.setTypeface(comfortaa_regular);
        text_password.setTypeface(comfortaa_regular);
        text_password2.setTypeface(comfortaa_regular);
        text_age.setTypeface(comfortaa_regular);
        text_gender.setTypeface(comfortaa_regular);
        next_button.setTypeface(comfortaa_regular);
        male.setTypeface(comfortaa_bold);
        female.setTypeface(comfortaa_bold);
        other.setTypeface(comfortaa_bold);

    }


    public void next(View view){

        Log.d(TAG, "Next...");
        String SignUpMail = editTextSignUpMail.getText().toString();
        String SignUpPassword = editTextSignUpPassword.getText().toString();
        String SignUpPassword2 = editTextSignUpPassword2.getText().toString();
        String SignUpNickname = editTextSignUpNickname.getText().toString();

        if(SignUpMail.isEmpty() || SignUpNickname.isEmpty() || SignUpPassword.isEmpty()) {
            Log.d(TAG, "Values were empty");
            Toast.makeText(SignUp.this, "Please fill out EMail, Nickname and Password", Toast.LENGTH_LONG).show();
        }
        else {
            if(editTextSignUpAge.getText().toString().isEmpty()){
                Toast.makeText(SignUp.this, "Please fill out Age", Toast.LENGTH_LONG).show();
            } else{
                SignUpAge = editTextSignUpAge.getText().toString();
            };
            if(SignUpPassword.equals(SignUpPassword2)){

                radioGroupGender = findViewById(R.id.radio_gender);
                int genderId = radioGroupGender.getCheckedRadioButtonId();
                Log.d(TAG, "Not selecting a Gender results in ID: " + genderId);
                radioButtonGender = findViewById(genderId);

                if (genderId < 0) {
                    SignUpGender = null;
                    Toast.makeText(SignUp.this, "Please select gender", Toast.LENGTH_LONG).show();
                } else {
                    SignUpGender = radioButtonGender.getText().toString();
                    Intent mIntent = new Intent(SignUp.this, SignUp2.class);
                    mIntent.putExtra("nickname", SignUpNickname);
                    mIntent.putExtra("password", SignUpPassword);
                    mIntent.putExtra("gender", SignUpGender);
                    mIntent.putExtra("mail", SignUpMail);
                    mIntent.putExtra("age", SignUpAge);
                    startActivity(mIntent);
                }

            }else {
                Toast.makeText(SignUp.this, "Passwords do not match", Toast.LENGTH_LONG).show();
            }

        }

    }



}
