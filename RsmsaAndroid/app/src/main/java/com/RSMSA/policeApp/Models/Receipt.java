package com.RSMSA.policeApp.Models;

/**
 * Created by Ilakoze on 2/11/2015.
 */
public class Receipt extends Model {
    private String amount;
    private long  date;
    private String receipt_number;
    private String payment_mode="";
    private String full_Name="";
    private String gender="";
    private String driver_license_number="";
    private String vehicle_owner_name="";
    private String vehicle_plate_number="";
    private String Offence_registry_list="";

    public String getFull_Name() {
        return full_Name;
    }

    public void setFull_Name(String full_Name) {
        this.full_Name = full_Name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDriver_license_number() {
        return driver_license_number;
    }

    public void setDriver_license_number(String driver_license_number) {
        this.driver_license_number = driver_license_number;
    }

    public String getVehicle_owner_name() {
        return vehicle_owner_name;
    }

    public void setVehicle_owner_name(String vehicle_owner_name) {
        this.vehicle_owner_name = vehicle_owner_name;
    }

    public String getVehicle_plate_number() {
        return vehicle_plate_number;
    }

    public void setVehicle_plate_number(String vehicle_plate_number) {
        this.vehicle_plate_number = vehicle_plate_number;
    }

    public String getOffence_registry_list() {
        return Offence_registry_list;
    }

    public void setOffence_registry_list(String offence_registry_list) {
        Offence_registry_list = offence_registry_list;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getReceipt_number() {
        return receipt_number;
    }

    public void setReceipt_number(String receipt_number) {
        this.receipt_number = receipt_number;
    }
}
