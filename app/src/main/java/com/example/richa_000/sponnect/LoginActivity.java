package com.example.richa_000.sponnect;

import android.content.Intent;
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

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private static final String KEY_MAILADDRESS = "mailaddress";
    private static final String KEY_PASSWORD = "password";

    private EditText editTextLogInMail;
    private EditText editTextLogInPassword;

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
                            mIntent.putExtra("Me", nickname);
                            mIntent.putExtra("gender", user.getGender().toString());
                            mIntent.putExtra("age", user.getAge());
                            startActivity(mIntent);
                            break;
                        } else{
                            Log.d(TAG, "Wrong Password");
                            Toast.makeText(LoginActivity.this, "Wrong Password! Try again!", Toast.LENGTH_LONG).show();
                        }

                    } else{
                        //Not a match
                    }

                }
                //TODO NANNI: mail address is not found but Toast will be shown anyway, IF needed
                //Did not find the mail address
                //Toast.makeText(LoginActivity.this, "Your Email does not exist. How about signing up?", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void sign_up(View view){
        Intent mIntent = new Intent(LoginActivity.this, SignUp.class);
        startActivity(mIntent);
    }

}

