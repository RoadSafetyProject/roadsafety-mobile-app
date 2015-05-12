package com.RSMSA.policeApp;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.RSMSA.policeApp.Dhis2.DHIS2Config;
import com.RSMSA.policeApp.Dhis2.DHIS2Modal;
import com.RSMSA.policeApp.Dhis2.Data.DataElements;
import com.RSMSA.policeApp.Dhis2.Data.Program;
import com.RSMSA.policeApp.iRoadDB.IroadDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Coze on 11/11/2014.
 */
public class LoginActivity extends ActionBarActivity {
    private static final String TAG="LoginActivity";
    public static final int HTTP_REQUEST_TIMEOUT_MS = 30 * 1000;
    private IroadDatabase db;
    private boolean loginStatus=false;
    private SharedPreferences sharedpreferences;
    private String username, password, orgUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        db = new IroadDatabase(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }


        sharedpreferences=getSharedPreferences(MainOffence.MyPREF, Context.MODE_PRIVATE);
        ImageView background=(ImageView)findViewById(R.id.background);

        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.3f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.3f);
        ObjectAnimator scaleAnimation = ObjectAnimator.ofPropertyValuesHolder(background, pvhX, pvhY);
        scaleAnimation.setDuration(20000);
        scaleAnimation.setRepeatMode(ValueAnimator.REVERSE);
        scaleAnimation.setRepeatCount(ValueAnimator.INFINITE);
        scaleAnimation.start();

        final EditText usernameEditText = (EditText)findViewById(R.id.username);
        final EditText passwordEditText = (EditText)findViewById(R.id.password);
        final TextView loginText = (TextView)findViewById(R.id.login_text);
        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.pbar_main);
        CardView button = (CardView)findViewById(R.id.login_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password = passwordEditText.getText().toString();
                username = usernameEditText.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                loginText.setVisibility(View.GONE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONParser jsonParser = new JSONParser();
                        JSONObject object = jsonParser.dhis2HttpRequest(DHIS2Config.BASE_URL + "api/me.json", "GET",username,password,null);

                        String id = "";
                        try {
                            id=object.getString("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e){
                            e.printStackTrace();
                        }

                        if (!id.equals("")) {
                            try {
                                JSONObject orgJson = object.getJSONArray("organisationUnits").getJSONObject(0);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString("username", usernameEditText.getText().toString());
                                editor.putString("password", passwordEditText.getText().toString());
                                editor.putString("orgUnit", orgJson.getString("id"));
                                editor.commit();

                                orgUnit = orgJson.getString("id");

                                Log.d(TAG,"org unit id = "+orgUnit);

                                String url = DHIS2Config.BASE_URL+"api/programs?paging=false";
                                JSONParser dhis2parser = new JSONParser();
                                JSONObject objectProgram = dhis2parser.dhis2HttpRequest(url, "GET",username,password,null);
                                Log.d(TAG,"DHIS2 Programs = "+objectProgram.toString());
                                JSONArray jsonPrograms = null;
                                try {
                                    jsonPrograms = objectProgram.getJSONArray("programs");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                int counter = jsonPrograms.length();
                                for (int i=0;i<counter;i++) {
                                    JSONObject jsonProgram = null;
                                    ContentValues values = new ContentValues();
                                    try {
                                        jsonProgram = jsonPrograms.getJSONObject(i);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Program program = new Program();
                                    program.setModel(jsonProgram, program);
                                    MainOffence.programs.add(i, program);

                                    values.put(IroadDatabase.KEY_ID, program.getId());
                                    values.put(IroadDatabase.KEY_NAME, program.getName());
                                    values.put(IroadDatabase.KEY_CREATED, program.getCreated());
                                    values.put(IroadDatabase.KEY_LAST_UPDATED, program.getLastUpdated());
                                    values.put(IroadDatabase.KEY_HREF, program.getHref());
                                    try {
                                        db.insert(IroadDatabase.TABLE_PROGRAMS, null, values);
                                    }catch (Exception e){
                                        Log.d(TAG, "error catched = " + e.getMessage());
                                    }

                                }





                                String urlDataElements = DHIS2Config.BASE_URL+"api/dataElements.json?paging=false&fields=id,name,type,optionSet[id,name,options[id,name],version]";
                                JSONObject objectDataElements = dhis2parser.dhis2HttpRequest(urlDataElements, "GET",username,password,null);
                                Log.d(TAG, "DHIS2 Data Elements = " + objectDataElements.toString());
                                JSONArray jsonDatalements = null;
                                try {
                                    jsonDatalements = objectDataElements.getJSONArray("dataElements");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                int cou = jsonDatalements.length();
                                for (int i=0;i<cou;i++) {
                                    JSONObject jsonDataElement = null;
                                    ContentValues values = new ContentValues();
                                    try {
                                        jsonDataElement = jsonDatalements.getJSONObject(i);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    DataElements dataElement = new DataElements();
                                    dataElement.setModel(jsonDataElement, dataElement);
                                    MainOffence.dataElements.add(i, dataElement);

                                    values.put(IroadDatabase.KEY_ID, dataElement.getId());
                                    values.put(IroadDatabase.KEY_NAME, dataElement.getName());
                                    values.put(IroadDatabase.KEY_CREATED, dataElement.getCreated());
                                    values.put(IroadDatabase.KEY_LAST_UPDATED, dataElement.getLastUpdated());
                                    values.put(IroadDatabase.KEY_HREF, dataElement.getHref());
                                    try {
                                        db.insert(IroadDatabase.TABLE_DATA_ELEMENTS, null, values);
                                    }catch (Exception e){
                                        Log.d(TAG, "error catched = " + e.getMessage());
                                    }


                                    try {
                                        JSONObject optionSetObject = jsonDataElement.getJSONObject("optionSet");
                                        JSONArray optionsArray = optionSetObject.getJSONArray("options");
                                        int size = optionsArray.length();
                                        for(int j=0 ;j<size;j++){
                                            JSONObject option = optionsArray.getJSONObject(j);

                                            Log.d(TAG,"option set = "+optionSetObject.getString("name")+"|"+" Option naame =  "+option.getString("name"));
                                            ContentValues contentValues = new ContentValues();
                                            contentValues.put(IroadDatabase.KEY_ID, option.getString("id"));
                                            contentValues.put(IroadDatabase.KEY_NAME, optionSetObject.getString("name"));
                                            contentValues.put(IroadDatabase.KEY_OPTION_NAME, option.getString("name"));
                                            db.insert(IroadDatabase.TABLE_OPTION_SETS, null, contentValues);
                                        }
                                    }catch (JSONException e){

                                    } catch (Exception e){

                                    }

                                }


                                DHIS2Modal dhis2Modal = new DHIS2Modal("Offence Registry",null, username,password);
                                JSONArray jsonArray = dhis2Modal.getAllEvents();
                                int count=jsonArray.length();
                                for(int i=0;i<count;i++) {
                                    JSONObject offenceRegistryObject = null;
                                    ContentValues values = new ContentValues();
                                    try {
                                        offenceRegistryObject = jsonArray.getJSONObject(i);
                                        String uid = offenceRegistryObject.getString("id");
                                        String description = offenceRegistryObject.getString("Description");
                                        String nature = offenceRegistryObject.getString("Nature");
                                        String section = offenceRegistryObject.getString("Section");
                                        String amount = offenceRegistryObject.getString("Amount");
                                        values.put(IroadDatabase.KEY_ID, uid);
                                        values.put(IroadDatabase.KEY_DESCRIPTION, description);
                                        values.put(IroadDatabase.KEY_NATURE, nature);
                                        values.put(IroadDatabase.KEY_SECTION, section);
                                        values.put(IroadDatabase.KEY_AMOUNT, amount);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        db.insert(IroadDatabase.TABLE_OFFENCE_REGISTRY, null, values);
                                    } catch (Exception e) {
                                        Log.d(TAG, "error catched = " + e.getMessage());
                                    }
                                }

                                loginStatus = true;
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    loginText.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(LoginActivity.this, "Login Failed, Try Again", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
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

    @Override
    public void finish(){
        if(loginStatus){
            Intent returnIntent = new Intent();
            returnIntent.putExtra("username",username);
            returnIntent.putExtra("password",password);
            returnIntent.putExtra("orgUnit",orgUnit);
            setResult(RESULT_OK,returnIntent);
        }else{
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED,returnIntent);
        }
        super.finish();
        overridePendingTransition(R.anim.activity_slide_in_right, R.anim.activity_slide_out_right);
    }

}
