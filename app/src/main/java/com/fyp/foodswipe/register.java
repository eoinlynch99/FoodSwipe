package com.fyp.foodswipe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class register extends AppCompatActivity {
    // setting variables for the fields
    EditText mFirstName, mSurName, mEmail, mPassword;

    // setting variables for buttons
    Button mRegisterBtn;
    TextView mLoginBtn;
    ProgressBar progressBar;

    // creating a firebase auth variable
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirstName = findViewById(R.id.firstName);
        mSurName = findViewById(R.id.surName);
        mPassword = findViewById(R.id.userPass);
        mEmail = findViewById(R.id.userEmail);

        mRegisterBtn = findViewById(R.id.register_btn);
        mLoginBtn = findViewById(R.id.loginPG_btn);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();


        // checking if user is already logged in
        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        mRegisterBtn.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email must be entered!");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password must be entered!");
                }

                if(password.length() < 6){
                    mPassword.setError("Password must be at least 6 characters long");
                }

                progressBar.setVisibility(View.VISIBLE);

                // registering the user

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(register.this, "User Created.", Toast.LENGTH_SHORT).show();
                            String userID = mAuth.getCurrentUser().getUid();
                            // registering to realtime db
                            DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("email");
                            userDb.setValue(email);
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }

                        else
                        {
                            Toast.makeText(register.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),login.class));
            }
        });
    }
}