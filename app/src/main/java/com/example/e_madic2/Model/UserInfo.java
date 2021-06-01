package com.example.e_madic2.Model;

public class UserInfo {
    String image_url,name,address,blood_grp, contact_number,medical_condition;

    public UserInfo() {
    }

    public UserInfo(String image_url, String name, String address, String blood_grp, String contact_number,
                    String medical_condition) {
        this.image_url = image_url;
        this.name = name;
        this.address = address;
        this.blood_grp = blood_grp;
        this.contact_number = contact_number;
        this.medical_condition = medical_condition;

    }


    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBlood_grp() {
        return blood_grp;
    }

    public void setBlood_grp(String blood_grp) {
        this.blood_grp = blood_grp;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getMedical_condition() {
        return medical_condition;
    }

    public void setMedical_condition(String medical_condition) {
        this.medical_condition = medical_condition;
    }
}
