package com.RSMSA.policeApp;

import android.annotation.TargetApi;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;

import com.RSMSA.policeApp.Utils.SystemBarTintManager;

/**
 * Importing the location classes
 */


/**
 *  Created by Isaiah on 8/25/2014.
 */
public class PaymentVerifier extends ActionBarActivity{
    public static final String TAG="PaymentVerifier";
    public Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_offence);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        ColorDrawable colorDrawable= new ColorDrawable(getResources().getColor(R.color.blue_900));
        tintManager.setTintDrawable(colorDrawable);






    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }



}
