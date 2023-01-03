package com.andregg.usthbarp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    boolean mLocationPermissionGranted =false;

    Button QRCodeBtn, ArBtn, MapsBtn;
    int CODE = 2;
    ActivityResultLauncher<ScanOptions> barlaucher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        barlaucher = registerForActivityResult(new ScanContract(), result -> {
            if(result.getContents() != null){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Result");
                builder.setMessage(result.getContents());
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.dismiss();
                    }
                }).show();
                openAr(result.getContents());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        QRCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                scanCode();
               // openAr(modelURL);
            }
        });
        ArBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                    File file = File.createTempFile("Chicken", "glb");
//                    Log.d("TAG", "file path : "+file.getPath());
              //  https://arvr.google.com/scene-viewer/1.0?file=https://firebasestorage.googleapis.com/v0/b/mos-project-4ccbb.appspot.com/buvette.glb?alt=media&token=YOUR_TOKEN

                    String nvb = "https://arvr.google.com/scene-viewer/1.0?file=https://firebasestorage.googleapis.com/v0/b/" +
                            "ar-test2-5c517.appspot.com/o/nv_block.glb?alt=media&token=57dd465f-bbc5-40f7-b8ea-801ea5c4cf41";
                    openAr(nvb);


            }
        });
        MapsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLocationPermissionGranted){
                    startActivity(new Intent(MainActivity.this, MapsActivity.class));
                }else{
                    getLocationPermission();
                }
            }
        });
    }

    private void initViews() {
        QRCodeBtn = findViewById(R.id.qrcode_btn);
        ArBtn = findViewById(R.id.ar_btn);
        MapsBtn = findViewById(R.id.maps_btn);
    }


    private void openAr(String Url) {

        //   String p = "https://arvr.google.com/scene-viewer/1.0?file=https://firebasestorage.googleapis.com/v0/b/ar-test2-5c517.appspot.com/o/Chicken.glb?alt=media" ;
        Intent sceneViewerIntent = new Intent(Intent.ACTION_VIEW);
        Uri intentUri = Uri.parse(Url)
                .buildUpon()
                .appendQueryParameter("mode", "ar_preferred")
                .appendQueryParameter("title", "Model3D")
                .build();


        sceneViewerIntent.setData(intentUri);
        sceneViewerIntent.setPackage("com.google.android.googlequicksearchbox");
        startActivity(sceneViewerIntent);
    }


    void scanCode(){
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volumn UP to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barlaucher.launch(options);
    }

    void verifyPermissions()
    {
        String[] permission = {Manifest.permission.CAMERA};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), permission[0]) == PackageManager.PERMISSION_GRANTED){  // affichage
            Toast.makeText(this.getApplicationContext(), "Permission verified", Toast.LENGTH_LONG).show();
        }else{
            ActivityCompat.requestPermissions(this, permission, CODE);
        }
    }


    // MAP AND GPS PERMISION AND AVAILABILITY
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

}