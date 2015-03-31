package com.RSMSA.policeApp.Models;

/**
 * Created by Ilakoze on 3/3/2015.
 */
public class Witness extends Model {
    private String name="",gender="",date_of_birth="",physical_address="",address="",national_id="",phone_no="";
    private String signature="";

    public String getSignatureFilePath() {
        return signatureFilePath;
    }

    public void setSignatureFilePath(String signatureFilePath) {
        this.signatureFilePath = signatureFilePath;
    }

    private String signatureFilePath="";

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

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
