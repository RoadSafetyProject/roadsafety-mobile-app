package com.RSMSA.policeApp.Utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.RSMSA.policeApp.Models.Model;

import org.json.JSONException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Ilakoze on 3/5/2015.
 */
public class CustomeTimeWatcher implements View.OnFocusChangeListener {
    private static final String TAG = CustomeTimeWatcher.class.getSimpleName();
    Model model;
    String methodString;
    public CustomeTimeWatcher(Model model, String method){
        this.model = model;
        methodString = method;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        EditText editText = (EditText)v;
        if(!hasFocus){
            try {
                for(Method method: model.getClass().getMethods())
                {
                    if(method.getName().equals(methodString))
                    {
                        if(method.getParameterTypes()[0]==int.class)
                            method.invoke(model, Integer.parseInt(editText.getText().toString()));
                        else
                            method.invoke(model,editText.getText().toString());
                    }
                }
                afterFocus(editText.getText().toString(),editText);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NumberFormatException e){

            }

            Log.d(TAG,methodString+"("+editText.getText().toString()+")");
        }
    }

    public void afterFocus(String text, EditText editText) {

    }
}
