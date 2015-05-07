package com.RSMSA.policeApp.Models;

/**
 * Created by Ilakoze on 2/4/2015.
 */
public class Offence extends Model {
    private String offence_date;
    private String place="";
    private String facts="";
    private String latitude;
    private String longitude;
    private String vehicle_plate_number="";
    private String driver_license_number="";
    private String rank_no="";
    private boolean paid;
    private boolean admit;
    private String id="";
    private String Program_Driver="";
    private String Program_Police="";
    private String Program_Vehicle="";


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOffence_date() {
        return offence_date;
    }

    public void setOffence_date(String offence_date) {
        this.offence_date = offence_date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getFacts() {
        return facts;
    }

    public void setFacts(String facts) {
        this.facts = facts;
    }

    public String getVehicle_plate_number() {
        return vehicle_plate_number;
    }

    public void setVehicle_plate_number(String vehicle_plate_number) {
        this.vehicle_plate_number = vehicle_plate_number;
    }

    public String getDriver_license_number() {
        return driver_license_number;
    }

    public void setDriver_license_number(String driver_license_number) {
        this.driver_license_number = driver_license_number;
    }

    public String getRank_no() {
        return rank_no;
    }

    public void setRank_no(String rank_no) {
        this.rank_no = rank_no;
    }

    public boolean getPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public boolean getAdmit() {
        return admit;
    }

    public void setAdmit(boolean admit) {
        this.admit = admit;
    }

    public boolean isPaid() {
        return paid;
    }

    public boolean isAdmit() {
        return admit;
    }

    public String getProgram_Driver() {
        return Program_Driver;
    }

    public void setProgram_Driver(String program_Driver) {
        Program_Driver = program_Driver;
    }

    public String getProgram_Police() {
        return Program_Police;
    }

    public void setProgram_Police(String program_Police) {
        Program_Police = program_Police;
    }

    public String getProgram_Vehicle() {
        return Program_Vehicle;
    }

    public void setProgram_Vehicle(String program_Vehicle) {
        Program_Vehicle = program_Vehicle;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
