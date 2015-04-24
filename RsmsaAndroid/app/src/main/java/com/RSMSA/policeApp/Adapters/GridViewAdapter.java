package com.RSMSA.policeApp.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.RSMSA.policeApp.MainOffence;
import com.RSMSA.policeApp.R;

/**
 *  Created by Isaiah on 8/25/2014.
 */
public class GridViewAdapter extends BaseAdapter {

    public Context context;
    public String mOffense_type;
    public String mStatus;
    Cursor offenses;
    public String TAG = "Database";
    public String[] mDate;
    public String[] mType;

    public GridViewAdapter(Context c, String[] date, String[] type)
    {
        context = c;
        mDate = date;
        mType = type;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View history_view;
        if(view == null)
        {
            history_view = new View(context);
            history_view = inflater.inflate(R.layout.history_text, null);

        }else{
            history_view = (View) view;
        }

        TextView date = (TextView)history_view.findViewById(R.id.date_issued);
        date.setTypeface(MainOffence.Roboto_Condensed);
        date.setText(mDate[(i)]);

        TextView offence_type = (TextView)history_view.findViewById(R.id.offense_type);
        offence_type.setTypeface(MainOffence.Roboto_Condensed);
        offence_type.setText(mType[i]);

        return history_view;
    }

    @Override
    public int getCount() {
        return mDate.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


}
