package com.RSMSA.policeApp.Dhis2.Data;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Ilakoze on 3/30/2015.
 */
public class Modal implements Serializable {
    private static final String TAG = Modal.class.getSimpleName();
    public Modal setModel(JSONObject object,Modal modal){
        for(Method method: modal.getClass().getMethods()){
            if(method.getName().startsWith("set") && !method.getName().equals("setModel") ){
                String field = (method.getName().replaceFirst("set",""));
                if (field.length() <= 1) {
                    field = field.toLowerCase();
                } else {
                    field = field.substring(0, 1).toLowerCase() + field.substring(1);
                }

                try {
                    method.invoke(modal,object.getString(field));
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

            }
        }
        return modal;
    }


    public Modal setModel(Cursor cursor,Modal model){
        int counter = cursor.getColumnCount();
        for(int i=0;i<counter;i++){
            String columnName=cursor.getColumnName(i);
            columnName=columnName.toLowerCase();
            StringBuilder columnNameSb = new StringBuilder();
            columnNameSb.append(columnName);
            columnNameSb.setCharAt(0, Character.toUpperCase(columnNameSb.charAt(0)));
            columnName = columnNameSb.toString();

            for(Method method:model.getClass().getMethods())
            {
                if(method.getName().equals("set"+columnName))
                {
                    final Class<?> c = method.getParameterTypes()[0];
                    final String name = (c.isArray()? c.getComponentType() : c).getName();
                    if(name.equals("java.lang.String")) {
                        try {
                            method.invoke(model, cursor.getString(i));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }else if(name.equals("long")){
                        try {
                            method.invoke(model, cursor.getLong(i));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }else if (name.equals("int")){
                        try {
                            method.invoke(model, cursor.getInt(i));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }

        }
        return model;
    }
}
