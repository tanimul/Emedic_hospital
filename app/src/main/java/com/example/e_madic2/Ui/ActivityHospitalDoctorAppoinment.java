package com.example.e_madic2.Ui;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.e_madic2.Adapter.AppointmentListAdapter;
import com.example.e_madic2.Model.AppointmentInfo;
import com.example.e_madic2.interfaces.AppointmentListAdapterCallBack;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ActivityHospitalDoctorAppoinment extends AppCompatActivity implements AppointmentListAdapterCallBack {
    private static final String TAG = "Act_Hosp_doc_Appoinment";
    private com.example.e_madic2.databinding.ActivityHospitalDoctorAppoinmentBinding binding;

    private ArrayList<AppointmentInfo> appointment_list;

    private DatabaseReference databaseReference;
    private AppointmentListAdapter appointmentListAdapter;
    private String doctor_id, hospital_id;
    private String date[];
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.example.e_madic2.databinding.ActivityHospitalDoctorAppoinmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

        binding.btnShowAppoinment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAppoinmentInfo(
                        binding.appoinmentDatePicker.getSelectedYear(),
                        binding.appoinmentDatePicker.getSelectedMonth(),
                        binding.appoinmentDatePicker.getSelectedDay()
                );
            }
        });


        binding.backs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        appointment_list = new ArrayList<>();
        appointmentListAdapter = new AppointmentListAdapter(appointment_list, ActivityHospitalDoctorAppoinment.this, ActivityHospitalDoctorAppoinment.this);
        binding.recyHospitalDoctorAppointment.setLayoutManager(new LinearLayoutManager(this));
        binding.recyHospitalDoctorAppointment.setAdapter(appointmentListAdapter);
    }

    private void init() {
        final Bundle hospitalinfo = getIntent().getExtras();
        if (hospitalinfo != null) {
            Log.d(TAG, " Data.");
            doctor_id = hospitalinfo.getString("doctor_id");
            hospital_id = hospitalinfo.getString("hospital_id");
        } else Log.d(TAG, "No Data.");


        binding.appoinmentDatePicker.setFirstVisibleDate(2021, Calendar.JANUARY, 01);
        binding.appoinmentDatePicker.setLastVisibleDate(2021, Calendar.DECEMBER, 31);
        binding.appoinmentDatePicker.setFollowScroll(true);
        date = getCurrentDateArray();
        binding.appoinmentDatePicker.setSelectedDate(Integer.valueOf(date[2]), Integer.valueOf(date[1]) - 1, Integer.valueOf(date[0]));
    }

    private void getAppoinmentInfo(int year, int month, int day) {
        final ProgressDialog Dialog = new ProgressDialog(ActivityHospitalDoctorAppoinment.this);
        Dialog.setMessage("Please wait ...");
        Dialog.show();

        String date = year + "-" + (month + 1) + "-" + day;
        databaseReference = FirebaseDatabase.getInstance().getReference("Appointment").child(hospital_id).child(date).child(doctor_id);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointment_list.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    AppointmentInfo appointment_info = dataSnapshot1.getValue(AppointmentInfo.class);
                    appointment_list.add(appointment_info);
                }
                appointmentListAdapter.notifyDataSetChanged();
                Dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ActivityHospitalDoctorAppoinment.this, "something is wrong!!", Toast.LENGTH_SHORT).show();
                Dialog.dismiss();
            }
        });
    }


    @Override
    public void onRescheduleClicked(AppointmentInfo appointmentInfo) {
        String tempAppointmentOldSchedule=appointmentInfo.getDate();
        Calendar mcurrentDate = Calendar.getInstance();
        int currentDay = 0,currentYear = 0,currentMonth = 0;
        if (currentYear == 0 || currentMonth == 0 || currentDay == 0) {
            currentYear = mcurrentDate.get(Calendar.YEAR);
            currentMonth = mcurrentDate.get(Calendar.MONTH);
            currentDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
        }
        datePickerDialog = new DatePickerDialog(ActivityHospitalDoctorAppoinment.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Log.d(TAG, "" + year + "-" + (month + 1) + "-" + dayOfMonth);
                        String updatedDate = "" + year + "-" + (month + 1) + "-" + dayOfMonth;
                        appointmentInfo.setDate(updatedDate);

                        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Appointment").child(hospital_id);
                        databaseReference2.child(updatedDate).child(appointmentInfo.getDoctor_id()).child(appointmentInfo.getUser_id())
                                .setValue(appointmentInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isComplete()){
                                    //rescheduled
                                    DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference("Appointment").child(hospital_id);
                                    databaseReference3.child(tempAppointmentOldSchedule).child(appointmentInfo.getDoctor_id()).child(appointmentInfo.getUser_id())
                                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //previous schedule deleted
                                            getAppoinmentInfo(
                                                    binding.appoinmentDatePicker.getSelectedYear(),
                                                    binding.appoinmentDatePicker.getSelectedMonth(),
                                                    binding.appoinmentDatePicker.getSelectedDay()
                                            );
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                                    Toast.makeText(ActivityHospitalDoctorAppoinment.this, "Rescheduled", Toast.LENGTH_SHORT).show();
                                }else {
                                    appointmentInfo.setDate(tempAppointmentOldSchedule);
                                    appointmentListAdapter.notifyDataSetChanged();
                                    Toast.makeText(ActivityHospitalDoctorAppoinment.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                appointmentInfo.setDate(tempAppointmentOldSchedule);
                                appointmentListAdapter.notifyDataSetChanged();
                                Toast.makeText(ActivityHospitalDoctorAppoinment.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }, currentYear, currentMonth, currentDay);

        datePickerDialog.show();
    }

    @Override
    public void onDeleteClicked(AppointmentInfo appointmentInfo, int position) {
        ProgressDialog Dialog = new ProgressDialog(ActivityHospitalDoctorAppoinment.this);
        Dialog.setMessage("Please wait ...");
        Dialog.show();

        FirebaseDatabase.getInstance().getReference("Appointment")
                .child(appointmentInfo.getHospital_id()).child(appointmentInfo.getDate())
                .child(appointmentInfo.getDoctor_id()).child(appointmentInfo.getUser_id()).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            appointment_list.remove(appointmentInfo);
                            appointmentListAdapter.notifyDataSetChanged();
                            Dialog.dismiss();
                        } else {
                            Dialog.dismiss();
                            Toast.makeText(ActivityHospitalDoctorAppoinment.this, "Somethings is wrong.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Dialog.dismiss();
                Toast.makeText(ActivityHospitalDoctorAppoinment.this, "Somethings is wrong.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String[] getCurrentDateArray() {
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = dateFormat.format(calendar.getTime());
        Log.d(TAG, "getCurrentDate: " + date);
        String dateArray[] = date.split("-", 3);

        return dateArray;
    }


}
