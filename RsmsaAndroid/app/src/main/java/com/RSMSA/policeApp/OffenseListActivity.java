package com.RSMSA.policeApp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.RSMSA.policeApp.iRoadDB.IroadDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 *Created by Isaiah on 8/25/2014.
 * Modified by Coze on 4/29/2015
 */
public class OffenseListActivity extends Activity{
    public ListView mlistView;
    public  static ArrayList<String> offenseDesc = new ArrayList<String>();
    public static ArrayList<String>  OffenseListType = new ArrayList<String>();
    public static ArrayList<String>  offenceIds = new ArrayList<String>();
    public static int offenseCount = 0;
    public Button doneBtn;
    public ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        setContentView(R.layout.offense_list_activity);

        /**
         * get the instance of the gridview
         */
        mlistView = (ListView) findViewById(R.id.offense_list);


        /**
         * get the instance of a done button and assign action
         * when clicked
         */
        doneBtn = (Button) findViewById(R.id.done);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /**
         * getting the offence details form the database
         */
        IroadDatabase db = new IroadDatabase(getApplicationContext());
        List<String> list= new ArrayList<String>();
        List<String> offenceAmountList = new ArrayList<String>();
        List<String> ids = new ArrayList<String>();


        list = db.getAllOffenceDetails(false);
        offenceAmountList = db.getAllOffenceDetails(true);
        ids=db.getOffenceUIds();


        adapter = new com.RSMSA.policeApp.Adapters.adapter(this, list, offenceAmountList,ids);
        mlistView.setAdapter(adapter);
    }
    private String isCheckedOrNot(CheckBox checkbox) {
        if(checkbox.isChecked())
            return "is checked";
        else
            return "is not checked";
    }

    @Override
    public void finish(){
        Intent returnIntent=new Intent();
        returnIntent.putExtra("count", offenseCount);
        returnIntent.putStringArrayListExtra("uids", offenceIds);
        returnIntent.putStringArrayListExtra("desc", offenseDesc);
        returnIntent.putStringArrayListExtra("type", OffenseListType);
        setResult(RESULT_OK, returnIntent);
        super.finish();
        offenseCount = 0;
        OffenseListType.clear();
        offenseDesc.clear();
    }
}