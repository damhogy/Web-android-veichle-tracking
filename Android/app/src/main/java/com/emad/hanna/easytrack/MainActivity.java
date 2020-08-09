package com.emad.hanna.easytrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText user_id;
    private EditText etpassword;
    private Button Login;
    private CheckBox mRememberMe;
    private ProgressDialog loginProgress;
    //Fire base
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //call input and button  from layout
        user_id = findViewById(R.id.user_id);
        etpassword = findViewById(R.id.password);
        Login = findViewById(R.id.btn_login);
        loginProgress = new ProgressDialog(this);
        mRememberMe = findViewById(R.id.checkbox);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {

        }
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserLogin();

            }
        });
    }

    private void UserLogin() {
        final String email = user_id.getText().toString().trim();
        final String password = etpassword.getText().toString().trim();

        if (password.isEmpty() && email.isEmpty()) {
            etpassword.setError("Password is required...!");
            etpassword.requestFocus();
            user_id.setError("Email is required...!");
            user_id.requestFocus();
            return;
        } else if (email.isEmpty()) {
            user_id.setError("Email is required...!");
            user_id.requestFocus();
            return;
        } else if (password.isEmpty()) {
            etpassword.setError("Password is required...!");
            etpassword.requestFocus();
            return;
        }

        loginProgress.setMessage("Login is running ...!");
        loginProgress.show();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a valid email or password...!", Toast.LENGTH_SHORT).show();
                }
                loginProgress.dismiss();
            }
        });

    }
}



