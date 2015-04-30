package com.RSMSA.policeApp.iRoadDB;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IroadDatabase extends SQLiteOpenHelper {
    private static  final String TAG= IroadDatabase.class.getSimpleName();
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "iroad";
    //  table name
    public static final String TABLE_OFFENCE_REGISTRY = "tbl_offence_registry";
    public static final String TABLE_PROGRAMS= "tbl_programs";
    public static final String TABLE_DATA_ELEMENTS= "tbl_data_elements";


    //  Table Columns offence
    public static final String KEY_ID = "id";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_NATURE = "nature";
    public static final String KEY_SECTION = "section";
    public static final String KEY_AMOUNT = "amount";

    public static final String KEY_NAME= "name";
    public static final String KEY_CREATED = "created";
    public static final String KEY_LAST_UPDATED = "lastUpdated";
    public static final String KEY_HREF = "href";


    public IroadDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_PROGRAMS = "CREATE TABLE if not exists " + TABLE_PROGRAMS + "("
                + KEY_ID + " STRING PRIMARY KEY ,"
                + KEY_NAME + " VARCHAR,"
                + KEY_CREATED + " VARCHAR,"
                + KEY_LAST_UPDATED + " VARCHAR,"
                + KEY_HREF + " VARCHAR" + ")";

        String CREATE_TABLE_DATA_ELEMENTS = "CREATE TABLE if not exists " + TABLE_DATA_ELEMENTS + "("
                + KEY_ID + " STRING PRIMARY KEY ,"
                + KEY_NAME + " VARCHAR,"
                + KEY_CREATED + " VARCHAR,"
                + KEY_LAST_UPDATED + " VARCHAR,"
                + KEY_HREF + " VARCHAR" + ")";

        String CREATE_TABLE_OFFENCE_REGISTRY = "CREATE TABLE if not exists " + TABLE_OFFENCE_REGISTRY + "("
                + KEY_ID + " STRING PRIMARY KEY ,"
                + KEY_DESCRIPTION + " VARCHAR,"
                + KEY_NATURE + " VARCHAR,"
                + KEY_SECTION + " VARCHAR,"
                + KEY_AMOUNT + " VARCHAR" + ")";

        db.execSQL(CREATE_TABLE_OFFENCE_REGISTRY);
        db.execSQL(CREATE_TABLE_PROGRAMS);
        db.execSQL(CREATE_TABLE_DATA_ELEMENTS);
    }
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OFFENCE_REGISTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROGRAMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA_ELEMENTS);
        onCreate(db);
    }

    /**
     * Getting offenseNature data from database
     * */
    public List<String> getAllOffenceDetails(boolean flag) {

        String selectQuery = "SELECT  * FROM " + TABLE_OFFENCE_REGISTRY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<String> listOfOffenceNature = new ArrayList<String>();
        List<String> listOfOffenceDescriptions = new ArrayList<String>();
        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                listOfOffenceNature.add(cursor.getString(cursor.getColumnIndex(KEY_NATURE)));
                listOfOffenceDescriptions.add(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
                cursor.moveToNext();

            }
        }
        cursor.close();
        db.close();
        if (flag){
            return listOfOffenceDescriptions;
        }

        else {
            return listOfOffenceNature;
        }
    }

    public String getAnOffenceDetail(boolean flag, String uid) {

        String selectQuery = "SELECT  * FROM " + TABLE_OFFENCE_REGISTRY + " WHERE "+KEY_ID+" = '"+uid+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.d("tag", "Cursor size is " + cursor.getCount());
        String offenceNature = "";
        String offenceDescription = "";
        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            offenceNature=cursor.getString(cursor.getColumnIndex(KEY_NATURE));
            offenceDescription=cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION));
        }
        cursor.close();
        db.close();
        if (flag){
            return offenceDescription;
        }

        else {
            return offenceNature;
        }
    }

    /**
     * Getting offenseNature data from database
     * */
    public List<String> getOffenceUIds() {

        String selectQuery = "SELECT  * FROM " + TABLE_OFFENCE_REGISTRY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.d("tag", "Cursor size is " + cursor.getCount());
        List<String> ids = new ArrayList<String>();
        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                ids.add(cursor.getString(cursor.getColumnIndex(KEY_ID)));
                cursor.moveToNext();

            }
        }
        cursor.close();
        db.close();

        return ids;
    }


    public void insert(String table,String nullCollumnHack,ContentValues contentValues){
        Log.d(TAG,"inserting into the database");
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.insert(table,nullCollumnHack,contentValues);
        }catch (SQLException e) {
            Log.i(TAG,e.getMessage());
        }
    }


    public Cursor query(String sql) {
        String query = sql;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

}
