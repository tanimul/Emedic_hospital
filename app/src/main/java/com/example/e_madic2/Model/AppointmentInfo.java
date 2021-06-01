package com.example.e_madic2.Model;

public class AppointmentInfo {
    String date, doctor_id, problem, user_id, doctor_name, hospital_id,hospital_name,patient_name,patient_contactNum;

    public AppointmentInfo(String date, String doctor_id, String problem, String user_id, String doctor_name, String hospital_id,String hospital_name) {
        this.date = date;
        this.doctor_id = doctor_id;
        this.problem = problem;
        this.user_id = user_id;
        this.doctor_name = doctor_name;
        this.hospital_id = hospital_id;
        this.hospital_name=hospital_name;
    }

    public AppointmentInfo() {
    }

    public String getPatient_name() {
        return patient_name;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }

    public String getPatient_contactNum() {
        return patient_contactNum;
    }

    public void setPatient_contactNum(String patient_contactNum) {
        this.patient_contactNum = patient_contactNum;
    }

    public String getDoctor_name() {
        return doctor_name;
    }

    public void setDoctor_name(String doctor_name) {
        this.doctor_name = doctor_name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(String doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
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
