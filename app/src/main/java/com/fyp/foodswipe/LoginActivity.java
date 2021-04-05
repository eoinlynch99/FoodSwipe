package com.fyp.foodswipe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText mEmail, mPassword;
    Button mLoginBtn;
    TextView mCreateBtn;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.userEmail);
        mPassword = findViewById(R.id.userPass);
        fAuth = FirebaseAuth.getInstance().getInstance();
        mLoginBtn = findViewById(R.id.login_btn);
        mCreateBtn = findViewById(R.id.registPg_btn);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                // password and email error detection
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email must be entered!");
                    return;
                } // end if
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password must be entered!");
                } // end if
                if (password.length() < 6) {
                    mPassword.setError("Password must be at least 6 characters long");
                } // end if


                // logs the user in
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(LoginActivity.this, "Logged in.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        } // end  if
                        else
                        {
                            Toast.makeText(LoginActivity.this, "Email or Password are incorrect.", Toast.LENGTH_SHORT).show();
                        } // end else
                    } // end onComplete
                }); // end signInWithEmailAndPassword
            } // end onClick
        }); // end mLoginBtn.setOnClickListener

        // button to change user to register page
        mCreateBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

    }
}

