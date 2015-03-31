package com.RSMSA.policeApp.Models;

/**
 * Created by Ilakoze on 3/3/2015.
 */
public class AccidentVehicle extends Model {
    private int fatal;
    private int severe_injured;
    private int simple;
    private int only_damage;
    private int vehicle;
    private int vehicle_total;
    private int infastructure;
    private int cost;
    private int alcohol_percentage;

    private String licence_no="",plate_number="",estimated_repair="";
    private boolean drug,phone_use,healmet;
    private String violations="", defects="";
    private String signature="";

    public String getSignatureFilePath() {
        return signatureFilePath;
    }

    public void setSignatureFilePath(String signatureFilePath) {
        this.signatureFilePath = signatureFilePath;
    }

    private String signatureFilePath="";

    public int getAlcohol_percentage() {
        return alcohol_percentage;
    }

    public void setAlcohol_percentage(int alcohol_percentage) {
        this.alcohol_percentage = alcohol_percentage;
    }

    public int getFatal() {
        return fatal;
    }

    public void setFatal(int fatal) {
        this.fatal = fatal;
    }

    public int getSevere_injured() {
        return severe_injured;
    }

    public void setSevere_injured(int severe_injured) {
        this.severe_injured = severe_injured;
    }

    public int getSimple() {
        return simple;
    }

    public void setSimple(int simple) {
        this.simple = simple;
    }

    public int getOnly_damage() {
        return only_damage;
    }

    public void setOnly_damage(int only_damage) {
        this.only_damage = only_damage;
    }

    public int getVehicle() {
        return vehicle;
    }

    public void setVehicle(int vehicle) {
        this.vehicle = vehicle;
    }

    public int getVehicle_total() {
        return vehicle_total;
    }

    public void setVehicle_total(int vehicle_total) {
        this.vehicle_total = vehicle_total;
    }

    public int getInfastructure() {
        return infastructure;
    }

    public void setInfastructure(int infastructure) {
        this.infastructure = infastructure;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getLicence_no() {
        return licence_no;
    }

    public void setLicence_no(String licence_no) {
        this.licence_no = licence_no;
    }

    public String getPlate_number() {
        return plate_number;
    }

    public void setPlate_number(String plate_number) {
        this.plate_number = plate_number;
    }

    public String getEstimated_repair() {
        return estimated_repair;
    }

    public void setEstimated_repair(String estimated_repair) {
        this.estimated_repair = estimated_repair;
    }

    public boolean isDrug() {
        return drug;
    }

    public void setDrug(boolean drug) {
        this.drug = drug;
    }

    public boolean isPhone_use() {
        return phone_use;
    }

    public void setPhone_use(boolean phone_use) {
        this.phone_use = phone_use;
    }

    public boolean isHealmet() {
        return healmet;
    }

    public void setHealmet(boolean healmet) {
        this.healmet = healmet;
    }

    public String getViolations() {
        return violations;
    }

    public void setViolations(String violations) {
        this.violations = violations;
    }

    public String getDefects() {
        return defects;
    }

    public void setDefects(String defects) {
        this.defects = defects;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
