package com.RSMSA.policeApp;


import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.RSMSA.policeApp.Dhis2.DHIS2Config;
import com.RSMSA.policeApp.Dhis2.DHIS2Modal;
import com.RSMSA.policeApp.Dhis2.Data.DataElements;
import com.RSMSA.policeApp.Dhis2.Data.Program;
import com.RSMSA.policeApp.Dialogues.PaymentConfirmationDialogue;
import com.RSMSA.policeApp.Fragments.OffenceHistoryFragment;
import com.RSMSA.policeApp.Fragments.PaymentVerifierFragment;
import com.RSMSA.policeApp.Fragments.ReportAccidentsFragment;
import com.RSMSA.policeApp.Utils.CameraActivity;
import com.RSMSA.policeApp.Utils.SystemBarTintManager;
import com.RSMSA.policeApp.iRoadDB.IroadDatabase;
import com.suredigit.inappfeedback.FeedbackDialog;
import com.suredigit.inappfeedback.FeedbackSettings;

import com.RSMSA.policeApp.Adapters.DrawerListCustomAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *  Created by Ilakoze on 16/01/2015.
 */

public class MainOffence extends CameraActivity implements PaymentConfirmationDialogue.OnCompleteListener {
    private IroadDatabase db;
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
    public static final String MyPREF  = "RoadSafetyApp";
    public static final String list = "offense_list_present";
    public static int selection=0;
    public static FeedbackDialog mFeedbackDialog;
    private static final String KEY="AF-3065226AE0E7-23";
    public static List<Program> programs = new ArrayList<>();
    public static List<DataElements> dataElements = new ArrayList<>();
    static final int LOGIN_REQUEST = 300;
    public static String username;
    public static String password;
    public static String orgUnit,userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new IroadDatabase(this);

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

        sharedpreferences=getSharedPreferences(MyPREF,Context.MODE_PRIVATE);
        String userName= sharedpreferences.getString("username","");
        String passWord = sharedpreferences.getString("password","");

        if (userName.equals("")) {
            Log.d(TAG, "app running for the first time");
            Intent LoginActivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivityForResult(LoginActivity, LOGIN_REQUEST);
            overridePendingTransition(R.anim.activity_slide_in_left, R.anim.activity_slide_out_left);

        }else {
            username = userName;
            password = passWord;
            orgUnit = sharedpreferences.getString("orgUnit","");
            userId = sharedpreferences.getString("userId","");
            Log.d(TAG,"police_uid = "+userId);
            Cursor cursor=db.query("SELECT * FROM " + IroadDatabase.TABLE_PROGRAMS);
            int counter=cursor.getCount();
            for(int i=0;i<counter;i++){
                cursor.moveToPosition(i);
                Program program= new Program();
                program=(Program)program.setModel(cursor,program);
                Log.d(TAG,"adding program "+program.getName()+" with uid = "+program.getId());
                programs.add(program);
            }

            Cursor cursor2=db.query("SELECT * FROM "+IroadDatabase.TABLE_DATA_ELEMENTS);
            int count=cursor2.getCount();
            for(int i=0;i<count;i++){
                cursor2.moveToPosition(i);
                DataElements dataElement= new DataElements();
                dataElement=(DataElements)dataElement.setModel(cursor2,dataElement);
                Log.d(TAG,"adding dataElement "+dataElement.getName()+" with uid = "+dataElement.getId());
                dataElements.add(dataElement);
            }

            Fragment newFragment = new OffenceHistoryFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.activityMain_content_frame, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }



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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            Fragment fragment = getFragmentManager().findFragmentById(R.id.activityMain_content_frame);
            fragment.onActivityResult(requestCode, resultCode, data);
        }catch (Exception e){}
        if (requestCode == LOGIN_REQUEST) {
            Log.d(TAG,"results received result code = "+resultCode);
            if(resultCode == RESULT_OK){
                Log.d(TAG,"results received");
                password=data.getStringExtra("password");
                username=data.getStringExtra("username");
                orgUnit=data.getStringExtra("orgUnit");
                userId=data.getStringExtra("userId");

                Log.d(TAG,"police_uid = "+userId);
                Fragment newFragment = new OffenceHistoryFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.activityMain_content_frame, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout ) {
            SharedPreferences sharedpreferences = getSharedPreferences
                    (MyPREF, Context.MODE_PRIVATE);
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
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
