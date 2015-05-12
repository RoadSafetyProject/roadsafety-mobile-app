package com.RSMSA.policeApp.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;

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

}
