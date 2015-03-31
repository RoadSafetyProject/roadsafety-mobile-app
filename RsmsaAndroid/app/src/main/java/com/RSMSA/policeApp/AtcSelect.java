package com.RSMSA.policeApp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import adapters.ActGridAdapter;
import adapters.ActGridAdapter;

/**
 *  Created by isaiah on 10/23/2014.
 */
public class AtcSelect extends Activity {

    String SelectedItem;

    int gridselection = -1;

    TextView SelItem;

    String theItem;

    GridView gridView;

    public String[] cat1 = {"Leave road to left in left corner", "Leave road to left in right corner", "leave straight road to the side", "Leave road to the right in left corner",
                            "Leave road to the right in right corner", "Leave road within crossing or diverge", "Reverse or return","Falling or injury within vehicle","Collision fixed obstacles on road",
                            "Tree on Car","Car Burning"};
    public String[] cat1Numbers = {"112","113","121","122","123","131","141","151","161","172","181"};

    public String[] cat2 = {"Collision overtaking right","Lane change to the leftleft","Change to left, 2nd vehicle leaves road","Lane change to the right","Change to right, 2nd vehicle leaves road",
                            "Rear accident straight road","Rear side accident left corner","Rear side accident right co.","Rear accident stopped vehi.","Rear accident junct. 1st vehic. stops",
                            "Collision other vehicle by reversing","Collision while entering fro. left","Collision while entering fro. right"};
    public String[] cat2Numbers = {"212","221","222","223","224","231","232","233","241","261","271","281","282"};

    public String[] cat3 = {"Head collision change lanes right","Change to left road side","Leave road right due to opposing vehicle","Leave road left due opposing vehi. left","Leave road right due opposing vehi. right",
                            "Leave road right due to opposing vehi.","Leave road left due to opposing vehicle","Graze collision on straight road","Head on collision on straight road","Head on colli. in corner",
                            "Head on/graze colli. while overtaking","Head on/graze collision left co.","Head on/graze collision right co."};
    public String[] cat3Numbers = {"312","313","321","322","323","324","325","331","341","342","361","362","363"};

    public String[] cat4 = {"Collision of left turn with straight drive","Collisionn of 2 left turning vehi.","Rear accident while turning right","Colli. right turn straight drive","Collision of 2 right turning vehi.",
                            "Colli. U-turn same direction","Collision turning left opposing dir.","Collision of 2 opposing left turn","Collision turning to same direction","Turning left with other","Collision while U-turn within junction"};
    public String[] cat4Numbers = {"412","413","421","422","423","431","451","452","453","461","471"};

    public String[] cat5 = {"Colli. in same dir. right turn","Colli. opposing traffic left turn","Colli. opposing traffic right turn","Collision of two left turning vehi.","Collision of two right turning vehi.",
                            "Colli. of left and right turning vehi.","Collission on Ferry manoeuvring /parking"};
    public String[] cat5Numbers = {"521","522","531","532","541","542","551"};

    public String[] cat6 = {"Vehicle parked right side, rear accident","Vehicle parked while passing right","Vehicle parked while passing left"};
    public String[] cat6Numbers = {"621","641","642"};

    public String[] cat7 = {"Pedestrian crosses after minor road left","Pedestrian crosses before minor road","Pedestrian walks in travelling direction","Pedestrian walks in vehicle, turn right","Pedestrian walks opposite in vehicle",
                            "Pedestrian crosses road from left","Pedestrian crosses road from right","Pedestrian walks on road on the left","Pedestrian walks on road on the right","Collision pedestrian on road side",
                            "Collision pedestrian while reversing","Collision pedestrian entering an entrance","Collision pedestrian leaving entrance","Collision roundabout pedestrian","Collision roundabout incoming vehicle","Collision roundabout outgoing vehicle",
                            "Center collision roundabout","Animal on the road","Accident with train"};
    public String[] cat7numbers = {"712","713","721","723","724","741","742","761","762","772","773","774","775","776","777","778","779","781","791"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        setContentView(R.layout.atc_select);


        final Bundle bundle=getIntent().getExtras();
        SelectedItem = bundle.getString("classification");


        gridView = (GridView)findViewById(R.id.accident_type_grid);
        if (SelectedItem.equals("0")){
            TypedArray imgs = getResources().obtainTypedArray(R.array.cat_one_images);
            gridView.setAdapter(new ActGridAdapter(this, cat1, cat1Numbers, imgs));
        }
        else if (SelectedItem.equals("1")){
            TypedArray imgs = getResources().obtainTypedArray(R.array.cat_two_images);
            gridView.setAdapter(new ActGridAdapter(this, cat2, cat2Numbers, imgs));
        }
        else if (SelectedItem.equals("2")){
            TypedArray imgs = getResources().obtainTypedArray(R.array.cat_three_images);
            gridView.setAdapter(new ActGridAdapter(this, cat3, cat3Numbers, imgs));
        }
        else if (SelectedItem.equals("3")){
            TypedArray imgs = getResources().obtainTypedArray(R.array.cat_four_images);
            gridView.setAdapter(new ActGridAdapter(this, cat4, cat4Numbers, imgs));
        }
        else if (SelectedItem.equals("4")){
            TypedArray imgs = getResources().obtainTypedArray(R.array.cat_five_images);
            gridView.setAdapter(new ActGridAdapter(this, cat5, cat5Numbers, imgs));
        }
        else if (SelectedItem.equals("5")){
            TypedArray imgs = getResources().obtainTypedArray(R.array.cat_six_images);
            gridView.setAdapter(new ActGridAdapter(this, cat6, cat6Numbers, imgs));
        }
        else{
            TypedArray imgs = getResources().obtainTypedArray(R.array.cat_seven_images);
            gridView.setAdapter(new ActGridAdapter(this, cat7, cat7numbers, imgs));
        }

        SelItem = (TextView)findViewById(R.id.selected_item);
        SelItem.setText(SelectedItem);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                gridselection = i;
                finish();
            }
        });

    }

    /**
     * Overiding h to return back results to the previous activity
     */
    @Override
    public void finish(){
        Intent returnIntent=new Intent();
        returnIntent.putExtra("item", gridselection+"");
        setResult(RESULT_OK, returnIntent);
        Log.d("resulty", gridselection+"");
        super.finish();
    }

}
