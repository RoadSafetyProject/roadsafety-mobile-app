package adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.RSMSA.policeApp.R;

/**
 *  Created by Isaiah on 10/27/14.
 */
public class ActGridAdapter extends BaseAdapter {
    private Context context;
    final String[] mdesc;
    final String[] mImages;
    int one = 2;
    TypedArray imgsrc;
    TextView number;
    TextView description;
    ImageView header_image;

    public ActGridAdapter(Context context, String[] mResourcesDescription, String[] mResourcesImages, TypedArray images) {
        this.context = context;
        mdesc = mResourcesDescription;
        mImages = mResourcesImages;
        imgsrc = images;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {
            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.grid_item_atc, null);

        }
        else {
            gridView = (View) convertView;
        }

        number = (TextView)gridView.findViewById(R.id.number);
        description = (TextView)gridView.findViewById(R.id.cat_description);
        header_image = (ImageView)gridView.findViewById(R.id.cat_image);

        number.setText(mImages[position]);
        description.setText(mdesc[position]);
        header_image.setImageResource(imgsrc.getResourceId(position, -1));

        return gridView;
    }

    @Override
    public int getCount() {
        return mdesc.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}
