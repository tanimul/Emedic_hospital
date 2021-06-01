package com.example.e_madic2.Ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.e_madic2.Adapter.DoctorListAdapter;
import com.example.e_madic2.Model.DoctorInfo;
import com.example.e_madic2.R;
import com.example.e_madic2.interfaces.DoctorListAdapterCallBack;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ActivityHospitalDoctorList extends AppCompatActivity implements DoctorListAdapterCallBack {
    private static final String TAG = "Act_Hosp_doctor_list";
    private com.example.e_madic2.databinding.ActivityHospitalDoctorListBinding binding;


    private ArrayList<DoctorInfo> doctorlist = new ArrayList<DoctorInfo>();
    private DatabaseReference databaseReference;
    DoctorListAdapter doctorListAdapter;
    static String hospital_name,hospital_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.example.e_madic2.databinding.ActivityHospitalDoctorListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

        binding.addDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityHospitalDoctorList.this, ActivityHospitalAddDoctor.class);
                intent.putExtra("hospital_id", hospital_id);
                intent.putExtra("hospital_name", hospital_name);
                intent.putExtra("skipnote",false);
                startActivity(intent);
            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.recyHospitalDoctorList.setFitsSystemWindows(true);
        binding.recyHospitalDoctorList.setLayoutManager(new LinearLayoutManager(this));
        binding.recyHospitalDoctorList.setHasFixedSize(true);
        doctorListAdapter = new DoctorListAdapter(doctorlist, ActivityHospitalDoctorList.this,ActivityHospitalDoctorList.this);
        binding.recyHospitalDoctorList.setAdapter(doctorListAdapter);
    }

    void init(){
        Bundle hospitalinfo= getIntent().getExtras();
        if (hospitalinfo != null) {
            hospital_name = hospitalinfo.getString("hospital_name");
            hospital_id = hospitalinfo.getString("hospital_id");
        }


    }


    private void readDoctor() {
        Log.d(TAG, "read"+hospital_id);
        final ProgressDialog Dialog = new ProgressDialog(ActivityHospitalDoctorList.this);
        Dialog.setMessage("Please wait ...");
        Dialog.show();
        databaseReference = FirebaseDatabase.getInstance().getReference("DoctorList").child(hospital_id);
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                doctorlist.clear();
                Log.d(TAG, "Total children: " + dataSnapshot.getChildrenCount());
                Dialog.dismiss();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String key = dataSnapshot1.getKey();
                    DoctorInfo doctor_info = dataSnapshot1.getValue(DoctorInfo.class);
                    doctorlist.add(doctor_info);
                    Log.d(TAG, "Key:" + key + " Doctor name:" + doctor_info.getDoctor_name());

                }
                doctorListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "something is wrong:");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!hospital_name.equals("")) readDoctor();
    }

    @Override
    public void onDoctorAppointmentClicked(DoctorInfo doctor) {
        Intent intent=new Intent(ActivityHospitalDoctorList.this, ActivityHospitalDoctorAppoinment.class);
        intent.putExtra("doctor_id",doctor.getDoctor_key());
        intent.putExtra("hospital_id",hospital_id);
        startActivity(intent);
    }
}