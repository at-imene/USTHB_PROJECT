package com.andregg.usthbarp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.andregg.usthbarp.MainActivity;
import com.andregg.usthbarp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    public static final String mypreference = "app-state";

    final String DATABASE_URL = "https://rock-sublime-371122-default-rtdb.firebaseio.com/";
    TextInputEditText userNameField, passwordField;
    Button loginBtn;
    TextView register_tv;
    DatabaseReference databaseReference ;
//    FirebaseDatabase mAuth;

    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loginBtn.setOnClickListener(v -> login());
        register_tv.setOnClickListener(v -> openRegisterActivity());
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        if (sharedpreferences.contains("name")) {
            openMainActivity();
        }
    }

    public void openRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void login() {
       String matricule = Objects.requireNonNull(userNameField.getText()).toString();
       String password = Objects.requireNonNull(passwordField.getText()).toString();

       if(matricule.equals("") || password.isEmpty()){
           Toast.makeText(this, "You must fill all the fileds", Toast.LENGTH_SHORT).show();
       }else{
           System.out.println(databaseReference);
           db.collection("Users")
                   .whereEqualTo("matricule", matricule)
                   .whereEqualTo("password", password)
                   .get()
                   .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<QuerySnapshot> task) {
                           String name = "";
                           if (task.isSuccessful()) {
                               for(QueryDocumentSnapshot document : task.getResult()){
                                   if (document != null) {
                                       name = document.getString("name");
                                       Log.i("LOGGER","name "+name);
                                       Toast.makeText(LoginActivity.this, "Successfully Logged in", Toast.LENGTH_SHORT).show();
                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                        editor.putString("name", name);
                                        editor.commit();
                                        break;
                                   } else {
                                       Log.d("LOGGER", "No such document");
                                   }
                            }
                           } else {
                               Log.d("LOGGER", "get failed with ", task.getException());
                             }

                           if(!name.isEmpty()){
                               openMainActivity();
                           }


                       }
                   });
       }

    }

    private void openMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private void initViews() {
        userNameField = findViewById(R.id.username_et);
        passwordField = findViewById(R.id.password_et);
        loginBtn = findViewById(R.id.submit_btn);
        register_tv = findViewById(R.id.register_tv);
        databaseReference  = FirebaseDatabase.getInstance().getReferenceFromUrl(DATABASE_URL);
        db= FirebaseFirestore.getInstance();
    }
}