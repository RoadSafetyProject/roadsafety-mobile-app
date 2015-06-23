package com.RSMSA.policeApp.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by Ilakoze on 2/11/2015.
 */
public class Functions {
    private static final String TAG="Functions";
    /**
     * Obtain a String of The date in format
     * 24 JAN 2015 0900 Hrs
     * from a unix timestamp in long
     * @param time
     * @return
     */
    public static String getDateFromUnixTimestamp(long time){
        String dateString="";
        Calendar cl= Calendar.getInstance();
        cl.setTimeInMillis(time);

        dateString = DateFormat.format("yyyy-MM-dd", cl).toString();

        return dateString;
    }


    public static void displayPromptForEnablingGPS(final Activity activity){
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Enable either GPS or any other location"
                + " service to find current location.  Click OK to go to"
                + " location services settings to let you do so.";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                activity.startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }




    public static JSONArray generateJson(JSONArray header, JSONArray rows){
        int counter = rows.length();
        JSONArray jsonArray = new JSONArray();

        for(int i=0;i<counter;i++){
            JSONObject object = new JSONObject();
            JSONArray row = null;
            try {
                row = rows.getJSONArray(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int count = row.length();
            for(int j = 0; j<count;j++) {
                try {
                    object.put(header.getJSONObject(j).getString("column"), row.getString(j));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            try {
                jsonArray.put(i,object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

}
