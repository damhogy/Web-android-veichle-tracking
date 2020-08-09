package com.emad.hanna.easytrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.emad.hanna.easytrack.directionhelpers.FetchURL;
import com.emad.hanna.easytrack.directionhelpers.TaskLoadedCallback;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback
        , LocationListener, TaskLoadedCallback {

    public static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 100;
    MyLocationProvider locationProvider;
    MapView mapView;
    private GoogleMap mMap;

    Toolbar toolbar;

    MarkerOptions start_Marker , end_Marker;
    Polyline currentPolyline;
    private static LatLng startRoute;
    private static LatLng endRoute;

    //firebase Logout
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    //retrieve start and end Route for Direction
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    FirebaseDatabase mDatabase;
    DatabaseReference mDatabaseReference,mDataRef;
    String userKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (canAccessLocation()) {
            //call your function
            getUserLocation();
        } else {
            //request permission
            requestLocationPermission();
        }


        //Firebase Logout
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //retrieve start and end Route for Direction

        mFirebaseAuth= FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference();
        mDataRef =mDatabase.getReference();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        userKey = mFirebaseUser.getUid();

    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
         String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }
    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        // and next place it, on bottom right (as Google Maps app)
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                locationButton.getLayoutParams();
        // position on right bottom above zoomLayout
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        layoutParams.addRule(RelativeLayout.ABOVE, 0);
        layoutParams.setMargins(0, 130, 0, 0);

        setCurrentLocationOnMap();

       mDatabaseReference.child("Driver").child(userKey).child("Route").addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               //Start Route
               Double St_Latitude = dataSnapshot.child("St_Lat").getValue(Double.class);
               Double St_Longitude = dataSnapshot.child("St_Long").getValue(Double.class);
               startRoute= new LatLng(St_Latitude,St_Longitude);
               //End Route
               Double End_Latitude = dataSnapshot.child("End_Lat").getValue(Double.class);
               Double End_Longitude = dataSnapshot.child("End_Long").getValue(Double.class);
               endRoute= new LatLng(End_Latitude,End_Longitude);
               start_Marker = new MarkerOptions().position(startRoute).title("Start Route");
               end_Marker = new MarkerOptions().position(endRoute).title("End Route");
               //Direction
               String url =getUrl(start_Marker.getPosition(),end_Marker.getPosition(),"driving");
               new FetchURL(MapsActivity.this).execute(url, "driving");
               //Add Marker start and end on Map
               mMap.addMarker(end_Marker);
               mMap.addMarker(start_Marker);
           }
           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }

    Marker currentMarker = null;
    public void setCurrentLocationOnMap() {
        if (myLocation == null) {
            return;
        }
        if (mMap == null) {
            return;
        }
        LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        if (currentMarker == null)
            currentMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                    .title("my location"));
        else currentMarker.setPosition(latLng);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
        storeLocation();

    }
    private void storeLocation(){

        //Store Location in firebase
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("DriverLocation");

        GeoFire geoFire = new GeoFire(mDatabaseReference);
        geoFire.setLocation(user_id,new GeoLocation(myLocation.getLatitude(),myLocation.getLongitude()));
    }

    boolean canAccessLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    public void requestLocationPermission() {
        // Permission is not granted
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.warning)
                    .setMessage(R.string.location_request)
                    .show();
        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    Location myLocation = null;
    public void getUserLocation() {
        if (locationProvider == null)
            locationProvider = new MyLocationProvider(this);
        myLocation = locationProvider.getLocation();
        locationProvider.trackDeviceLocation(this);
        if (myLocation == null) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("GPS Permission")
                    .setMessage("cannot get your location. please enable gps or change your location")
                    .show();

        } else {
            //locationTv.setText(myLocation.getLatitude() +"\n"+myLocation.getLongitude());
            setCurrentLocationOnMap();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        myLocation = location;
        setCurrentLocationOnMap();
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // LOCATION task you need to do.
                    // call your function
                    getUserLocation();

                } else {
                    Toast.makeText(this, "app cannot access location", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                logOutDriver();
        }
        return true;
    }

    private void logOutDriver() {
        Intent intent = new Intent(MapsActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void stopStoreLocation(){
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("DriverLocation");
        GeoFire geoFire = new GeoFire(mDatabaseReference);
        geoFire.removeLocation(user_id);
    }
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }
    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        if(currentUser==null){
            stopStoreLocation();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}