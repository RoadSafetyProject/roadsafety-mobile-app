package com.RSMSA.policeApp;

/**
 * Created by Isaiah on 8/21/2014.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Database {

    SQLiteDatabase db ;
    private static final String TAG = "Database";

    public Database(Context context){

        db = context.openOrCreateDatabase("rsmsa",context.MODE_PRIVATE,null);

        db.execSQL("CREATE TABLE IF NOT EXISTS tbl_license (" +
                "id INT AUTO INCREMENT," +
                "license_number VARCHAR ," +
                "name VARCHAR,"+
                "address VARCHAR,"+
                "license_type VARCHAR,"+
                "expiry_date VARCHAR)");

        db.execSQL("CREATE TABLE IF NOT EXISTS tbl_offenses ("+
                "id INT AUTO INCREMENT ,"+
                "license VARCHAR ,"+
                "plate_number VARCHAR ,"+
                "offense_type VARCHAR ,"+
                "date_issued VARCHAR)");

        //initializeSampleData();
    }

    /**
     * a function to populate saple test data into the database
     */
    public void initializeSampleData(){

        /**
         * initializing sample data for licenses registered in our system
         */
        db.execSQL("INSERT INTO tbl_license VALUES ('','89688958586','Hassan','Box200Dar','D','29/june/2018')");
        db.execSQL("INSERT INTO tbl_license VALUES ('','9780330376792','Wilbur Smith','Monsoon','D','29/june/2018')");
        db.execSQL("INSERT INTO tbl_license VALUES ('','45367892976','John','Box200Arusha','c','29/may/2017')");
        db.execSQL("INSERT INTO tbl_license VALUES ('','78373673677','Doe','Box2112Dar','B','2/january/2015')");
        db.execSQL("INSERT INTO tbl_license VALUES ('','6164001011046','Deo','Box12Mwanza','F','2/may/2009')");
        db.execSQL("INSERT INTO tbl_license VALUES ('','4000282745','Kelvin Kulwa','Box200Arusha','c','29/may/2017')");
        db.execSQL("INSERT INTO tbl_license VALUES ('','4000109940','Henry Kalisti','Box2112Dar','D','2/january/2015')");
        db.execSQL("INSERT INTO tbl_license VALUES ('','8904084707928','Jason','Box200Dar','D','29/june/2018')");
        db.execSQL("INSERT INTO tbl_license VALUES ('','67363789974','Jude','Box2112Dar','B','2/january/2015')");


        /**
         * initialize sample offenses that already committed
         */
        db.execSQL("INSERT INTO tbl_offenses VALUES ('','4000109940','T 123 ADS','Over Speeding','2/january/2015')");
        db.execSQL("INSERT INTO tbl_offenses VALUES ('','90909827778','T 894 CDA','Red Light Crossing','29/june/2018')");
        db.execSQL("INSERT INTO tbl_offenses VALUES ('','67363789974','T 112 AAC','wrong parking','2/february/2080')");
        db.execSQL("INSERT INTO tbl_offenses VALUES ('','4000282745','T 908 BFG','Tyres','2/february/2080')");
        db.execSQL("INSERT INTO tbl_offenses VALUES ('','4000282745','T 918 CAR','wrong parking','2/february/2080')");
        db.execSQL("INSERT INTO tbl_offenses VALUES ('','4000109940','T 517 BJD','wrong parking','2/february/2080')");
        db.execSQL("INSERT INTO tbl_offenses VALUES ('','4000109940','T 212 AAA','wrong parking','2/february/2080')");
        db.execSQL("INSERT INTO tbl_offenses VALUES ('','8904084707928','T 324 ACX','wrong parking','2/february/2010')");



        Log.d(TAG, "Data Initialized successfully....");

    }

    public void execQuery(String sql){
        db.execSQL(sql);
    }

    public Cursor query(String sql){
        return db.rawQuery(sql,null);
    }

    public void close(){
        db.close();
    }

    public void delete(String tbl){
        db.execSQL("DROP TABLE "+tbl);

    }


}
