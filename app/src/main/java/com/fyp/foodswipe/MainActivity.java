package com.fyp.foodswipe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();;
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    } // end logout

    public void map(View view) {

        startActivity(new Intent(getApplicationContext(), MapsActivity.class));
        finish();
    } // end map

    public void swipe(View swipe) {

        startActivity(new Intent(getApplicationContext(), SwipeActivity.class));
        finish();
    } // end swipe

    // back button navigation
    @Override
    public void onBackPressed() {
        finish();
    } // end onBackPressed
} // end MainActivity