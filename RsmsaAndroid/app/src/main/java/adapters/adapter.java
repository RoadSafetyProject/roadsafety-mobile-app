package adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.RSMSA.policeApp.MainOffence;
import com.RSMSA.policeApp.OffenseListActivity;
import com.RSMSA.policeApp.R;

import java.util.List;

/**
 *  Created by Isaiah on 9/2/2014.
 */
public class adapter extends ArrayAdapter<String> {

    /**
     * define class variable
     */
    private final Activity mContext;
    private final List<String> mList;
    private final List<String> mrelList;
    private final List<Integer> ids;
    public boolean[] checked;
    public int mSize = 0;
    public int checkedcount;

    public adapter(Activity context, List<String> list, List<String> relList, List<Integer> id) {
        super(context, R.layout.offense, list);
        mContext = context;
        mList = list;
        mrelList = relList;
        ids=id;
        mSize = list.size();
        checkedcount = 0;
        checked = new boolean[list.size()];
        for(int i=0; i<list.size(); i++){
            checked[i] = false;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        View cv;
        if(convertView == null){

                LayoutInflater inflator = mContext.getLayoutInflater();
                cv = new View(mContext);
                cv = inflator.inflate(R.layout.offense, null);
        }
        else {
            cv = (View) convertView;
        }

        CheckBox checkBox = (CheckBox)cv.findViewById(R.id.offense_checked);
        TextView description = (TextView)cv.findViewById(R.id.offense_description);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                {
                    checked[position] = true;
                    OffenseListActivity.offenseDesc.add(mList.get(position));
                    OffenseListActivity.OffenseListType.add(mrelList.get(position));
                    OffenseListActivity.offenceIds.add(ids.get(position));

                    //checkedcount++;
                    OffenseListActivity.offenseCount++;
                }
                else
                {
                    checked[position] = false;
                    OffenseListActivity.offenseDesc.remove(mList.get(position));
                    OffenseListActivity.OffenseListType.remove(mrelList.get(position));
                    OffenseListActivity.offenceIds.remove(ids.get(position));
                    //checkedcount--;
                    OffenseListActivity.offenseCount--;
                }
            }
        });

        checkBox.setChecked(checked[position]);

       // OffenseListActivity.offenseCount = checkedcount;

        description.setTypeface(MainOffence.Rosario_Regular);
        description.setText(mList.get(position));

        return cv;
    }

}
