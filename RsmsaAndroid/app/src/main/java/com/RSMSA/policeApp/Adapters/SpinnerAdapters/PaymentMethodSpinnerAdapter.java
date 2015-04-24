package com.RSMSA.policeApp.Adapters.SpinnerAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.RSMSA.policeApp.R;


/**
 * Created by graysonjulius on 8/23/14.
 */
public class PaymentMethodSpinnerAdapter extends BaseAdapter {
    Context context;
    int layoutResourceId;
    String [] data;
    TextView username;
    ImageView image;


    public PaymentMethodSpinnerAdapter(Context a, int textViewResourceId, String [] data) {
        // super(a, textViewResourceId, data);
        this.data = data;
        this.context = a;
        this.layoutResourceId = textViewResourceId;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null) {
            view = inflater.inflate( R.layout.spinner_item,parent, false);
        }
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(data[position]);
        return view;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (v == null) {
            v = inflater.inflate(R.layout.row_menu, null);
            username = (TextView) v.findViewById(R.id.menu_title);
        }
        final String item = data[position];
        if (item != null) {
            username.setText(item);
        }

        final TextView finalItem = username;
        username.post(new Runnable() {
            @Override
            public void run() {
                finalItem.setSingleLine(false);
            }
        });
        return v;
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }
}



