package com.example.e_madic2.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.e_madic2.Model.Ambulance_info;
import com.example.e_madic2.Model.HospitalInfo;
import com.example.e_madic2.R;
import com.example.e_madic2.databinding.ActivityAddAmbulanceBinding;
import com.example.e_madic2.databinding.ActivityHospitalAddDoctorBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActivityAddAmbulance extends AppCompatActivity {
    private static final String TAG = "ActivityAddAmbulance";
    private ActivityAddAmbulanceBinding binding;
    private String hospital_unique_id, address, contract_number, drivername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAmbulanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final Bundle hospitalinfo = getIntent().getExtras();
        if (hospitalinfo != null) {
            Log.d(TAG, " Data.");
            hospital_unique_id = hospitalinfo.getString("hospital_id");
        }

        spinner_ambulancearea();

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Registration
        binding.ambulanceAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ambulanceRegistrationValidation()) {
                    Log.d(TAG, "Validation sucessfull.");
                    ambulanceRegistration();
                } else {
                    Toast.makeText(ActivityAddAmbulance.this, "Please fill the all Informations", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ambulanceRegistration() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Ambulance_List");
        Ambulance_info ambulance_info = new Ambulance_info(drivername, contract_number, address, hospital_unique_id);
        String key = database.getReference("Ambulance_List").push().getKey();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    databaseReference.child(key).setValue(ambulance_info).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ActivityAddAmbulance.this, "Ambulance Add complete", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            } else {
                                Toast.makeText(ActivityAddAmbulance.this, "Ambulance Add failed", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private boolean ambulanceRegistrationValidation() {

        drivername = binding.ambulanceDriverName.getText().toString().trim();
        contract_number = binding.doctorAddContractNum.getText().toString().trim();
        address = binding.ambulanceArea.getSelectedItem().toString();

        if (drivername.isEmpty()) {
            binding.ambulanceDriverName.setError("Enter a Driver's Name please");
            binding.ambulanceDriverName.requestFocus();
            return false;
        }

        if (contract_number.isEmpty()) {

            binding.doctorAddContractNum.setError("Enter a Contract Number please");
            binding.doctorAddContractNum.requestFocus();
            return false;

        }

        if (address.equals("Select one")) {
            binding.spinnerimagebutton.requestFocus();
            binding.spinnerimagebutton.setFocusable(true);
            return false;

        } else {
            return true;
        }

    }

    private void spinner_ambulancearea() {
        ArrayAdapter<String> ambulance_typeadapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.ambulance_area));
        ambulance_typeadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.ambulanceArea.setAdapter(ambulance_typeadapter);
    }
}