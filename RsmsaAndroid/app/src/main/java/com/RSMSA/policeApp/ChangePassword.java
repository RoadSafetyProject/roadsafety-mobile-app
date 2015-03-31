package com.RSMSA.policeApp;

/**
 * Created by User on 9/3/2014.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ChangePassword extends Activity {
    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";
    EditText newpass;
    EditText newpass1;
    EditText Oldpass;
    TextView alert;
    Button changepass;
    Button cancel;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepassword);
        cancel = (Button) findViewById(R.id.btcancel);
        cancel.setOnClickListener(new View.OnClickListener(){
            public void onClick(View arg0){
                Intent login = new Intent(getApplicationContext(), MainOffence.class);
                startActivity(login);
                finish();
            }
        });
        newpass1 = (EditText) findViewById(R.id.newpass1);
        newpass = (EditText) findViewById(R.id.newpass);
        Oldpass = (EditText) findViewById(R.id.Oldpass);
        alert = (TextView) findViewById(R.id.alertpass);
        changepass = (Button) findViewById(R.id.btchangepass);

        changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newpas,newpas1, oldpass;
                oldpass = Oldpass.getText().toString();
                newpas = newpass.getText().toString();
                newpas1 = newpass1.getText().toString();
              if((!oldpass.equals("")) && (!newpas.equals("")) && (!newpas1.equals("")))  {
                if((newpas.equals(newpas1))&& (!oldpass.equals("")) ){
                    alert.setText("");
                    NetAsync(view);

               } else {
                    Toast toast = Toast.makeText(ChangePassword.this,
                            "MissMatch Of new Password!", Toast.LENGTH_SHORT);
                    toast.show();

               }
            }else{

                  Toast toast = Toast.makeText(ChangePassword.this,
                          "Empty Field(s)!", Toast.LENGTH_SHORT);
                  toast.show();
              }
            }
        });}
    private class NetCheck extends AsyncTask<String, Void, Boolean>
    {
        private ProgressDialog nDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(ChangePassword.this);
            nDialog.setMessage("Loading..");
            nDialog.setTitle("Checking Network");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }
        @Override
        protected Boolean doInBackground(String... args){
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL("http://www.google.com");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    }
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return false;
        }
        @Override
        protected void onPostExecute(Boolean th){
            if(th == true){
                nDialog.dismiss();
                new ProcessChange().execute();
            }
            else{
                nDialog.dismiss();
                alert.setText("Error in Network Connection");
            }
        }
    }
    private class ProcessChange extends AsyncTask <String, Void, JSONObject>{
        private ProgressDialog pDialog;
        String newpas,newpas1, oldPas;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            newpas = newpass.getText().toString();
            newpas1 = newpass1.getText().toString();
            oldPas = Oldpass.getText().toString();



            pDialog = new ProgressDialog(ChangePassword.this);
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected JSONObject doInBackground(String... args) {
            PoliceFunction password = new PoliceFunction();
            JSONObject json = password.chgPass(newpas,oldPas);
            Log.d("Button", "Register");
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if (json.getString(KEY_SUCCESS) != null) {
                    alert.setText("");
                    String res = json.getString(KEY_SUCCESS);
                    String red = json.getString(KEY_ERROR);
                    if (Integer.parseInt(res) == 1) {
                        /**
                         * Dismiss the process dialog
                         **/
                        pDialog.dismiss();
                        alert.setText("Your Password is successfully changed.");
                    } else if (Integer.parseInt(red) == 1) {
                        pDialog.dismiss();
                        alert.setText("Invalid old Password.");
                    } else {
                        pDialog.dismiss();
                        alert.setText("Error occurred in changing Password.");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }}
    public void NetAsync(View view){
        new NetCheck().execute();
    }}