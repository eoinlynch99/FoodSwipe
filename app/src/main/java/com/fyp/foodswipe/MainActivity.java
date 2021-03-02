package com.fyp.foodswipe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void logout(View view) {

        FirebaseAuth.getInstance().signOut();;
        startActivity(new Intent(getApplicationContext(), login.class));
        finish();
    }

    public void map(View view) {

        startActivity(new Intent(getApplicationContext(), map.class));
        finish();
    }

    public void swipe(View swipe) {

        startActivity(new Intent(getApplicationContext(), SwipePage.class));
        finish();
    }
}