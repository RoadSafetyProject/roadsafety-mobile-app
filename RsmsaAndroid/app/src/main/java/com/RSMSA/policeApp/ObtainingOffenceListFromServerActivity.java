package com.RSMSA.policeApp;


import android.app.Activity;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ObtainingOffenceListFromServerActivity{
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "psms";
    private static final String TAG="ObtainingOffenceListFromServerActivity";


    // All static variables
    static final String URL = "http://"+PoliceFunction.ipAddress+"/api/offence/registry";
    // XML node keys
    static final String KEY_ITEM = "natureOfOffence"; // parent node
    static final String KEY_ID = "id";
    static final String KEY_NAME = "nature";
    static final String KEY_AMOUNT = "amount";
    static final String KEY_RELATING = "relating";
    private JSONParser jsonParser;

    public ObtainingOffenceListFromServerActivity() {
    }

    public void createOffence(DatabaseHandlerOffence db){
        jsonParser = new JSONParser();
        JSONArray array = jsonParser.makeHttpRequest(URL, "GET");
        try {

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = null;
                try {
                    object = array.getJSONObject(i);
                    String id = null;
                    try {
                        id = object.getString(KEY_ID);
                        String name = object.getString(KEY_NAME);
                        String amount = object.getString(KEY_AMOUNT);
                        String relating = object.getString(KEY_RELATING);
                        Log.d(TAG, "id: " + id + "name:" + name + " amount:" + amount + "  relating:" + relating + "");
                        db.addNature(id, name, amount, relating);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }catch (Exception e){}

    }


}
