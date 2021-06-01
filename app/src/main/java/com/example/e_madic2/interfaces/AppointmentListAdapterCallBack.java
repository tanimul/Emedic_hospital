package com.example.e_madic2.interfaces;

import com.example.e_madic2.Model.AppointmentInfo;
import com.example.e_madic2.Model.DoctorInfo;

public interface AppointmentListAdapterCallBack {

    void onRescheduleClicked(AppointmentInfo appointmentInfo);
    void onDeleteClicked(AppointmentInfo appointmentInfo,int position);
}
