package com.andregg.usthbarp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.andregg.usthbarp.util.Place;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback{

    private static final float DEFAULT_ZOOM = 15f;
    private GoogleMap mMap;
    FusedLocationProviderClient mFusedLocationClient;
    private static final String TAG = "MapsActivity";

    private ClusterManager<ClusterMarker> mClusterManager;
    private MyClusterManagerRenderer mClusterManagerRenderer;
    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();
    Intent myIntent;
    Double latitude, longtitude;
    LatLng mUserPosition;
    private GeoApiContext mGeoApiContext= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        myIntent = getIntent();
        Bundle b = myIntent.getExtras();

        latitude = myIntent.getDoubleExtra(getResources().getString(R.string.latitude), 0.0);
        longtitude = myIntent.getDoubleExtra(getResources().getString(R.string.longtitude), 0.0);

//        if(mGeoApiContext == null){
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getResources().getString(R.string.MAP_API_KEY))
                    .build();
        //}

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(latitude != 0.0 && longtitude != 0.0){
            addMapMarkers();
            moveCamera(new LatLng(latitude, longtitude), DEFAULT_ZOOM);
            mMap.setMyLocationEnabled(true);
        }else{
        mFusedLocationClient =  LocationServices.getFusedLocationProviderClient(this);

        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                // get User location
                Location location = task.getResult();
                if (location == null) {
                    Toast.makeText(MapsActivity.this, "Cannot get user location", Toast.LENGTH_SHORT).show();
                } else {
                    mUserPosition = new LatLng(location.getLatitude(), location.getLongitude());
                    addMapMarkers();
                    moveCamera(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM);
                    mMap.setMyLocationEnabled(true);
//                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                }
            }
            });
        };
    }

    private void moveCamera(LatLng latLng, float zoom){
//       LatLng l = new LatLng(36.7112814,3.1801664);

        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Your location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    @SuppressLint("PotentialBehaviorOverride")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addMapMarkers(){

        if(mMap != null){

            if(mClusterManager == null){
                mClusterManager = new ClusterManager<ClusterMarker>(this.getApplicationContext(), mMap);
            }
            if(mClusterManagerRenderer == null){
                mClusterManagerRenderer = new MyClusterManagerRenderer(
                        this,
                        mMap,
                        mClusterManager
                );
                mClusterManager.setRenderer(mClusterManagerRenderer);
            }
            ArrayList<Place> places = new ArrayList<Place>();

            places.add(new Place("Rectorat", "36.7122971,3.1757376", R.drawable.rectora));
            places.add(new Place("Village", "36.7121803,3.176952", R.drawable.village));
            places.add(new Place("Salles 100/200", "36.7121164,3.1791816", R.drawable.salle_100));
            places.add(new Place("Salles 300/400", "36.7112814,3.1801664", R.drawable.salle_300));
            places.add(new Place("Faculte Info", "36.7158507,3.1741626", R.drawable.fac_info1));
            places.add(new Place("Nouveau block", "36.7094983,3.1806348", R.drawable.nvblock));
            places.add(new Place("Auditoriom", "36.7147099,3.1745499", R.drawable.audit));


            places.forEach(p ->{
                try{
                ClusterMarker newClusterMarker = new ClusterMarker(new LatLng(p.getLatitude(), p.getLongitude()), p.getName(), p.getName(), p.getImage());
                mClusterManager.addItem(newClusterMarker);
                mClusterMarkers.add(newClusterMarker);

                }catch (NullPointerException e){
                    Log.e(TAG, "addMapMarkers: NullPointerException: " + e.getMessage() );
                }
            });
            mClusterManager.cluster();
        }
    }

}

      