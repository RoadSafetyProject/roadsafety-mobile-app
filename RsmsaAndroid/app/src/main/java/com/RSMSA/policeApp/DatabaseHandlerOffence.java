package com.RSMSA.policeApp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseHandlerOffence extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "psms";
    //  table name
    private static final String TABLE_USER = "user";
    private static final String TABLE_OFFENCE = "offence";
    private static final String TABLE_OFFENCE_N = "natureOfOffence";
    //  Table Columns offence
    private static final String KEY_ID = "id";
    private static final String KEY_LICENSE = "license";
    private static final String KEY_PLATE_NUMBER = "plateNumber";
    private static final String KEY_COMMIT = "comit";
    private static final String KEY_OFFENCE_NATURE = "offence";
    private static final String KEY_ISSUER = "rankNo";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    //table columns user
    private static final String KEY_USERNAME = "rankNo";
    private static final String KEY_FULLNAME = "fullName";
    private static final String KEY_STATION = "station";
    private static final String CREATED_AT = "created_at";
    //table column of nature
    static final String KEY_NAME = "natureOfOffence";
    static final String KEY_RELATING = "relating";


    public DatabaseHandlerOffence(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_OFFENCE_TABLE = "CREATE TABLE if not exists " + TABLE_OFFENCE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_LICENSE + " TEXT,"
                + KEY_PLATE_NUMBER + " TEXT,"
                + KEY_OFFENCE_NATURE + " TEXT,"
                + KEY_COMMIT + " TEXT,"
                + KEY_ISSUER  + " TEXT,"
                + KEY_AMOUNT  + " TEXT,"
                + KEY_LATITUDE  + " TEXT,"
                + KEY_LONGITUDE  + " TEXT,"
                + KEY_CREATED_AT + " TEXT" + ")";
        db.execSQL(CREATE_OFFENCE_TABLE);

        String CREATE_USER_TABLE = "CREATE TABLE if not exists " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USERNAME + " TEXT,"
                + KEY_FULLNAME + " TEXT,"
                + KEY_STATION + " TEXT,"
                + CREATED_AT + " TEXT" + ")";
        db.execSQL(CREATE_USER_TABLE);

        db.execSQL("CREATE TABLE if not exists natureOfOffence ("
                + "id INTEGER PRIMARY KEY,"
                + "natureOfOffence TEXT,"
                + "relating TEXT,"
                + "amount INTEGER"
                + ");");
    }
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OFFENCE);
        // Create tables again
        onCreate(db);
    }

    /**
     *
     * @param license
     * @param plateNumber
     *
     * @param offence
     *
     * @param created_at
     */

    public void addOffence(String license, String offence, String plateNumber,String commit,  String created_at, String RankNO, String amount, String latitude, String longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LICENSE, license); // license
        values.put(KEY_PLATE_NUMBER, plateNumber); // plateNumber
        values.put(KEY_COMMIT, commit); // commit
        values.put(KEY_OFFENCE_NATURE, offence); // offence
        values.put(KEY_ISSUER, RankNO); // RankNO
        values.put(KEY_AMOUNT, amount); // RankNO
        values.put(KEY_LATITUDE, latitude);//latitudde
        values.put(KEY_LONGITUDE, longitude);//longitude
        values.put(KEY_CREATED_AT, created_at); // Created At
        // Inserting Row
        db.insert(TABLE_OFFENCE, null, values);
        db.close(); // Closing database connection
    }

    /**
     *
     * @param rankNo
     * @param fullName
     * @param station
     * @param created_at
     */

    public void addUser(String rankNo, String fullName, String station,  String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, rankNo); // rankno
        values.put(KEY_FULLNAME, fullName); // fullname
        values.put(KEY_STATION, station); // station
        values.put(CREATED_AT, created_at); // Created At
        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection
    }

    public void addNature(String id,String name,String amount, String relating){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, id); // id of offence
        values.put(KEY_NAME, name); // nature of offence
        values.put(KEY_AMOUNT, amount); // amount of offence
        values.put(KEY_RELATING, relating); // relating to either motor vehicle or tricycle
        // Inserting Row
        db.insert(TABLE_OFFENCE_N, null, values);

        db.close(); // Closing database connection
    }

    /**
     * Getting offense data from database
     * */
    public HashMap<String, String> getOffenceDetails(){
        HashMap<String, String> offence = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_OFFENCE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToLast();
        if(cursor.getCount() > 0){
            offence.put("license", cursor.getString(1));
            offence.put("plateNumber", cursor.getString(2));
            offence.put("offence", cursor.getString(3));
            offence.put("commit", cursor.getString(4));
            offence.put("rankNo", cursor.getString(5));
            offence.put("amount", cursor.getString(6));
            offence.put("created_at", cursor.getString(7));
        }
        cursor.close();
        db.close();
        // return an offence detail
        return offence;
    }

    /**
     * Getting offenseNature data from database
     * */
    public List<String> getOffenceNature(boolean flag) {

        String selectQuery = "SELECT  * FROM " + TABLE_OFFENCE_N;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.d("tag", "Cursor size is " + cursor.getCount());
        List<String> list_of_offence = new ArrayList<String>();
        List<String> relating = new ArrayList<String>();
        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {

                list_of_offence.add(cursor.getString(1));
                relating.add(cursor.getString(3));
                cursor.moveToNext();

            }
        }
        cursor.close();
        db.close();
        // return an offence detail
        if (flag){
            return relating;
        }

        else {
            return list_of_offence;
        }
    }

    public String getAnOffenceNature(boolean flag, int id) {

        String selectQuery = "SELECT  * FROM " + TABLE_OFFENCE_N + " WHERE "+KEY_ID+" = '"+id+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.d("tag", "Cursor size is " + cursor.getCount());
        String list_of_offence = "";
        String relating = "";
        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            list_of_offence=cursor.getString(1);
            relating=cursor.getString(3);
        }
        cursor.close();
        db.close();
        // return an offence detail
        if (flag){
            return relating;
        }

        else {
            return list_of_offence;
        }
    }


    /**
     * Getting offenseNature data from database
     * */
    public List<Integer> getOffenceIds() {

        String selectQuery = "SELECT  * FROM " + TABLE_OFFENCE_N;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.d("tag", "Cursor size is " + cursor.getCount());
        List<Integer> ids = new ArrayList<Integer>();
        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                ids.add(cursor.getInt(0));
                cursor.moveToNext();

            }
        }
        cursor.close();
        db.close();

        return ids;
    }



    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> User = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToLast();
        if(cursor.getCount() > 0){
            User.put("rankNo", cursor.getString(1));
            User.put("fullName", cursor.getString(2));
            User.put("station", cursor.getString(3));
            User.put("created_at", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return an user detail
        return User;
    }


    /**
     * Getting user login status
     * return true if rows are there in table
     * */
    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_OFFENCE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();
        // return row count
        return rowCount;
    }
    /**
     * Getting user login status
     * return true if rows are there in table
     * */
    public int getOffenceCount() {
        String countQuery = "SELECT  natureOfOffence FROM " + TABLE_OFFENCE_N;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();
        // return row count
        return rowCount;
    }
    /**
     * Re create database
     * Delete all tables and create them again
     * */
    public void resetTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();
    }
}
