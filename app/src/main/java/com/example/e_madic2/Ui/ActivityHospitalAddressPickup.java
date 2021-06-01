package com.example.e_madic2.Ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.e_madic2.R;
import com.example.e_madic2.databinding.ActivityHospitalAddressPickupBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

public class ActivityHospitalAddressPickup extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "Act_HospitalAdd_Pickup";
    private ActivityHospitalAddressPickupBinding binding;

    private Double hos_lat, hos_long;

    private GoogleMap map;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int REQUEST_CODE = 111;
    private FusedLocationProviderClient fusedLocationClient;
    private Location last_location;
    private Place place;
    private String location_name;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHospitalAddressPickupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //PLaces API Declare
        initPlacesAPI();
        //Place search
        binding.hospitalAddressTypeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initAutoComplete();
            }
        });
        //Address confirm
        binding.hospitalAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (!binding.hospitalAddressTypeAddress.getText().toString().isEmpty()) {
                    String hos_address = binding.hospitalAddressTypeAddress.getText().toString();
                    Log.d(TAG, "Hospital address: " + hos_address);
                    intent.putExtra("hos_address", hos_address);
                    intent.putExtra("hos_address_lat", hos_lat);
                    intent.putExtra("hos_address_long", hos_long);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    finish();
                }

            }
        });

        //current location face
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(ActivityHospitalAddressPickup.this);
        fetchlastlocation();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: called");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

    }

    public void moveCamera(Location location) {
        LatLng latLng = new LatLng(last_location.getLatitude(), last_location.getLongitude());
        // MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
        //  map.addMarker(markerOptions);
    }

    public void moveCamera(LatLng latLng) {
//        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
//        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
//        map.addMarker(markerOptions);
        if (marker != null) {
            marker.remove();
        }
        marker = map.addMarker(new MarkerOptions()
                .position(latLng)
                .title("" + location_name));
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

    }

    public void fetchlastlocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    last_location = location;
                    moveCamera(location);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchlastlocation();
                }
                break;
        }
    }

    private void initAutoComplete() {

        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG,
                Place.Field.TYPES);

        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .setCountry("bd")
                .setHint("Search for Places")
                .build(ActivityHospitalAddressPickup.this);
        startActivityForResult(intent, 100);
    }

    private void initPlacesAPI() {
        Places.initialize(getApplicationContext(), "AIzaSyCWzXk-SGxgedHpO8pnTA0h6aYzEiJL_ss");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {

            place = Autocomplete.getPlaceFromIntent(data);
            Log.d(TAG, "place address: " + place.getAddress());
            location_name = place.getAddress();
            hos_lat = place.getLatLng().latitude;
            hos_long = place.getLatLng().longitude;
            binding.hospitalAddressTypeAddress.setText("" + location_name);
            LatLng latLng = new LatLng(hos_lat, hos_long);
            moveCamera(latLng);
        }
    }
}