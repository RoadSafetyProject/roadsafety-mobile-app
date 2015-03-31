package com.RSMSA.policeApp.Utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.RSMSA.policeApp.Models.Model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Ilakoze on 3/5/2015.
 */
public class CustomeTimeWatcher implements TextWatcher {
    private static final String TAG = CustomeTimeWatcher.class.getSimpleName();
    Model model;
    String methodString;
    public CustomeTimeWatcher(Model model, String method){
        this.model = model;
        methodString = method;
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        try {
            for(Method method: model.getClass().getMethods())
            {
                if(method.getName().equals(methodString))
                {
                    if(method.getParameterTypes()[0]==int.class)
                        method.invoke(model, Integer.parseInt(s.toString()));
                    else
                        method.invoke(model,s.toString());
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        Log.d(TAG,methodString+"("+s.toString()+")");

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
