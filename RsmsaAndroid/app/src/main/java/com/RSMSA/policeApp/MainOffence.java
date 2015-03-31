package com.RSMSA.policeApp;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.RSMSA.policeApp.Fragments.OffenceHistoryFragment;
import com.RSMSA.policeApp.Fragments.PaymentVerifierFragment;
import com.RSMSA.policeApp.Fragments.ReportAccidentsFragment;
import com.RSMSA.policeApp.Models.Offence;
import com.RSMSA.policeApp.Utils.CameraActivity;
import com.RSMSA.policeApp.Utils.SystemBarTintManager;
import com.google.zxing.integration.android.IntentIntegrator;
import com.suredigit.inappfeedback.FeedbackDialog;
import com.suredigit.inappfeedback.FeedbackSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import adapters.DrawerListCustomAdapter;
import adapters.GridViewAdapter;

/**
 *  Created by Ilakoze on 16/01/2015.
 */

public class MainOffence extends CameraActivity implements PaymentConfirmationDialogue.OnCompleteListener {
    public static Database db;
    public static final String TAG = MainOffence.class.getSimpleName();
    public static Typeface Roboto_Condensed, Roboto_Black, Roboto_Light, Roboto_BoldCondensedItalic,
            Roboto_BoldCondensed, Rosario_Regular, Rosario_Bold,Roboto_Bold, Rosario_Italic,
            Roboto_Regular, Roboto_Medium;
    public Toolbar toolbar;
    private DrawerListCustomAdapter drawerListCustomAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private static DrawerLayout mDrawerLayout;
    public static ListView mDrawerList;
    private RelativeLayout content;
    public static String[] DrawerList;

    private static RelativeLayout mDrawer;
    private TextView drawerTitle;
    private SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES  = "MyPrefs";
    public static final String MyPREF  = "RoadSafetyApp";
    public static final String list = "offense_list_present";
    public static int selection=0;
    public static FeedbackDialog mFeedbackDialog;
    private static final String KEY="AF-8AC523F9D5B4-FA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /**
         * Initializing the send feedback dialogue
         * to send or receive feedback from the developers
         */
        FeedbackSettings feedbackSettings=new FeedbackSettings();
        feedbackSettings.setYourComments("Type your feedback here...");
        feedbackSettings.setRadioButtons(false); // Disables radio buttons
        feedbackSettings.setToast("Thank you so much!");
        feedbackSettings.setIdeaLabel("Bug");
        feedbackSettings.setBugLabel("Idea");
        feedbackSettings.setQuestionLabel("Question");
        feedbackSettings.setReplyTitle("From iRoad Developers");
        feedbackSettings.setText("Thank you for using iRoad. We would love to hear from you. Leave a comment to help us improve your experience");
        feedbackSettings.setGravity(Gravity.CENTER);
        feedbackSettings.setReplyCloseButtonText("Close");
        feedbackSettings.setReplyRateButtonText("RATE!");

        mFeedbackDialog = new FeedbackDialog(this,KEY);
        mFeedbackDialog.setDebug(true);
        mFeedbackDialog.setSettings(feedbackSettings);

        final DatabaseHandlerOffence db1 = new DatabaseHandlerOffence(getApplicationContext());
        sharedpreferences=getSharedPreferences(MyPREF,Context.MODE_PRIVATE);
        if (!sharedpreferences.contains(list)) {
            Log.d(TAG, "app running for the first time");
            /**
             * adding user information to shared preferences
             */
            SharedPreferences.Editor editor = sharedpreferences.edit();
            String name = "Vincent Minde";
            String rank_no = "R6478";
            String station = "Kijitonyama";
            editor.putString("name", name);
            editor.putString("rank_no", rank_no);
            editor.putString("station", station);

            editor.commit();

            new AsyncTask<Void,Void,Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    ObtainingOffenceListFromServerActivity object = new ObtainingOffenceListFromServerActivity();
                    object.createOffence(db1);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    sharedpreferences.edit().putString(list, "v").commit();
                }
            }.execute();

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        ColorDrawable colorDrawable= new ColorDrawable(getResources().getColor(R.color.blue_900));
        tintManager.setTintDrawable(colorDrawable);


        /**
         * Type faces used for setting fonts thoughout the app
         */
        Roboto_Light= Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        Roboto_Black= Typeface.createFromAsset(getAssets(), "Roboto-Black.ttf");
        Roboto_Condensed= Typeface.createFromAsset(getAssets(), "Roboto-Condensed.ttf");
        Roboto_BoldCondensedItalic= Typeface.createFromAsset(getAssets(), "Roboto-BoldCondensedItalic.ttf");
        Roboto_BoldCondensed= Typeface.createFromAsset(getAssets(), "Roboto-BoldCondensed.ttf");
        Roboto_Regular= Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        Roboto_Medium= Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");
        Rosario_Regular= Typeface.createFromAsset(getAssets(), "Rosario-Regular.ttf");
        Rosario_Italic= Typeface.createFromAsset(getAssets(), "Rosario-Italic.ttf");
        Rosario_Bold= Typeface.createFromAsset(getAssets(), "Rosario-Bold.ttf");
        Roboto_Bold= Typeface.createFromAsset(getAssets(), "Roboto-Bold.ttf");

        content=(RelativeLayout)findViewById(R.id.activityMain_content_frame);
        drawerTitle=(TextView)findViewById(R.id.drawer_title);
        DrawerList=this.getResources().getStringArray(R.array.drawer_list);
        mDrawer = (RelativeLayout) findViewById(R.id.left_drawer);
        mDrawerList=(ListView)findViewById(R.id.drawer_list);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerSlide (View drawerView, float slideOffset){
                int alpha_value=(int)(slideOffset *  255);
                float MIN_SCALE = 0.93f;
                content.setAlpha(1 - slideOffset*2/3);
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        drawerListCustomAdapter=new DrawerListCustomAdapter(this,DrawerList);
        mDrawerList.setAdapter(drawerListCustomAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        drawerTitle.setText(DrawerList[1]);

        Fragment newFragment = new OffenceHistoryFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.activityMain_content_frame, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        //DatabaseHandlerOffence databaseHandlerOffence = new DatabaseHandlerOffence(this);
        //List<String> list1=databaseHandlerOffence.getOffenceNature(false);


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

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout ) {
            SharedPreferences sharedpreferences = getSharedPreferences
                    (LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.commit();
            Intent intent = new Intent(MainOffence.this, LoginActivity.class);
            this.startActivity(intent);
            MainOffence.this.finish();
        }

        if (item.getItemId() == R.id.action_changePasswd ) {
            Intent intent = new Intent(MainOffence.this, ChangePassword.class);
            startActivity(intent);

        }

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        // boolean drawerOpen = mDrawer.isDrawerOpen(mDrawerList);
        // menu.findItem(R.id.new_refresh).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            LoginActivity.justBack = true;
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"results received");
        Fragment fragment = getFragmentManager().findFragmentById(R.id.activityMain_content_frame);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onComplete(boolean saved, int index) {
        PaymentVerifierFragment fragment = (PaymentVerifierFragment)getFragmentManager().findFragmentById(R.id.activityMain_content_frame);
        fragment.onComplete(saved, index);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mFeedbackDialog.dismiss();
    }

    /**
     * The click listener for ListView in the navigation drawer
     **/
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {
            System.gc();
            Log.d(TAG, "Drawer item clicked is = " + DrawerList[position]);
            selection=position;
            if(position==0){
                mDrawerLayout.closeDrawer(mDrawer);
                new AsyncTask<Void,Void,Void>(){
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            Thread.sleep(280);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                    @Override
                    public void onPostExecute(Void v){
                        drawerTitle.setText(DrawerList[position]);
                        Fragment newFragment = new OffenceHistoryFragment();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.activityMain_content_frame, newFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }else if(position==1){
                mDrawerLayout.closeDrawer(mDrawer);
                new AsyncTask<Void,Void,Void>(){
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            Thread.sleep(280);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                    @Override
                    public void onPostExecute(Void v){
                        drawerTitle.setText(DrawerList[position]);
                        Fragment newFragment = new PaymentVerifierFragment();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.activityMain_content_frame, newFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }else if(position==2){
                mDrawerLayout.closeDrawer(mDrawer);
                new AsyncTask<Void,Void,Void>(){
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            Thread.sleep(280);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                    @Override
                    public void onPostExecute(Void v){
                        drawerTitle.setText(DrawerList[position]);
                        Fragment newFragment = new ReportAccidentsFragment();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.activityMain_content_frame, newFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }else if(position==3){
                mDrawerLayout.closeDrawer(mDrawer);
                new AsyncTask<Void,Void,Void>(){
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            Thread.sleep(280);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                    @Override
                    public void onPostExecute(Void v){
                        mFeedbackDialog.show();
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            mDrawerList.setItemChecked(position, true);
        }
    }


}
