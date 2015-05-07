package com.RSMSA.policeApp.Models;

/**
 * Created by Ilakoze on 3/3/2015.
 */
public class Accident extends Model {
    private String accident_regNumber="";
    private String area_name="";
    private String district="";
    private String city="";
    private String video_filename="";
    private String video_filepath="";

    public String getVideo_filename() {
        return video_filename;
    }

    public void setVideo_filename(String video_filename) {
        this.video_filename = video_filename;
    }

    public String getImage_filename() {
        return image_filename;
    }

    public void setImage_filename(String image_filename) {
        this.image_filename = image_filename;
    }

    private String image_filename="";
    private String image_filepath="";
    private String road_name="";
    private String intersection_name="";
    private String accident_type="";
    private String fatal="",
            severe_injured="",
            simple="",
            only_damage="",
            road_number="",
            intersection_number="",
            road_km_mark="",
            intersection_km_mark="";
    private String junction_structure="",
            junction_control="",
            road_type="",
            surface_type="",
            road_structure="",
            surface_status="",
            road_surface="",
            light="",
            weather="",
            control="";
    private String rank_no="";
    private double latitude,longitude;

    public String getRank_no() {
        return rank_no;
    }

    public void setRank_no(String rank_no) {
        this.rank_no = rank_no;
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

    private String time;
    private String police_signature="";
    private String police_signatureFilePath="";
    private String description="";

    public String getVideo_filepath() {
        return video_filepath;
    }

    public void setVideo_filepath(String video_filepath) {
        this.video_filepath = video_filepath;
    }

    public String getImage_filepath() {
        return image_filepath;
    }

    public void setImage_filepath(String image_filepath) {
        this.image_filepath = image_filepath;
    }

    public String getPolice_signatureFilePath() {
        return police_signatureFilePath;
    }

    public void setPolice_signatureFilePath(String police_signatureFilePath) {
        this.police_signatureFilePath = police_signatureFilePath;
    }

    public String getDescriptionFilePath() {
        return descriptionFilePath;
    }

    public void setDescriptionFilePath(String descriptionFilePath) {
        this.descriptionFilePath = descriptionFilePath;
    }

    private String descriptionFilePath="";

    public String getAccident_regNumber() {
        return accident_regNumber;
    }

    public void setAccident_regNumber(String accident_regNumber) {
        this.accident_regNumber = accident_regNumber;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRoad_name() {
        return road_name;
    }

    public void setRoad_name(String road_name) {
        this.road_name = road_name;
    }

    public String getIntersection_name() {
        return intersection_name;
    }

    public void setIntersection_name(String intersection_name) {
        this.intersection_name = intersection_name;
    }

    public String getAccident_type() {
        return accident_type;
    }

    public void setAccident_type(String accident_type) {
        this.accident_type = accident_type;
    }

    public String getJunction_structure() {
        return junction_structure;
    }

    public void setJunction_structure(String junction_structure) {
        this.junction_structure = junction_structure;
    }

    public String getJunction_control() {
        return junction_control;
    }

    public void setJunction_control(String junction_control) {
        this.junction_control = junction_control;
    }

    public String getRoad_type() {
        return road_type;
    }

    public void setRoad_type(String road_type) {
        this.road_type = road_type;
    }

    public String getSurface_type() {
        return surface_type;
    }

    public void setSurface_type(String surface_type) {
        this.surface_type = surface_type;
    }

    public String getRoad_structure() {
        return road_structure;
    }

    public void setRoad_structure(String road_structure) {
        this.road_structure = road_structure;
    }

    public String getSurface_status() {
        return surface_status;
    }

    public void setSurface_status(String surface_status) {
        this.surface_status = surface_status;
    }

    public String getRoad_surface() {
        return road_surface;
    }

    public void setRoad_surface(String road_surface) {
        this.road_surface = road_surface;
    }

    public String getLight() {
        return light;
    }

    public void setLight(String light) {
        this.light = light;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getControl() {
        return control;
    }

    public void setControl(String control) {
        this.control = control;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPolice_signature() {
        return police_signature;
    }

    public void setPolice_signature(String police_signature) {
        this.police_signature = police_signature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFatal() {
        return fatal;
    }

    public void setFatal(String fatal) {
        this.fatal = fatal;
    }

    public String getSevere_injured() {
        return severe_injured;
    }

    public void setSevere_injured(String severe_injured) {
        this.severe_injured = severe_injured;
    }

    public String getSimple() {
        return simple;
    }

    public void setSimple(String simple) {
        this.simple = simple;
    }

    public String getOnly_damage() {
        return only_damage;
    }

    public void setOnly_damage(String only_damage) {
        this.only_damage = only_damage;
    }

    public String getRoad_number() {
        return road_number;
    }

    public void setRoad_number(String road_number) {
        this.road_number = road_number;
    }

    public String getIntersection_number() {
        return intersection_number;
    }

    public void setIntersection_number(String intersection_number) {
        this.intersection_number = intersection_number;
    }

    public String getRoad_km_mark() {
        return road_km_mark;
    }

    public void setRoad_km_mark(String road_km_mark) {
        this.road_km_mark = road_km_mark;
    }

    public String getIntersection_km_mark() {
        return intersection_km_mark;
    }

    public void setIntersection_km_mark(String intersection_km_mark) {
        this.intersection_km_mark = intersection_km_mark;
    }
}
