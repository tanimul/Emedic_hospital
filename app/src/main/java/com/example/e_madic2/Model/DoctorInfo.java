package com.example.e_madic2.Model;

public class DoctorInfo {
    private String doctor_name, contruct_number, qualification, visiting_schedule,doctor_key,hospital_id,hospital_name;
    private int visiting_charge,max_appionment;

    public DoctorInfo() {
    }

    public DoctorInfo(String doctor_name, String contruct_number, String qualification,
                       int visiting_charge, String visiting_schedule, int max_appionment,String doctor_key,String hospital_id,String hospital_name) {
        this.doctor_name = doctor_name;
        this.contruct_number = contruct_number;
        this.qualification = qualification;
        this.visiting_charge = visiting_charge;
        this.visiting_schedule = visiting_schedule;
        this.max_appionment = max_appionment;
        this.doctor_key=doctor_key;
        this.hospital_id=hospital_id;
        this.hospital_name=hospital_name;
    }

    public String getDoctor_key() {
        return doctor_key;
    }

    public void setDoctor_key(String doctor_key) {
        this.doctor_key = doctor_key;
    }

    public String getDoctor_name() {
        return doctor_name;
    }

    public void setDoctor_name(String doctor_name) {
        this.doctor_name = doctor_name;
    }

    public String getContruct_number() {
        return contruct_number;
    }

    public void setContruct_number(String contruct_number) {
        this.contruct_number = contruct_number;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public int getVisiting_charge() {
        return visiting_charge;
    }

    public void setVisiting_charge(int visiting_charge) {
        this.visiting_charge = visiting_charge;
    }

    public String getVisiting_schedule() {
        return visiting_schedule;
    }

    public void setVisiting_schedule(String visiting_schedule) {
        this.visiting_schedule = visiting_schedule;
    }

    public int getMax_appionment() {
        return max_appionment;
    }

    public void setMax_appionment(int max_appionment) {
        this.max_appionment = max_appionment;
    }

    public String getHospital_id() {
        return hospital_id;
    }

    public void setHospital_id(String hospital_id) {
        this.hospital_id = hospital_id;
    }

    public String getHospital_name() {
        return hospital_name;
    }

    public void setHospital_name(String hospital_name) {
        this.hospital_name = hospital_name;
    }
}
