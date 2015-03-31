package com.RSMSA.policeApp.Models;

/**
 * Created by Ilakoze on 3/3/2015.
 */
public class PassengerVehicle extends Model {
    private String name="",
            gender="",
            date_of_birth="",
            physical_address="",
            address="",
            national_id="",
            phone_no="",
            casualty="";
    private boolean helmet;
    private int alcohol_percent;

    public int getAlcohol_percent() {
        return alcohol_percent;
    }

    public void setAlcohol_percent(int alcohol_percent) {
        this.alcohol_percent = alcohol_percent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getPhysical_address() {
        return physical_address;
    }

    public void setPhysical_address(String physical_address) {
        this.physical_address = physical_address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNational_id() {
        return national_id;
    }

    public void setNational_id(String national_id) {
        this.national_id = national_id;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }


    public String getCasualty() {
        return casualty;
    }

    public void setCasualty(String casualty) {
        this.casualty = casualty;
    }

    public boolean isHelmet() {
        return helmet;
    }

    public void setHelmet(boolean helmet) {
        this.helmet = helmet;
    }
}
