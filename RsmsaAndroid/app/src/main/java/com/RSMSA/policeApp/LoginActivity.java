package com.RSMSA.policeApp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends Activity {

    /**
     * defining the layout variables
     */
    Button login;
    EditText username,passwd;
    TextView error;
    TextView passreset;
    TextView title;
    TextView rankNumberText;
    TextView passwordText;
    TextView FPText;

    /**
     * custom Typefaces
     */
    public static Typeface Athletic, Fun_Raiser, Roboto_Condensed, Roboto_Black, Roboto_Light, Roboto_BoldCondensedItalic, Roboto_BoldCondensed, Rosario_Regular, Rosario_Bold, Rosario_Italic, Roboto_Regular, Roboto_Medium;


    /**
     * Called when the activity is first created.
     */
    private static String KEY_SUCCESS = "success";

    private static String KEY_USERNAME = "rankNo";
    private static String KEY_FULLNAME = "fullName";
    private static String KEY_STATION = "station";
    private static String KEY_CREATED_AT = "created_at";

    /**
     * user session variables
     */
    public static final String name = "nameKey";
    public static final String pass = "passwordKey";
    public static final String list = "offense_list_present";

    public static boolean justBack = false;

    public static final String MyPREFERENCES  = "MyPrefs";
    public static final String MyPREF  = "Prefs";


    SharedPreferences sharedpreferences;

    public ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        /**
         * Type faces used for setting fonts thoughout the app
         */
        Roboto_Light = Typeface.createFromAsset(this.getAssets(), "Roboto-Light.ttf");
        Roboto_Black = Typeface.createFromAsset(this.getAssets(), "Roboto-Black.ttf");
        Roboto_Condensed = Typeface.createFromAsset(this.getAssets(), "Roboto-Condensed.ttf");
        Roboto_BoldCondensedItalic = Typeface.createFromAsset(getAssets(), "Roboto-BoldCondensedItalic.ttf");
        Roboto_BoldCondensed = Typeface.createFromAsset(getAssets(), "Roboto-BoldCondensed.ttf");
        Roboto_Regular = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        Roboto_Medium = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");
        Rosario_Regular = Typeface.createFromAsset(getAssets(), "Rosario-Regular.ttf");
        Rosario_Italic = Typeface.createFromAsset(getAssets(), "Rosario-Italic.ttf");
        Rosario_Bold = Typeface.createFromAsset(getAssets(), "Rosario-Bold.ttf");
        Fun_Raiser = Typeface.createFromAsset(getAssets(), "Fun-Raiser.ttf");
        Athletic = Typeface.createFromAsset(getAssets(), "athletic.ttf");


        progressBar = (ProgressBar)findViewById(R.id.pbar);

        title = (TextView)findViewById(R.id.view2);
        title.setTypeface(Rosario_Bold);

        rankNumberText = (TextView)findViewById(R.id.textName);
        rankNumberText.setTypeface(Rosario_Regular);

        passwordText = (TextView)findViewById(R.id.address);
        passwordText.setTypeface(Rosario_Regular);

        FPText = (TextView)findViewById(R.id.forgotPassword);
        FPText.setTypeface(Rosario_Regular);

        login = (Button)findViewById(R.id.login);
        login.setTypeface(Rosario_Regular);

        username = (EditText)findViewById(R.id.userName);

        passwd= (EditText)findViewById(R.id.passwd);

        error = (TextView)findViewById(R.id.loginError);
        error.setTypeface(Roboto_Condensed);

        passreset = (TextView)findViewById(R.id.forgotPassword);
        passreset.setTypeface(Rosario_Regular);

        // * Login button click event
        //* A Toast is set to alert when the Email and Password field is empty

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {


                if (  ( !username.getText().toString().equals("")) && ( !passwd.getText().toString().equals("")) )
                {
                    error.setText("");
                    NetAsync(view);
                }
                else if ( ( !username.getText().toString().equals("")) )
                {
                    Toast.makeText(getApplicationContext(),
                            "username field empty", Toast.LENGTH_SHORT).show();
                }
                else if ( ( !passwd.getText().toString().equals("")) )
                {
                    Toast.makeText(getApplicationContext(),
                            "password field empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "Username and Password field are empty", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // * forgot password listener of a click by user

        passreset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), PasswordReset.class);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(myIntent);
            }});
    }


    /**
     * Async Task to check whether internet connection is working.
     **/
    private class NetCheck extends AsyncTask<String, Void, Boolean>
    {
        //private ProgressDialog nDialog;
        @Override
        protected void onPreExecute(){
            progressBar.setVisibility(View.VISIBLE);
//            super.onPreExecute();
//            nDialog = new ProgressDialog(LoginActivity.this);
//            nDialog.setTitle("Checking Network");
//            nDialog.setMessage("Loading..");
//            nDialog.setIndeterminate(false);
//            nDialog.setCancelable(true);
//            nDialog.show();
        }
        @Override
        protected Boolean doInBackground(String... args){
/**
 * Gets current device state and checks for working internet connection by trying Google.
 **/
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
                progressBar.setVisibility(View.GONE);
                new ProcessLogin().execute();
            }
            else{
                progressBar.setVisibility(View.GONE);
                error.setText("Error in Network Connection");
            }
        }
    }
    /**
     * Async Task to get and send data to MySql database through JSON response.
     **/
    private class ProcessLogin extends AsyncTask<String, Void, JSONObject> {
        //private ProgressDialog pDialog;
        String User,Pword;
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
            username = (EditText) findViewById(R.id.userName);
            passwd = (EditText) findViewById(R.id.passwd);
            User = username.getText().toString();
            Pword = passwd.getText().toString();
//            pDialog = new ProgressDialog(LoginActivity.this);
//            pDialog.setTitle("Contacting Servers");
//            pDialog.setMessage("verifying user ...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(true);
//            pDialog.show();
        }
        @Override
        protected JSONObject doInBackground(String... args) {
            PoliceFunction pFunction = new PoliceFunction();
            JSONObject json = pFunction.loginPolice(User, Pword);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            try {



                if (json != null && json.getString(KEY_SUCCESS) != null){
                    String res = json.getString(KEY_SUCCESS);
                    if(Integer.parseInt(res) == 1){

                        /**
                         * adding user information to shared preferences
                         */
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        String u = username.getText().toString();
                        String p = passwd.getText().toString();
                        editor.putString(name, u);
                        editor.putString(pass, p);
                        editor.commit();

//                        pDialog.setMessage("Loading User Space");
//                        pDialog.setTitle("Getting Data");
                        DatabaseHandlerOffence db = new DatabaseHandlerOffence(getApplicationContext());
                        DatabaseHandlerOffence db1 = new DatabaseHandlerOffence(getApplicationContext());
                        JSONObject json_user = json.getJSONObject("user");
                        /**
                         * Clear all previous data in SQlite database.
                         **/
                        PoliceFunction logout = new PoliceFunction();
                        logout.logoutUser(getApplicationContext());
                        db.addUser(json_user.getString(KEY_USERNAME),json_user.getString(KEY_FULLNAME),
                                json_user.getString(KEY_STATION),json_user.getString(KEY_CREATED_AT));


                        /**
                         * create the offenceNature on the database this is done only once the application is first
                         * run on device
                         */
                        sharedpreferences=getSharedPreferences(MyPREF,
                                Context.MODE_PRIVATE);
                        if (!sharedpreferences.contains(list)) {

                            Log.d("pref", "app running for the first time");

                            ObtainingOffenceListFromServerActivity object = new ObtainingOffenceListFromServerActivity();

                            object.createOffence(db1);

                            sharedpreferences.edit().putString(list, "v").commit();
                        }


                        /**
                         *If JSON array details are stored in SQlite it launches the User Panel.
                         **/
                        Intent upanel = new Intent(getApplicationContext(), MainOffence.class);
                        upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        progressBar.setVisibility(View.GONE);
                        startActivity(upanel);
                        /**
                         * Close Login Screen
                         */
                         finish();

                    }else{
                        progressBar.setVisibility(View.GONE);
                        error.setText("Incorrect username/password");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        sharedpreferences=getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);
        if (sharedpreferences.contains(name))
        {
            if(sharedpreferences.contains(pass)){

                if(justBack)
                {
                    finish();
                    justBack = false;
                }
                else {
                    Intent i = new Intent(this, MainOffence.class);
                    startActivity(i);
                }

            }
        }
        super.onResume();
    }

    public void NetAsync(View view){
        new NetCheck().execute();
    }


}