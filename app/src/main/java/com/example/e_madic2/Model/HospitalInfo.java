package com.example.e_madic2.Model;

public class HospitalInfo {

    private String address, category, confirm_password, contact_number, hospital_name, hospital_unique_id, open_hour, password,hospital_id;
    private Double latitude, longitude;

    public HospitalInfo() {
    }

    public HospitalInfo(String password, String address, String hospital_unique_id, String category, String open_hour,
                        String contact_number, String confirm_password, String hospital_name, Double latitude, Double longitude,String hospital_id) {
        this.password = password;
        this.address = address;
        this.hospital_unique_id = hospital_unique_id;
        this.category = category;
        this.open_hour = "Open 24 Hour";
        this.contact_number = contact_number;
        this.confirm_password = confirm_password;
        this.hospital_name = hospital_name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.hospital_id=hospital_id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getHospital_name() {
        return hospital_name;
    }

    public void setHospital_name(String hospital_name) {
        this.hospital_name = hospital_name;
    }

    public String getHospital_unique_id() {
        return hospital_unique_id;
    }

    public void setHospital_unique_id(String hospital_unique_id) {
        this.hospital_unique_id = hospital_unique_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getOpen_hour() {
        return open_hour;
    }

    public void setOpen_hour(String open_hour) {
        this.open_hour = open_hour;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirm_password() {
        return confirm_password;
    }

    public void setConfirm_password(String confirm_password) {
        this.confirm_password = confirm_password;
    }

    public String getHospital_id() {
        return hospital_id;
    }

    public void setHospital_id(String hospital_id) {
        this.hospital_id = hospital_id;
    }
}
