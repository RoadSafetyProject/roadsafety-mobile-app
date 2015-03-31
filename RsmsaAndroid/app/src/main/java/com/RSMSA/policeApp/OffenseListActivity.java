package com.RSMSA.policeApp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 *Created by Isaiah on 8/25/2014.
 */
public class OffenseListActivity extends Activity implements AdapterView.OnItemClickListener {

    public ListView mlistView;

    public static int[] offenseTag = new int[50];

    public  static ArrayList<String> offenseDesc = new ArrayList<String>();

    public static ArrayList<String>  OffenseListType = new ArrayList<String>();

    public static ArrayList<Integer>  offenceIds = new ArrayList<Integer>();

    //public static String[] offenseDesc = new String[50];

    public static ArrayList<String> nullArray = new ArrayList<String>();

    public static int[] nullArrayInt = new int[50];

    public static int offenseCount = 0;

    public String[] list_of_offences;

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
         * checkboxes
         */
       // offenseCount = mlistView.getCheckedItemCount();


        /**
         * getting the offence details form the database
         */
        DatabaseHandlerOffence db = new DatabaseHandlerOffence(getApplicationContext());
        int cont=db.getOffenceCount();
        Log.d("pasat", cont+"");
        List<String> list= new ArrayList<String>();
        List<String> relatingList = new ArrayList<String>();
        List<Integer> ids = new ArrayList<Integer>();




        list = db.getOffenceNature(false);
        relatingList = db.getOffenceNature(true);
        ids=db.getOffenceIds();


        adapter = new adapters.adapter(this, list, relatingList,ids);
        mlistView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        TextView label = (TextView) view.findViewById(R.id.offense_description);
        CheckBox checkBox = (CheckBox)view.getTag(R.id.offense_checked);
        Toast.makeText(view.getContext(), label.getText().toString()+" "+isCheckedOrNot(checkBox), Toast.LENGTH_LONG).show();

    }

    private String isCheckedOrNot(CheckBox checkbox) {
        if(checkbox.isChecked())
            return "is checked";
        else
            return "is not checked";
    }

    /**
     * Overiding h to return back results to the previous activity
     */
    @Override
    public void finish(){
        Intent returnIntent=new Intent();
//        Log.d("Database", "total count "+offenseCount);
//        Log.d("Database", "Description "+offenseDesc.toString()

        returnIntent.putExtra("tag", offenseTag);
        returnIntent.putExtra("count", offenseCount);
        returnIntent.putIntegerArrayListExtra("ids", offenceIds);
        returnIntent.putStringArrayListExtra("desc", offenseDesc);
        returnIntent.putStringArrayListExtra("type", OffenseListType);
        Log.d("Database", "offenses at child is "+offenseDesc);
        setResult(RESULT_OK,returnIntent);
        super.finish();
        offenseCount = 0;
        OffenseListType.clear();
        offenseDesc.clear();
        offenseTag = nullArrayInt;
    }
}