package com.example.e_madic2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_madic2.Model.DoctorInfo;
import com.example.e_madic2.R;
import com.example.e_madic2.interfaces.DoctorListAdapterCallBack;

import java.util.ArrayList;

public class DoctorListAdapter extends RecyclerView.Adapter<DoctorListAdapter.DoctorListholder> {
    private static final String TAG = "Doctor_List_Adapter";
    private ArrayList<DoctorInfo> doctorList;
    Context context;
    DoctorListAdapterCallBack callBack;

    public DoctorListAdapter(ArrayList<DoctorInfo> doctorList, Context context, DoctorListAdapterCallBack callBack) {
        this.doctorList = doctorList;
        this.context = context;
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public DoctorListAdapter.DoctorListholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_hospital_doctorlist, parent, false);
        return new DoctorListAdapter.DoctorListholder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorListholder holder, int position) {
        DoctorInfo doctor_info = doctorList.get(position);

        holder.name.setText("" + doctor_info.getDoctor_name());
        holder.contractnumber.setText("Contract number: " + doctor_info.getContruct_number());
        holder.view_appointmet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.onDoctorAppointmentClicked(doctor_info);
            }
        });


    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public class DoctorListholder extends RecyclerView.ViewHolder {

        ImageView view_appointmet;
        TextView name, contractnumber;
        public DoctorListholder(@NonNull View itemView) {
            super(itemView);
            view_appointmet = itemView.findViewById(R.id.hospital_doctorlist_appointmentView);
            name = itemView.findViewById(R.id.hospital_doctorList_doctorName);
            contractnumber = itemView.findViewById(R.id.hospital_doctorlist_contactNumber);
        }
    }
}
