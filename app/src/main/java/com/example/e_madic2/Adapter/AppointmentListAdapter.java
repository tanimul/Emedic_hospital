package com.example.e_madic2.Adapter;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_madic2.Model.AppointmentInfo;
import com.example.e_madic2.Model.UserInfo;
import com.example.e_madic2.R;
import com.example.e_madic2.interfaces.AppointmentListAdapterCallBack;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AppointmentListAdapter extends RecyclerView.Adapter<AppointmentListAdapter.ViewHolderADA> {
    private static final String TAG = "AppointmentListAdapter";

    private ArrayList<AppointmentInfo> appointmentList = new ArrayList<>();
    Context context;
    private AppointmentListAdapterCallBack callBack;

    private String userName, phone_no, change_date;

    private static int poisitons;

    public AppointmentListAdapter(ArrayList<AppointmentInfo> appointmentList, Context context, AppointmentListAdapterCallBack callBack) {
        this.appointmentList = appointmentList;
        this.context = context;
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public ViewHolderADA onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_doctor_appointment, parent, false);
        ViewHolderADA viewHolderADAObject = new ViewHolderADA(view);
        return viewHolderADAObject;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderADA holder, int position) {
        final AppointmentInfo appointment_info = appointmentList.get(position);


        holder.patient_name.setText(appointment_info.getPatient_name());
        holder.problem.setText(appointment_info.getProblem());

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                callPatient(context, appointment_info.getPatient_contactNum());
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.onDeleteClicked(appointment_info,position);
            }
        });

        holder.reschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*changeDateset(appointment_info.getHospital_id(), appointment_info.getDate(),
                        appointment_info.getDoctor_id(), appointment_info.getUser_id()
                        , appointment_info.getDoctor_name(), appointment_info.getProblem(),appointment_info.getHospital_name());
                poisitons = position;*/


                callBack.onRescheduleClicked(appointment_info);
            }
        });
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    private void callPatient(Context context, String number) {
        Log.d(TAG, "patient number:" + number);
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "" + number));
        context.startActivity(intent);
    }




    private void deleleschedule(String hospital_id, String date, String doctor_id, String user_id) {
        FirebaseDatabase.getInstance().getReference("Appointment")
                .child(hospital_id).child(date).child(doctor_id).child(user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Succesfuly Updated.", Toast.LENGTH_SHORT).show();
                            //callBack.onDoctorAppointmentClicked(poisitons);
                        } else {
                            Toast.makeText(context, "Somethings is wrong.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private void getpatientinfo(String user_id) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User_Registration").child(user_id);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Data: " + dataSnapshot.getValue());
                UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);
                userName = userInfo.getName();
                phone_no = userInfo.getContact_number();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: database error" + databaseError.getMessage());
            }
        });
    }

    class ViewHolderADA extends RecyclerView.ViewHolder {

        TextView patient_name, problem;
        Button reschedule, delete;
        ImageView call;


        public ViewHolderADA(@NonNull View itemView) {
            super(itemView);


            patient_name = itemView.findViewById(R.id.hospital_doctorAppointment_patientName);
            problem = itemView.findViewById(R.id.hospital_doctorAppointment_prbolemText);
            reschedule = itemView.findViewById(R.id.hospital_doctorAppointment_rescheduleBtn);
            delete = itemView.findViewById(R.id.hospital_doctorAppointment_deleteBtn);
            call = itemView.findViewById(R.id.hospital_doctorAppointment_callImages);

        }
    }
}
