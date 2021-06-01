package com.example.e_madic2.Ui;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.e_madic2.KEYS;
import com.example.e_madic2.Model.HospitalInfo;
import com.example.e_madic2.R;

import com.example.e_madic2.Tools;
import com.example.e_madic2.databinding.ActivityHospitalHomePageBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActivityHospitalHomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {
    private static final String TAG = "Act_Hosp_Home_page";
    private ActivityHospitalHomePageBinding binding;
    private DrawerLayout drawerLayout;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private GoogleMap map;
    private static final float DEFAULT_ZOOM = 17f;
    Double latitude, longitude;
    static public String hospital_name;
    static public String userID;
    private FusedLocationProviderClient fusedLocationClient;

    private Dialog dialogRating;
    private TextView ratingCancel,ratingSubmit;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHospitalHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        userID = Tools.getPref(KEYS.Hospital_id, "Hospital_id");
        Log.d(TAG, "User Id: " + userID);
        if (!userID.equals("Hospital_id")) {
            Log.d(TAG, "uniqueId :> " + userID);
            getInformation(userID);
        }
        setSupportActionBar(binding.hospitalHomePageToobar);
        setupRatigDialog();

        drawerLayout = findViewById(R.id.hospital_drawer_layout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, binding.hospitalHomePageToobar,
                R.string.openNavDrawer, R.string.closeNavDrawer);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        binding.hospitalNavView.setNavigationItemSelectedListener(this);

        ratingSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogRating.dismiss();
                Toast.makeText(ActivityHospitalHomePage.this, "Thank you for your feedback.", Toast.LENGTH_SHORT).show();
            }
        });

        ratingCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogRating.dismiss();
            }
        });

        //======================================================= maps =======================================================
        // location face
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        binding.iconCentermap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latitude != null && longitude != null) {
                    moveCamera(new LatLng(latitude, longitude), "IconCenterMap");
                }
            }
        });
    }

    private void setupRatigDialog(){
        dialogRating=new Dialog(this);
        dialogRating.setContentView(R.layout.layout_rating_dialog);
        ratingSubmit=dialogRating.findViewById(R.id.ratingSubmit);
        ratingCancel=dialogRating.findViewById(R.id.ratingCancel);
        ratingBar=dialogRating.findViewById(R.id.ratingBar);
    }
    private void getInformation(String hospital_uniqueid) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Hospital_Registration").child(hospital_uniqueid);
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                HospitalInfo hospital_info = dataSnapshot.getValue(HospitalInfo.class);
                Log.d(TAG, "hospital name:" + hospital_info.getHospital_name());
                hospital_name = hospital_info.getHospital_name();
                binding.hospHospitalName.setText("Hospital Name: " + hospital_info.getHospital_name());
                binding.hospHospitalContractNumber.setText("Contrat Number: " + hospital_info.getContact_number());
                binding.hospHospitalOpenStatus.setText("Open status: " + hospital_info.getOpen_hour());
                latitude = hospital_info.getLatitude();
                longitude = hospital_info.getLongitude();
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.home_map);
                mapFragment.getMapAsync(ActivityHospitalHomePage.this);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "something is wrong:");
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {

            case R.id.nav_doctorList:
                intent = new Intent(ActivityHospitalHomePage.this, ActivityHospitalDoctorList.class);
                intent.putExtra("hospital_name", hospital_name);
                intent.putExtra("hospital_id", userID);
                startActivity(intent);
                break;

            case R.id.nav_addambulance:
                intent = new Intent(ActivityHospitalHomePage.this, ActivityAddAmbulance.class);
                intent.putExtra("hospital_id", userID);
                startActivity(intent);
                break;

            case R.id.nav_hospital_logOut:
                logout();
                intent = new Intent(ActivityHospitalHomePage.this, ActivityHospitalLogin.class);
                intent.putExtra("finish", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;

            case R.id.nav_hospital_rateApp:
                ratingBar.setRating(0);
                binding.hospitalDrawerLayout.closeDrawer(GravityCompat.START, false);
                dialogRating.show();
                break;

            case R.id.nav_hospital_aboutUs:
                startActivity(new Intent(ActivityHospitalHomePage.this, ActivityAbout.class));
                break;

        }
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    //logout current hospital
    private void logout() {
        Tools.savePrefBoolean(KEYS.IS_LOGGED_IN, false);
        Tools.savePref(KEYS.Hospital_id, "Hospital_id");
        firebaseAuth.getInstance().signOut();
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(getBitmapDescriptor(getResources().getDrawable(R.drawable.ic_icon_hospital, null)))
                .title(hospital_name);
        map.addMarker(markerOptions).showInfoWindow();
        moveCamera(latLng, "onMapReady");
    }


    @Override
    protected void onResume() {
        super.onResume();
        binding.hospitalDrawerLayout.closeDrawer(GravityCompat.START, false);
    }

    public void moveCamera(Location location, String caller) {
        Log.d(TAG, "moveCamera: called by " + caller);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM));
    }

    public void moveCamera(LatLng latLng, String caller) {
        Log.d(TAG, "moveCamera: called by " + caller);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
    }

    private BitmapDescriptor getBitmapDescriptor(Drawable vectorDrawable) {
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bm = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bm);
    }
}