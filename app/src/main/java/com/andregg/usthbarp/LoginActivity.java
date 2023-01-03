package com.andregg.usthbarp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    final String DATABASE_URL = "https://rock-sublime-371122-default-rtdb.firebaseio.com";
    TextInputEditText userNameField, passwordField;
    Button loginBtn;
    TextView register_tv;

    DatabaseReference databaseReference  = FirebaseDatabase.getInstance().getReferenceFromUrl(DATABASE_URL);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        register_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegisterActivity();
            }
        });
    }

    public void openRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void login() {
       String username = userNameField.getText().toString();
       String password = passwordField.getText().toString();

       if(username.equals("") || password.isEmpty()){
           Toast.makeText(this, "You must fill all the fileds", Toast.LENGTH_SHORT).show();
       }else{
            databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                // check if the user exists
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChild(username)){
                        // get original password
                        String orgPassword = snapshot.child(username).child("password").getValue(String.class);

                        if(orgPassword.equals(password)){
                            Toast.makeText(LoginActivity.this, "Successfully Logged in", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                        }else{
                            Toast.makeText(LoginActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(LoginActivity.this, "You must register first", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
       }

    }

    private void initViews() {
        userNameField = findViewById(R.id.username_et);
        passwordField = findViewById(R.id.password_et);
        loginBtn = findViewById(R.id.submit_btn);
        register_tv = findViewById(R.id.register_tv);
    }
}