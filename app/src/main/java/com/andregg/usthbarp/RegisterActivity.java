package com.andregg.usthbarp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    final String DATABASE_URL = "https://rock-sublime-371122-default-rtdb.firebaseio.com";

    TextInputEditText nameField, emailField, passwordField, conPasswordField, matriculeField;
    Button registerBtn;
    DatabaseReference databaseReference  = FirebaseDatabase.getInstance().getReferenceFromUrl(DATABASE_URL);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

    }

    private void register() {
        String name =  nameField.getText().toString();
        String password =  passwordField.getText().toString();
        String conPassword =  conPasswordField.getText().toString();
        String email =  emailField.getText().toString();
        String matricule =  matriculeField.getText().toString();
        if(name.isEmpty() || password.isEmpty() || conPassword.isEmpty() || email.isEmpty() || matricule.isEmpty()){
            Toast.makeText(this, "You must fill all the fileds", Toast.LENGTH_SHORT).show();
        }else if(!password.equals( conPassword)){
            Toast.makeText(this, "Recheck your password", Toast.LENGTH_SHORT).show();
        }else{

            databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // check if register number already exist
                    if(snapshot.hasChild(matricule)){
                        Toast.makeText(RegisterActivity.this, "User exists already!", Toast.LENGTH_SHORT).show();
                    }else{
                        databaseReference.child("users").child(matricule).child("username").setValue(name);
                        databaseReference.child("users").child(matricule).child("password").setValue(password);
                        databaseReference.child("users").child(matricule).child("email").setValue(email);

                        Toast.makeText(RegisterActivity.this, "User registered Successfuly", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }

    private void initViews() {
        nameField = findViewById(R.id.edit_name);
        passwordField = findViewById(R.id.edit_password);
        emailField = findViewById(R.id.edit_email);
        conPasswordField = findViewById(R.id.edit_confirm_password);
        matriculeField = findViewById(R.id.edit_matricule);
        registerBtn = findViewById(R.id.register_btn);
    }
}