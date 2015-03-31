package com.RSMSA.policeApp.Utils;

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

        dateString = DateFormat.format("dd-MM-yyyy", cl).toString();

        return dateString;
    }
}
