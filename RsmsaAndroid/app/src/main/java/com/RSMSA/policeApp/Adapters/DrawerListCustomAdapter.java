package com.RSMSA.policeApp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.RSMSA.policeApp.MainOffence;
import com.RSMSA.policeApp.R;


public class DrawerListCustomAdapter extends BaseAdapter
{
    public String title[];
	public Activity context;
	public LayoutInflater inflater;
    public static final String TAG="DrawerListCustomAdapter";


	public DrawerListCustomAdapter(Activity context, String[] title) {
		super();

		this.context = context;
		this.title = title;
	    this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return title.length;
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

	public static class ViewHolder
	{
		ImageView imgViewLogo;
		TextView txtViewTitle;
		RelativeLayout divider;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.draweritem_row, null);
            holder.txtViewTitle = (TextView) convertView.findViewById(R.id.drawer_row);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder)convertView.getTag();
        }

        boolean selected=(MainOffence.selection==position)?true:false;
        Log.d(TAG,"position = "+position+" is selection = "+selected);
        holder.txtViewTitle.setText(title[position]);
        formatNavDrawerItem(selected, holder);
		return convertView;
	}
    private void formatNavDrawerItem(boolean selected,ViewHolder holder) {
        holder.txtViewTitle.setTextColor(selected ?
                context.getResources().getColor(R.color.yellow_300) :
                context.getResources().getColor(R.color.card_white));
    }

}