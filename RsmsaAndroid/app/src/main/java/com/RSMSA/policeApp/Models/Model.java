package com.RSMSA.policeApp.Models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Ilakoze on 2/4/2015.
 */
public class Model {

    public JSONObject getjson(Model model){
        JSONObject jsonObject = new JSONObject();
        for(Method method:model.getClass().getMethods())
        {
            if(method.getName().startsWith("get") && !method.getName().equals("getClass")&&!method.getName().equals("getjson"))
            {
                String field = (method.getName().replaceFirst("get","")).toLowerCase();
                try {
                    Log.d("MEthod name",field);
                    jsonObject.put(field,method.invoke(model,null));
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

            }
        }
        return jsonObject;
    }

}
