package com.RSMSA.policeApp;

import android.content.Context;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class      PoliceFunction {

    public static final String TAG = "PoliceFunction";
    private JSONParser jsonParser;

    //104.131.161.8:8000
    public static String ipAddress = "41.86.177.84:8000";//"41.86.176.170/rsmsa/rsmsa/public";*/
    private static String loginURL = "http://"+ipAddress+"/PSMS/public/android/index.php";
    public static String sendMediaApi = "http://"+ipAddress+"/AccidentUploads/media.php";
    private static String registerOffenceURL = "http://"+ipAddress+"/api/offence/";
    private static String registerAccidentURL = "http://"+ipAddress+"/api/accident/submit/";
    private static String forpassURL = "http://"+ipAddress+"/PSMS/public/android/index.php";
    private static String verificationURL = "http://"+ipAddress+"/api/request/verification_tag";
    private static String chgpassURL = "http://"+ipAddress+"/PSMS/public/android/index.php";
    private static String HistoryURL = "http://"+ipAddress+"/PSMS/public/android/index.php";
    private static String PaymentValidationURL = "http://"+ipAddress+"/api/driver/";
    private static String login_tag = "login";
    private static String history_tag = "history";
    private static String register_tag = "register";
    private static String forpass_tag = "forpass";
    private static String chgpass_tag = "chgpass";
    // constructor
    public PoliceFunction(){
        jsonParser = new JSONParser();
    }
    /**
     * Function to Login
     **/
    public JSONObject loginPolice(String RankNO, String password){
        // Building Parameters
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("tag", login_tag));
        params.add(new BasicNameValuePair("username", RankNO));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
        return json;
    }
    /**
     * Function to change password
     **/
    public JSONObject chgPass(String newpas, String email){
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("tag", chgpass_tag));
        params.add(new BasicNameValuePair("newpas", newpas));
        params.add(new BasicNameValuePair("oldpas", email));
        JSONObject json = jsonParser.getJSONFromUrl(chgpassURL, params);
        return json;
    }
    /**
     * Function to reset the password
     **/
    public JSONObject forPass(String forgotpassword){
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("tag", forpass_tag));
        params.add(new BasicNameValuePair("forgotpassword", forgotpassword));
        JSONObject json = jsonParser.getJSONFromUrl(forpassURL, params);
        return json;
    }
    /**
     * Function to  Register
     **/
    public JSONObject registerOffence(JSONObject offence,JSONObject receipt,JSONArray event){



        JSONObject postOffence = new JSONObject();
        try {
            postOffence.put("offence",offence);
            postOffence.put("receipt",receipt);
            postOffence.put("events",event);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d(TAG,"send report json = "+postOffence.toString());
        JSONObject json = jsonParser.postData(registerOffenceURL,postOffence);


        return json;
    }



    public JSONObject registerAccident(JSONObject accident){


        Log.d(TAG,"send report json = "+accident.toString());
        JSONObject json = jsonParser.postData(registerAccidentURL,accident);


        return json;
    }


    /**
     * Function to  get history
     **/
    public JSONObject getHistory(String license){
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("tag", history_tag));
        params.add(new BasicNameValuePair("license", license));
        JSONObject json = jsonParser.getJSONFromUrl(HistoryURL,params);
        return json;
    }


    /**
     * Function to obtain offence history
     * @param license
     * @return JsonObject
     */
    public JSONObject carAndLicenceVerification(String license, String plateNo){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("license_number", license));
        params.add(new BasicNameValuePair("plate_number", plateNo));


        Log.d(TAG,"URL = "+verificationURL);
        JSONObject json = jsonParser.makeHttpRequest(verificationURL,"GET",params);

        String status="";
        try {
            status=json.getString("status");
            Log.d(TAG,"Passed Json = " + json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e){

        }

        return json;
    }
    /**
     * Function to logout user
     * Resets the temporary data stored in SQLite Database
     * */
    public boolean logoutUser(Context context){
        DatabaseHandlerOffence db = new DatabaseHandlerOffence(context);
        db.resetTables();
        return true;
    }

    public JSONObject paymentVerification(String url){
        String mURL = "http://"+ipAddress+url;
        JSONObject json = jsonParser.makeHttpRequestReturnJsonObject(mURL, "GET");
        try {
            Log.d(TAG,"Passed Json = " + json.toString());
        } catch (NullPointerException e){

        }
        return json;
    }
}