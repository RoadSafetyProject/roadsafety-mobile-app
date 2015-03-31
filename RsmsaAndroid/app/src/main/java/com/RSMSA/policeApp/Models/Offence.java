package com.RSMSA.policeApp.Models;

/**
 * Created by Ilakoze on 2/4/2015.
 */
public class Offence extends Model {
    private long offence_date;
    private String place="";
    private String facts="";
    private double latitude;
    private double longitude;
    private String vehicle_plate_number="";
    private String driver_license_number="";
    private String rank_no="";
    private boolean paid;
    private boolean admit;
    private String id="";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getOffence_date() {
        return offence_date;
    }

    public void setOffence_date(long offence_date) {
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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
}
