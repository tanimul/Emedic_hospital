package com.example.e_madic2.Model;

public class Ambulance_info {
    String name, ambulance_contact_number,area,hospital_id;


    public Ambulance_info() {
    }

    public Ambulance_info(String name, String ambulance_contact_number,String area,String hospital_id) {

        this.name = name;
        this.ambulance_contact_number = ambulance_contact_number;
        this.area=area;
        this.hospital_id=hospital_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmbulance_contact_number() {
        return ambulance_contact_number;
    }

    public void setAmbulance_contact_number(String ambulance_contact_number) {
        this.ambulance_contact_number = ambulance_contact_number;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getHospital_id() {
        return hospital_id;
    }

    public void setHospital_id(String hospital_id) {
        this.hospital_id = hospital_id;
    }
}
