package com.example.mini_project_2_aous_alnima;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;


public class MapsActivity extends AppCompatActivity
            implements
            OnMapReadyCallback,
            GoogleMap.OnMyLocationButtonClickListener,
            GoogleMap.OnMyLocationClickListener,
            ActivityCompat.OnRequestPermissionsResultCallback,
            LocationListener {

        private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
        private boolean mPermissionDenied = false;
        LocationManager locationManager;
        Location location;
        private GoogleMap mMap;
        List<gym> GymVisits;

        LatLng Gym;
        LatLng MyCoordinates;
        LatLng CS;

        DatabaseReference databaseGPS;
        private static DecimalFormat df2 = new DecimalFormat("#.##");

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_maps);

            FirebaseApp.initializeApp(this);
            databaseGPS = FirebaseDatabase.getInstance().getReference("gymVisits/");

            Gym = new LatLng(30.617707, -96.301920);


            MyCoordinates = new LatLng(0,0);
            SupportMapFragment mapFragment =
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            CS = new LatLng(30.621427, -96.340539);
            locationManager=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

            databaseGPS.addChildEventListener(new locationChildEventListener());
            FloatingActionButton fab = findViewById(R.id.showCard);
            FloatingActionButton fab1 = findViewById(R.id.log);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addNotification();
                    Gotoimage();
                    //
                }
            });

            fab1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GoTolist();
                    //
                }
            });

        }


        private class locationChildEventListener implements ChildEventListener {
            @Override
            public void onChildAdded(@NonNull DataSnapshot ds, @Nullable String s) {
                gym NewVisit = ds.getValue(gym.class);
                GymVisits.add(NewVisit);
            }

            @Override
            public void onChildRemoved (@NonNull DataSnapshot ds){
                if(ds.getChildrenCount()==4) {
                    // do sth

                }
            }
            @Override
            public void onChildChanged (@NonNull DataSnapshot dataSnapshot, @Nullable String s){
            }
            @Override
            public void onChildMoved (@NonNull DataSnapshot dataSnapshot, @Nullable String s){
            }
            @Override
            public void onCancelled (@NonNull DatabaseError databaseError){
            }
        }

        private void addGymVisit() {
            String time = String.valueOf(Calendar.getInstance().getTime());
            gym NewGymVisit = new gym(time, databaseGPS.getKey());
            //
            databaseGPS.push().setValue(NewGymVisit);
        }


        @Override
        public void onMapReady(GoogleMap map) {
            mMap = map;
            mMap.getUiSettings().setZoomControlsEnabled(false);
            //MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_retro);
            //mMap.setMapStyle(style);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
            enableMyLocation();
            //
            //MyCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(CS, 12));
            AddMarker(Gym);

        }

        private void enableMyLocation() {       //Enables the My Location layer if the fine location permission has been granted.
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission to access the location is missing.
                PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                        Manifest.permission.ACCESS_FINE_LOCATION, true);
            } else if (mMap != null) {
                // Access to the location has been granted to the app.
                mMap.setMyLocationEnabled(true);
            }
        }

        @Override
        public boolean onMyLocationButtonClick() {
            Toast.makeText(this, "Centering Around My Location", Toast.LENGTH_SHORT).show();
            addNotification();
            // Return false so that we don't consume the event and the default behavior still occurs
            // (the camera animates to the user's current position).
            return false;
        }

        @Override
        public void onMyLocationClick(@NonNull Location location) {
            MyCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
            Toast.makeText(this, "Current location:\n" + location.getLatitude()
                    +", "+location.getLongitude(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                               @NonNull int[] grantResults) {
            if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
                return;
            }

            if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Enable the my location layer if the permission has been granted.
                enableMyLocation();
            } else {
                // Display the missing permission error dialog when the fragments resume.
                mPermissionDenied = true;
            }
        }

        @Override
        protected void onResumeFragments() {
            super.onResumeFragments();
            if (mPermissionDenied) {
                // Permission was not granted, display error dialog.
                showMissingPermissionError();
                mPermissionDenied = false;
            }
        }

        //Displays a dialog with error message explaining that the location permission is missing.
        private void showMissingPermissionError() {
            PermissionUtils.PermissionDeniedDialog
                    .newInstance(true).show(getSupportFragmentManager(), "dialog");
        }


        @Override
        public void onLocationChanged(Location location) {
            //MakeToast("you moved a little");
            MyCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(MyCoordinates));
            //calculate distance to the gym
            CalculateDistancetoGyms(MyCoordinates);
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

        public void MakeToast(String s){
            Toast.makeText(this, s, Toast.LENGTH_LONG).show();

        }

        public void CalculateDistancetoGyms (LatLng l){

            Double GPS2_lat = l.latitude;
            Double GPS2_lon = l.longitude;


                double x_an = Gym.longitude - GPS2_lon;

                double dist = Math.sin(Math.toRadians(GPS2_lat)) * Math.sin(Math.toRadians(Gym.latitude)) +
                        Math.cos(Math.toRadians(GPS2_lat)) * Math.cos(Math.toRadians(Gym.latitude)) * Math.cos(Math.toRadians(x_an));
                dist = Math.acos(dist);
                dist = Math.toDegrees(dist);
                dist = dist * 60 * 1.1515;   //to miles

                if (dist <= 0.2) {

                    MakeToast(String.valueOf(df2.format(dist)));

                    String time = String.valueOf(Calendar.getInstance().getTime());
                    gym NewGymVisit = new gym(time, databaseGPS.getKey());
                    databaseGPS.push().setValue(NewGymVisit);
                    addNotification();
                }

        }

        public void AddMarker(LatLng l){
            Marker marker = mMap.addMarker(new MarkerOptions()
                .position(l)
                .title("GYM")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.gym1)));
        }

        public void Gotoimage(){
            Intent i = new Intent(this, ImageActivity.class);
            startActivity(i);
        }
    public void GoTolist(){
        Intent i = new Intent(this, Main2Activity.class);
        startActivity(i);
    }

    private void addNotification(){
        String chanel_id = "3000";

        NotificationCompat.Builder notification_builder = new NotificationCompat.Builder(this, chanel_id)
                .setSmallIcon(R.drawable.gym)
                .setContentTitle("GYM")
                .setContentText("Here's you Gym ID card")
                .setAutoCancel(true);


        Intent notificationIntent = new Intent(this, MapsActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification_builder.setContentIntent(contentIntent);


        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification_builder.build());
        MakeToast("here");}

}
