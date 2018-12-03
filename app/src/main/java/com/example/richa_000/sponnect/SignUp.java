package com.example.richa_000.sponnect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    public void add_more(View view){

    }

    public void save(View view){
        Intent mIntent = new Intent(SignUp.this, Menu.class);
        startActivity(mIntent);
    }

}
