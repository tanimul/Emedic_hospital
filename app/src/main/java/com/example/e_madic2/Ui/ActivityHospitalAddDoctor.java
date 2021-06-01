package com.example.e_madic2.Ui;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.e_madic2.Model.DoctorInfo;
import com.example.e_madic2.databinding.ActivityHospitalAddDoctorBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ActivityHospitalAddDoctor extends AppCompatActivity {
    private static final String TAG = "Act_Doctor_Add";
    private ActivityHospitalAddDoctorBinding binding;
    private DatabaseReference databaseReference;
    private long lastclicktime = 0;
    private String doctor_name, contruct_number, qualification, visiting_schedule,hospital_name;
    private int visiting_charge = 0, max_appionment = 0;
    private String visiting_schedulefrom, visiting_scheduleto;
    String hospital_id;
    private TimePickerDialog timePickerDialog, timePickerDialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHospitalAddDoctorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final Bundle hospitalinfo = getIntent().getExtras();

        boolean showSkipButton=getIntent().getBooleanExtra("skipnote",false);
        if(showSkipButton) binding.hospitalAddSkipText.setVisibility(View.VISIBLE);
        else binding.hospitalAddSkipText.setVisibility(View.GONE);

        if (hospitalinfo != null) {
            hospital_id = hospitalinfo.getString("hospital_id");
            hospital_name = hospitalinfo.getString("hospital_name");
            Log.d(TAG, "onComplete: hospital name:"+hospital_name);
        }

        //Add Doctor
        binding.doctorAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - lastclicktime < 1000) {
                    return;
                }
                lastclicktime = SystemClock.elapsedRealtime();

                if (doctoradd_Validation()) {
                    Log.d(TAG, "Validation sucessfull");
                    doctorRegistration();
                } else {
                    Toast.makeText(ActivityHospitalAddDoctor.this, "Please fill the all Informations", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.hospitalAddSkipText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - lastclicktime < 1000) {
                    return;
                }
                lastclicktime = SystemClock.elapsedRealtime();
                Intent intent = new Intent(ActivityHospitalAddDoctor.this, ActivityHospitalHomePage.class);
                startActivity(intent);
                finish();
            }
        });

        binding.doctorVisitSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getschdulefrom();
            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getschduleto(String visiting_schedulefrom) {
        TimePicker timePicker = new TimePicker(ActivityHospitalAddDoctor.this);
        final int currentHour = timePicker.getHour();
        final int currentMinute = timePicker.getMinute();
        timePickerDialog = new TimePickerDialog(ActivityHospitalAddDoctor.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        int CurHour = hourOfDay;
                        if (minute < 10) {
                            visiting_scheduleto = "0" + minute;
                            if (hourOfDay == 0) {
                                visiting_scheduleto = "12:" + visiting_scheduleto + " AM";
                            } else if (hourOfDay < 12) {

                                visiting_scheduleto = CurHour + ":" + visiting_scheduleto + " AM";
                            } else if (hourOfDay == 12) {
                                visiting_scheduleto = "12:" + visiting_scheduleto + " AM";
                            } else {
                                CurHour = hourOfDay - 12;
                                visiting_scheduleto = CurHour + ":" + visiting_scheduleto + " PM";
                            }
                        } else {
                            if (hourOfDay == 0) {
                                visiting_scheduleto = "12:" + minute + " AM";
                            } else if (hourOfDay < 12) {
                                visiting_scheduleto = CurHour + ":" + minute + " AM";
                            } else if (hourOfDay == 12) {
                                visiting_scheduleto = "12:" + minute + " PM";
                            } else {
                                CurHour = hourOfDay - 12;
                                visiting_scheduleto = CurHour + ":" + minute + " PM";
                            }
                        }
                        binding.doctorVisitSchedule.setText(visiting_schedulefrom + " - " + visiting_scheduleto);

                    }

                }, currentHour, currentMinute, false);
        timePickerDialog.setTitle("End Time");
        timePickerDialog.show();
    }

    private void getschdulefrom() {
        TimePicker timePicker = new TimePicker(ActivityHospitalAddDoctor.this);
        final int currentHour = timePicker.getHour();
        final int currentMinute = timePicker.getMinute();

        timePickerDialog2 = new TimePickerDialog(ActivityHospitalAddDoctor.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        int CurHour = hourOfDay;
                        if (minute < 10) {
                            visiting_schedulefrom = "0" + minute;
                            if (hourOfDay == 0) {
                                visiting_schedulefrom = "12:" + visiting_schedulefrom + " AM";
                            } else if (hourOfDay < 12) {

                                visiting_schedulefrom = CurHour + ":" + visiting_schedulefrom + " AM";
                            } else if (hourOfDay == 12) {
                                visiting_schedulefrom = "12:" + visiting_schedulefrom + " AM";
                            } else {
                                CurHour = hourOfDay - 12;
                                visiting_schedulefrom = CurHour + ":" + visiting_schedulefrom + " PM";
                            }
                        } else {
                            if (hourOfDay == 0) {
                                visiting_schedulefrom = "12:" + minute + " AM";
                            } else if (hourOfDay < 12) {
                                visiting_schedulefrom = CurHour + ":" + minute + " AM";
                            } else if (hourOfDay == 12) {
                                visiting_schedulefrom = "12:" + minute + " PM";
                            } else {
                                CurHour = hourOfDay - 12;
                                visiting_schedulefrom = CurHour + ":" + minute + " PM";
                            }
                        }
                        getschduleto(visiting_schedulefrom);
                    }
                }, currentHour, currentMinute, false);
        timePickerDialog2.setTitle("Start Time");
        timePickerDialog2.show();
    }

    //check all Validation
    public boolean doctoradd_Validation() {

        doctor_name = binding.hospitalAddDoctorName.getText().toString().trim();
        contruct_number = binding.doctorAddContractNum.getText().toString().trim();
        qualification = binding.doctorAddQualification.getText().toString().trim();
        visiting_charge = Integer.parseInt(binding.doctorAddVisitingCharge.getText().toString());
        visiting_schedule = binding.doctorVisitSchedule.getText().toString().trim();
        max_appionment = Integer.parseInt(binding.doctorAddMaxAppint.getText().toString());


        if (doctor_name.isEmpty()) {
            binding.hospitalAddDoctorName.setError("Enter a Doctor Name please");
            binding.hospitalAddDoctorName.requestFocus();
            return false;
        }

        if (contruct_number.isEmpty()) {

            binding.doctorAddContractNum.setError("Enter a Contract number please");
            binding.doctorAddContractNum.requestFocus();
            return false;

        }

        if (qualification.isEmpty()) {

            binding.doctorAddQualification.setError("Enter a Doctor Qualification please");
            binding.doctorAddQualification.requestFocus();
            return false;

        }

        if (visiting_charge == 0) {
            binding.doctorAddVisitingCharge.setError("Enter a Visiting Charge please");
            binding.doctorAddVisitingCharge.requestFocus();
            return false;
        }

        if (visiting_schedule.isEmpty()) {

            binding.doctorVisitSchedule.setError("Enter a Visiting Schedule please");
            binding.doctorVisitSchedule.requestFocus();
            return false;

        }
        if (max_appionment == 0) {

            binding.doctorVisitSchedule.setError("Enter a Maximum Appionment please");
            binding.doctorVisitSchedule.requestFocus();
            return false;

        } else {
            return true;
        }

    }

    public void doctorRegistration() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String key = database.getReference("DoctorList").push().getKey();
        databaseReference = FirebaseDatabase.getInstance().getReference("DoctorList").child(hospital_id);
        DoctorInfo doctor_info = new DoctorInfo(doctor_name, contruct_number, qualification, visiting_charge, visiting_schedule, max_appionment, key, hospital_id,hospital_name);
        databaseReference.child(key).setValue(doctor_info).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ActivityHospitalAddDoctor.this, "Add Successfully", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    Toast.makeText(ActivityHospitalAddDoctor.this, "Add Unsuccessfully", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}