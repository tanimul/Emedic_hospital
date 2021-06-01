package com.example.e_madic2.Ui;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.e_madic2.Model.HospitalInfo;
import com.example.e_madic2.R;
import com.example.e_madic2.databinding.ActivityHospitalRegistrationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/////>>>>>>>>Confirm<<<<<<<<<<
public class ActivityHospitalRegistration extends AppCompatActivity {
    private static final String TAG = "Act_Hospital_reg";
    private ActivityHospitalRegistrationBinding binding;
    private int requestcode = 1;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private long lastclicktime = 0;
    private TimePickerDialog timePickerDialog;
    private String hospital_name, hospital_unique_id, address, contract_number, opening_hr, category, password, con_password;
    private String min;
    private Double latitude, longitute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHospitalRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Spinner
        spinner_hospitaltype();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.whiteEash, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.whiteEash));
        }

        //for address pick
        binding.regHospitalAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityHospitalRegistration.this, ActivityHospitalAddressPickup.class);
                startActivityForResult(intent, requestcode);
            }
        });

        binding.regHospitalOpeninghHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePicker timePicker = new TimePicker(ActivityHospitalRegistration.this);
                final int currentHour = timePicker.getCurrentHour();
                final int currentMinute = timePicker.getCurrentMinute();

                timePickerDialog = new TimePickerDialog(ActivityHospitalRegistration.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                int CurHour = hourOfDay;
                                if (minute < 10) {
                                    min = "0" + minute;
                                    if (hourOfDay == 0) {
                                        binding.regHospitalOpeninghHour.setText("12:" + min + " AM");
                                    } else if (hourOfDay < 12) {
                                        binding.regHospitalOpeninghHour.setText(CurHour + ":" + min + " AM");
                                    } else if (hourOfDay == 12) {
                                        binding.regHospitalOpeninghHour.setText("12:" + min + " AM");
                                    } else {
                                        CurHour = hourOfDay - 12;
                                        binding.regHospitalOpeninghHour.setText(CurHour + ":" + min + " PM");
                                    }
                                } else {
                                    if (hourOfDay == 0) {
                                        binding.regHospitalOpeninghHour.setText("12:" + minute + " AM");
                                    } else if (hourOfDay < 12) {
                                        binding.regHospitalOpeninghHour.setText(CurHour + ":" + minute + " AM");
                                    } else if (hourOfDay == 12) {
                                        binding.regHospitalOpeninghHour.setText("12:" + minute + " PM");
                                    } else {
                                        CurHour = hourOfDay - 12;
                                        binding.regHospitalOpeninghHour.setText(CurHour + ":" + minute + " PM");
                                    }
                                }


                            }
                        }, currentHour, currentMinute, false);
                timePickerDialog.show();
            }
        });


        //Registration
        binding.regHospitalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - lastclicktime < 1000) {
                    return;
                }
                lastclicktime = SystemClock.elapsedRealtime();

                if (hospitalRegistrationValidation()) {
                    Log.d(TAG, "Validation sucessfull.");
                    userRegistration();
                } else {
                    Toast.makeText(ActivityHospitalRegistration.this, "Please fill the all Informations", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void spinner_hospitaltype() {
        ArrayAdapter<String> hospital_typeadapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.hospital_type));
        hospital_typeadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.regHospitalCatagry.setAdapter(hospital_typeadapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestcode && resultCode == RESULT_OK) {
            String address = data.getStringExtra("hos_address");
            latitude = data.getDoubleExtra("hos_address_lat", 0);
            longitute = data.getDoubleExtra("hos_address_long", 1);
            Log.d(TAG, "Hospital address: " + address);
            Log.d(TAG, "Hospital latitude: " + latitude);
            Log.d(TAG, "Hospital longitude: " + longitute);
            binding.regHospitalAddress.setText("" + address);

        }
    }


    //check all Validation
    public boolean hospitalRegistrationValidation() {

        hospital_name = binding.regHospitalName.getText().toString().trim();
        hospital_unique_id = binding.regHospitalusername.getText().toString().trim();
        address = binding.regHospitalAddress.getText().toString().trim();
        contract_number = binding.regHospitalContractNum.getText().toString().trim();
        opening_hr = binding.regHospitalOpeninghHour.getText().toString().trim();
        category = binding.regHospitalCatagry.getSelectedItem().toString();
        password = binding.regHospitalPass.getText().toString().trim();
        con_password = binding.regHospitalRetypePass.getText().toString().trim();

        if (hospital_name.isEmpty()) {
            binding.regHospitalName.setError("Enter a Hospital Name please");
            binding.regHospitalName.requestFocus();
            return false;
        }

        if (hospital_unique_id.isEmpty()) {

            binding.regHospitalusername.setError("Enter a Hospital Unique ID please");
            binding.regHospitalusername.requestFocus();
            return false;

        }

        if (address.isEmpty()) {

            binding.regHospitalAddress.setError("Enter a Hospital Address please");
            binding.regHospitalAddress.requestFocus();
            return false;

        }

        if (contract_number.isEmpty()) {
            binding.regHospitalContractNum.setError("Enter a Contract Number please");
            binding.regHospitalContractNum.requestFocus();
            return false;
        }

        if (opening_hr.isEmpty()) {

            binding.regHospitalOpeninghHour.setError("Enter a Hospital Opening Hour please");
            binding.regHospitalOpeninghHour.requestFocus();
            return false;

        }

        if (category.equals("Select one")) {
            binding.spinnerimagebutton.requestFocus();
            binding.spinnerimagebutton.setFocusable(true);
            return false;

        }

        if (password.isEmpty()) {

            binding.regHospitalPass.setError("Enter a Password please");
            binding.regHospitalPass.requestFocus();
            return false;

        }

        if (con_password.isEmpty()) {

            binding.regHospitalRetypePass.setError("Enter a Confirm Password please");
            binding.regHospitalRetypePass.requestFocus();
            return false;

        }

        if (password.length() < 6) {
            binding.regHospitalPass.setError("Enter a atleast 6 digit Password please");
            binding.regHospitalPass.requestFocus();
            return false;
        }

        if (con_password.length() < 6) {
            binding.regHospitalRetypePass.setError("Enter a atleast 6 digit Password please");
            binding.regHospitalRetypePass.requestFocus();
            return false;
        }

        if (!password.equals(con_password)) {
            binding.regHospitalPass.setError("Password and Confirm Password must be same");
            binding.regHospitalPass.requestFocus();
            return false;

        } else {
            return true;
        }
    }

    public void userRegistration() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Hospital_Registration");
        HospitalInfo hospital_info = new HospitalInfo(password, address, hospital_unique_id, category, opening_hr, contract_number, con_password,
                hospital_name, latitude, longitute,hospital_unique_id);
        databaseReference.child(hospital_unique_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    databaseReference.child(hospital_unique_id).setValue(hospital_info).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: hospital name:"+binding.regHospitalName.getText().toString());
                                Toast.makeText(ActivityHospitalRegistration.this, "Registration complete", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ActivityHospitalRegistration.this, ActivityHospitalAddDoctor.class);
                                intent.putExtra("hospital_id", hospital_unique_id);
                                intent.putExtra("hospital_name",binding.regHospitalName.getText().toString());
                                intent.putExtra("skipnote",true);
                                startActivity(intent);finish();
                            } else {
                                Toast.makeText(ActivityHospitalRegistration.this, "Registration failed", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                } else {
                    Toast.makeText(ActivityHospitalRegistration.this, "Already registered your Hospital", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}