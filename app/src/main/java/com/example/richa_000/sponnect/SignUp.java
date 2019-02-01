package com.example.richa_000.sponnect;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextSignUpMail = findViewById(R.id.edit_email_signup);
        editTextSignUpPassword = findViewById(R.id.edit_password_signup);
        editTextSignUpPassword2 = findViewById(R.id.edit_password_signup2);
        editTextSignUpNickname = findViewById(R.id.edit_nickname);
        editTextSignUpAge = findViewById(R.id.edit_age);
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
