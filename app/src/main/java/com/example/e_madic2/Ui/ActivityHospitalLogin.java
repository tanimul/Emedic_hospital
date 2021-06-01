package com.example.e_madic2.Ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.e_madic2.KEYS;
import com.example.e_madic2.Model.HospitalInfo;
import com.example.e_madic2.R;
import com.example.e_madic2.Tools;
import com.example.e_madic2.databinding.ActivityHospitalLoginBinding;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActivityHospitalLogin extends AppCompatActivity {
    private static final String TAG = "Act_Hospital_Login";
    private ActivityHospitalLoginBinding binding;
    private DatabaseReference databaseReference;
    private long lastclicktime = 0;

    private static final int FINE_LOCATION_REQUEST_CODE = 11;
    private static final int LOCATION_SETTINGS_REQUEST_CODE = 22;

    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Name = "hospitalKey";
    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHospitalLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Tools.getPrefBoolean(KEYS.IS_LOGGED_IN, false)) {
            Intent intent = new Intent(ActivityHospitalLogin.this, ActivityHospitalHomePage.class);
            startActivity(intent);
            finish();
        }
        if (requestlocationpermission()) {
            locationSettingOption();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.whiteEash, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.whiteEash));
        }

        binding.hospitalLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - lastclicktime < 1000) {
                    return;
                }
                lastclicktime = SystemClock.elapsedRealtime();
                validation();
            }
        });

        binding.textViewFromLoginForRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - lastclicktime < 1000) {
                    return;
                }
                startActivity(new Intent(ActivityHospitalLogin.this, ActivityHospitalRegistration.class));
            }
        });
    }

    //Request location permission
    private boolean requestlocationpermission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST_CODE);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINE_LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: permission granted.");
                locationSettingOption();
            } else {
                Log.d(TAG, "onRequestPermissionsResult: permission denied.");
                new AlertDialog.Builder(this)
                        .setTitle("Enable Location Permission")
                        .setMessage("You need to give Location Permission for better user user Experience.")
                        .setPositiveButton("Enable", new
                                DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                        requestlocationpermission();
                                    }
                                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();

            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //Location setting option
    private void locationSettingOption() {
        final LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        Log.d(TAG, "try to loction on ");
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(ActivityHospitalLogin.this,
                                LOCATION_SETTINGS_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException sendEx) {
                    }
                }
            }
        });
    }

    private void validation() {
        i = 0;
        final ProgressDialog Dialog = new ProgressDialog(ActivityHospitalLogin.this);
        Dialog.setMessage("Please wait ...");
        Dialog.show();
        final String hospital_tag = binding.hospitalLoginHospiTag.getText().toString().trim();
        final String password = binding.hospitalLoginPassword.getText().toString().trim();

        databaseReference = FirebaseDatabase.getInstance().getReference("Hospital_Registration");
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Total Registration: " + dataSnapshot.getChildrenCount());

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    HospitalInfo hospital_info = dataSnapshot1.getValue(HospitalInfo.class);
                    Log.d(TAG, "Hospital Name:" + hospital_info.getHospital_name());

                    if (hospital_tag.equals(hospital_info.getHospital_unique_id()) && password.equals(hospital_info.getPassword())) {
                        Toast.makeText(ActivityHospitalLogin.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Login Successfully");
                        Intent intent = new Intent(ActivityHospitalLogin.this, ActivityHospitalHomePage.class);
                        //intent.putExtra("hospital_unique_id", "" + hospital_info.getHospital_unique_id());
                        Tools.savePrefBoolean(KEYS.IS_LOGGED_IN, true);
                        Tools.savePref(KEYS.Hospital_id, hospital_tag);
                        Log.d(TAG, "" + Tools.getPref(KEYS.Hospital_id, "Hospital_id"));
//                        intent.putExtra("hospital_address", "" + hospital_info.getAddress());
                       Dialog.dismiss();
                        startActivity(intent);
                        finish();
                        break;

                    } else if (i == dataSnapshot.getChildrenCount()-1) {
                       Dialog.dismiss();
                        Log.d(TAG, "Login Unsuccessfully");
                        Toast.makeText(ActivityHospitalLogin.this, "Login Unsuccessfully", Toast.LENGTH_SHORT).show();
                    }
                    i++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    //Show hide password
    public void ShowHidePassAdm(View view) {
        if (binding.hospitalLoginPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
            //Show Password
            binding.hospitalLoginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            binding.eyebutton.setImageResource(R.drawable.ic_eyes);
            binding.hospitalLoginPassword.setHint("");
        } else {
            //Hide Password
            binding.hospitalLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            binding.eyebutton.setImageResource(R.drawable.hide);
            binding.hospitalLoginPassword.setHint("********");
        }
    }
}
