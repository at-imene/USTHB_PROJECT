package com.andregg.usthbarp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andregg.usthbarp.ui.LoginActivity;
import com.andregg.usthbarp.util.CaptureAct;
import com.andregg.usthbarp.util.Place;
import com.andregg.usthbarp.util.PlaceAdapter;
import com.andregg.usthbarp.util.SelectListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SelectListener {

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    SharedPreferences sharedpreferences;
    TextInputEditText search_bar;
    public static final String mypreference = "app-state";
    boolean mLocationPermissionGranted =false;
    RecyclerView recyclerView;
    PlaceAdapter placeAdapter;
//    SelectListener selectListener;
    ArrayList<Place> places;
    LinearLayout qrCode, MapLayout, LogoutLayout;
    TextView username;
    int CODE = 2;
    ActivityResultLauncher<ScanOptions> barlaucher;
    String QR_result= null;
    private final String GOOGLE_AR_LINK= "https://arvr.google.com/scene-viewer/1.0?file=";
    String nvb = "https://arvr.google.com/scene-viewer/1.0?file=https://firebasestorage.googleapis.com/v0/b/" +
        "ar-test2-5c517.appspot.com/o/nv_block.glb?alt=media&token=57dd465f-bbc5-40f7-b8ea-801ea5c4cf41";

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        barlaucher = registerForActivityResult(new ScanContract(), result -> {
            if(result.getContents() != null){
                openAr(result.getContents());
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setTitle("Result");
//                builder.setMessage(result.getContents());
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
//                {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i)
//                    {
//                        dialogInterface.dismiss();
//                    }
//                }).show();
//                QR_result = new String(result.getContents());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        qrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                scanCode();
//                openAr(modelURL);
            }
        });
//        ArBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
////                    File file = File.createTempFile("Chicken", "glb");
////                    Log.d("TAG", "file path : "+file.getPath());
//              //  https://arvr.google.com/scene-viewer/1.0?file=https://firebasestorage.googleapis.com/v0/b/mos-project-4ccbb.appspot.com/buvette.glb?alt=media&token=YOUR_TOKEN
//
//                    String nvb = "https://arvr.google.com/scene-viewer/1.0?file=https://firebasestorage.googleapis.com/v0/b/" +
//                            "ar-test2-5c517.appspot.com/o/nv_block.glb?alt=media&token=57dd465f-bbc5-40f7-b8ea-801ea5c4cf41";
//                    openAr(nvb);
//
//
//            }
//        });
        MapLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLocationPermissionGranted){
                    startActivity(new Intent(MainActivity.this, MapsActivity.class));
                }else{
                    getLocationPermission();
                }
            }
        });

        LogoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<Place> newP = new ArrayList<>();
                for(Place p: places){
                    if(p.getName().toLowerCase().contains(s.toString().toLowerCase())){
                        newP.add(p);
                    }
                }
                placeAdapter.setPlaces(newP);
                recyclerView.setAdapter(placeAdapter);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initViews() {
        db = FirebaseFirestore.getInstance();
        qrCode = findViewById(R.id.qrcode_layout);
        MapLayout = findViewById(R.id.map_layout);
        recyclerView = findViewById(R.id.recyle_view);
        LogoutLayout = findViewById(R.id.logout_layout);
        username = findViewById(R.id.username_tv);
        search_bar = findViewById(R.id.search_bar);
        initusername();
        places = new ArrayList<>();
        initPlaces();
        placeAdapter= new PlaceAdapter(places, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(placeAdapter);
//        recyclerView.setLayoutFrozen(false);
    }
    private void initusername(){
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        if (sharedpreferences.contains("name")) {
            username.setText(sharedpreferences.getString("name", "user"));
        }
    }
    //   ============ INIT ALL PLACES ============
    void initPlaces(){
//        int[] colors = {R.color.green, R.color.yellow, R.color.red};
        int[] colors = {1,2,3,4,5};
//        int[] images = {}
        String nvb = "https://arvr.google.com/scene-viewer/1.0?file=https://firebasestorage.googleapis.com/v0/b/" +
                "ar-test2-5c517.appspot.com/o/nv_block.glb?alt=media&token=57dd465f-bbc5-40f7-b8ea-801ea5c4cf41";
        places.add(new Place("Rectorat", "rectoratglb.glb", "36.7122971,3.1757376", R.drawable.rectora, colors[0]));
        places.add(new Place("Les Salles 100/200", "les_salle_100.glb", "36.7122436,3.1776667", R.drawable.salle_100, colors[1]));
        places.add(new Place("Les Salles 300/400", "les_salles_300.glb", "36.7122436,3.1776667", R.drawable.salle_300, colors[2]));
        places.add(new Place("Village", "village.glb", "36.7121803,3.176952", R.drawable.village, colors[3]));
        places.add(new Place("Faculte Info", "dept_info.glb", "36.7158507,3.1741626", R.drawable.fac_info1, colors[4]));
        places.add(new Place("Auditoriom", "auditorium.glb", "36.7147099,3.1745499", R.drawable.audit, colors[0]));
        places.add(new Place("Nouveau blocs", "nv_block.glb", "36.7094983,3.1806348", R.drawable.nvblock, colors[0]));
    }


    private void openAr(String Url) {
        String link = GOOGLE_AR_LINK+Url+ "?alt=media";
        System.out.println("============= "+link);
        Intent sceneViewerIntent = new Intent(Intent.ACTION_VIEW);
        Uri intentUri = Uri.parse(link)
                .buildUpon()
                .appendQueryParameter("mode", "ar_preferred")
                .appendQueryParameter("title", "Model3D")
                .build();

        sceneViewerIntent.setData(intentUri);
        sceneViewerIntent.setPackage("com.google.android.googlequicksearchbox");
        startActivity(sceneViewerIntent);
    }

    void logOut(){
        SharedPreferences.Editor editor = getSharedPreferences(mypreference, MODE_PRIVATE).edit();
        editor.remove("name");
        editor.commit();
        finish();
    }

    void scanCode(){
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volumn UP to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barlaucher.launch(options);
//        options.getCaptureActivity().
        if(QR_result!= null){

        openAr(nvb);
        }
    }

    //   ===========   VERIFY ALL PERMISSIONS ============
    void verifyPermissions()
    {
        String[] permission = {Manifest.permission.CAMERA};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), permission[0]) == PackageManager.PERMISSION_GRANTED){  // affichage
            Toast.makeText(this.getApplicationContext(), "Permission verified", Toast.LENGTH_LONG).show();
        }else{
            ActivityCompat.requestPermissions(this, permission, CODE);
        }
    }

    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    public void OnShowModelClicked(Place p) {
        openAr(p.getModelUrl());
    }

    @Override
    public void OnShowLocationClicked(Place p) {
        Log.d("TAG", "Location");
        Intent myIntent = new Intent(this, MapsActivity.class);
        Bundle b = new Bundle();
        b.putDouble(getResources().getString(R.string.latitude), p.getLatitude());
        b.putDouble(getResources().getString(R.string.longtitude), p.getLongitude());
        myIntent.putExtras(b);
        startActivity(myIntent);
    }
}