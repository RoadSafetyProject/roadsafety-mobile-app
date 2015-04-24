package com.RSMSA.policeApp;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.RSMSA.policeApp.Dialogues.AtcSelectDialogue;
import com.RSMSA.policeApp.Models.Accident;
import com.RSMSA.policeApp.Utils.SystemBarTintManager;
import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.RSMSA.policeApp.Adapters.ViewPagerAccidentsDetailsAdapter;
import com.RSMSA.policeApp.Adapters.ViewPagerWitnessesAdapter;
import com.RSMSA.policeApp.CustomViews.SlidingTabLayout;

/**
 *  Created by Isaiah on 02/02/2015.
 */
public class AccidentReportFormActivity extends ActionBarActivity{

    public static final String TAG="AccidentReportFormActivity";

    private Toolbar toolbar;
    private Spinner atcSpinner;
    private Button accidentTypeSelectButton,timePicker;
    private int selectedSpinner;
    public static ViewPager mViewPager,mViewPagerWitness;
    public static SlidingTabLayout mTabs;
    private EditText numberOfVihaclesInvolved, numberOfWitnesses;
    public static ScrollView accidentRegistration;
    public static TextView drawerTitle;
    public static String imagePath=null, videoPath=null;
    public static boolean isImage=true;
    private DatePickerDialog newDatePickerDialogueFragment;
    private TimePickerDialog newTimerPickerDialogueFragment;
    public static JSONArray vehicles,witnesses;
    public static Accident accident;
    private static final int SELECT_ACCIDENT_TYPE_REQUEST_CODE = 400;
    public static FragmentManager fragmentManager;
    private String dateSelected = "";
    private String timeSelected = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_accident);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fragmentManager=getFragmentManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        ColorDrawable colorDrawable= new ColorDrawable(getResources().getColor(R.color.blue_900));
        tintManager.setTintDrawable(colorDrawable);

        Intent i = getIntent();
        imagePath = i.getStringExtra("imagePath");
        videoPath= i.getStringExtra("videoPath");


        atcSpinner = (Spinner) findViewById(R.id.atc_spinner);
        accidentTypeSelectButton = (Button)findViewById(R.id.accident_type_select_button);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPagerWitness = (ViewPager) findViewById(R.id.pagerWitness);
        mTabs = (SlidingTabLayout)findViewById(R.id.tabs);
        numberOfVihaclesInvolved=(EditText)findViewById(R.id.number_of_vehicles);
        numberOfWitnesses=(EditText)findViewById(R.id.number_of_witnesses);

        //numberOfVihaclesInvolved.setOnTouchListener(editTextOnTouchListener);
        //numberOfWitnesses.setOnTouchListener(editTextOnTouchListener);

        Button buttonNext=(Button)findViewById(R.id.next);
        accidentRegistration=(ScrollView)findViewById(R.id.accident_registration);
        drawerTitle=(TextView)findViewById(R.id.drawer_title);

        //TODO should come from server
        List<String> list = new ArrayList<String>();
        list.add("Single vehicle accident");
        list.add("Accidents between vehicles driving same travel direction (2 or more vehicles)");
        list.add("Accidents between vehicles driving opposite travel direction (2 or more vehicles)");
        list.add("Accidents at a junction turning in same or opposite direction (2 or more vehi.)");
        list.add("Collision at a junction between two or more participants");
        list.add("Accident w. parked vehicles");
        list.add("Pedestrian, animals and other accidents");

        ArrayAdapter<String> atc_adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,list);
        atc_adapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        atcSpinner.setAdapter(atc_adapter);

        atcSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSpinner = i;
                if (i == 0) {
                    selectedSpinner = 234;
                    //TODO
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        accidentTypeSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent atcselectintent = new Intent(AccidentReportFormActivity.this, AtcSelectDialogue.class);
                atcselectintent.putExtra("classification", selectedSpinner + "");
                Log.d("selected", atcSpinner.getSelectedItem() + "");
                startActivityForResult(atcselectintent, SELECT_ACCIDENT_TYPE_REQUEST_CODE);
            }
        });

        vehicles  = new JSONArray();
        witnesses = new JSONArray();

        final EditText accident_reg=(EditText)findViewById(R.id.reg_ref);
        final EditText fatal=(EditText)findViewById(R.id.fatal_edit);
        final EditText severInjuries=(EditText)findViewById(R.id.injury_edit);
        final EditText simpleEdit=(EditText)findViewById(R.id.simple_edit);
        final EditText onlyDamage=(EditText)findViewById(R.id.not_injured_edit);
        final EditText date=(EditText)findViewById(R.id.date);
        final EditText time=(EditText)findViewById(R.id.time);
        final EditText areaName=(EditText)findViewById(R.id.name);
        final EditText district=(EditText)findViewById(R.id.district);
        final EditText city=(EditText)findViewById(R.id.city);
        final EditText roadName=(EditText)findViewById(R.id.road_name);
        final EditText intersectionName=(EditText)findViewById(R.id.intersection_name);
        final EditText roadNumber=(EditText)findViewById(R.id.road_number);
        final EditText intersectionNumber=(EditText)findViewById(R.id.intersection_number);
        final EditText roadMark=(EditText)findViewById(R.id.road_mark);
        final EditText intersectionMark=(EditText)findViewById(R.id.intersection_mark);
        timePicker= (Button)findViewById(R.id.time_picker);
        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog();
                timePickerDialog.show(getFragmentManager(),"TimePickerDialogue");

            }
        });
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(numberOfVihaclesInvolved.getText().toString().equals("")||numberOfWitnesses.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),
                            "Please fill the number of vehicles involved and witnesses", Toast.LENGTH_LONG).show();
                }
                accidentRegistration.setVisibility(View.GONE);
                mViewPager.setVisibility(View.VISIBLE);
                mViewPagerWitness.setVisibility(View.GONE);
                drawerTitle.setVisibility(View.GONE);
                mTabs.setVisibility(View.VISIBLE);
                int p=Integer.parseInt(numberOfVihaclesInvolved.getText().toString());
                int q=Integer.parseInt(numberOfWitnesses.getText().toString());
                List<String> tabnamesVehiches= new ArrayList<String>();
                for(int i=0;i<p;i++){
                    tabnamesVehiches.add("Vehicle " + (i + 1));
                }
                List<String> tabnamesWitnesses= new ArrayList<String>();
                for(int i=0;i<q;i++){
                    tabnamesWitnesses.add("Witness "+(i+1));
                }
                ViewPagerAccidentsDetailsAdapter pager_adapter = new ViewPagerAccidentsDetailsAdapter(AccidentReportFormActivity.this,tabnamesVehiches);
                mViewPager.setAdapter(pager_adapter);
                mViewPager.setOffscreenPageLimit(10);
                mTabs.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);

                Resources res = getResources();
                mTabs.setSelectedIndicatorColors(res.getColor(R.color.yellow_200));
                mTabs.setDistributeEvenly(false);
                mTabs.setViewPager(mViewPager);

                ViewPagerWitnessesAdapter witnessPagerAdapter = new ViewPagerWitnessesAdapter(AccidentReportFormActivity.this,tabnamesWitnesses);
                mViewPagerWitness.setAdapter(witnessPagerAdapter);
                mViewPagerWitness.setOffscreenPageLimit(10);


                accident=new Accident();
                accident.setAccident_regNumber(accident_reg.getText().toString());
                accident.setFatal(Integer.parseInt(fatal.getText().toString()));
                accident.setSevere_injured(Integer.parseInt(severInjuries.getText().toString()));
                accident.setSimple(Integer.parseInt(simpleEdit.getText().toString()));
                accident.setOnly_damage(Integer.parseInt(onlyDamage.getText().toString()));
                accident.setTime(dateSelected+" "+timeSelected);




                try {
                    File imageFile = new File(imagePath);
                    accident.setImage_filename(imageFile.getName());
                }catch (Exception e){}

                try {
                    File videoFile = new File(videoPath);
                    accident.setVideo_filename(videoFile.getName());
                }catch (Exception e){}



                accident.setArea_name(areaName.getText().toString());
                accident.setDistrict(district.getText().toString());
                accident.setCity(city.getText().toString());
                accident.setRoad_name(roadName.getText().toString());
                accident.setIntersection_name(intersectionName.getText().toString());
                accident.setRoad_number(Integer.parseInt(roadNumber.getText().toString()));
                accident.setIntersection_number(Integer.parseInt(intersectionNumber.getText().toString()));
                accident.setRoad_km_mark(Integer.parseInt(roadMark.getText().toString()));
                accident.setIntersection_km_mark(Integer.parseInt(intersectionMark.getText().toString()));



            }
        });
        Button datePicker=(Button)findViewById(R.id.date_picker);
        Button timePicker=(Button)findViewById(R.id.time_picker);

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newDatePickerDialogueFragment=new DatePickerDialog();
                newDatePickerDialogueFragment.show(getFragmentManager(),"DatePickerDialogue");
                newDatePickerDialogueFragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
                        date.setText(dayOfMonth + " / " + (monthOfYear + 1) + " / "
                                + year);
                        dateSelected=dayOfMonth + " / " + (monthOfYear + 1) + " / "
                                + year;


                    }
                });
            }
        });

        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newTimerPickerDialogueFragment=new TimePickerDialog();
                newTimerPickerDialogueFragment.show(getFragmentManager(),"TimePickerDialogue");
                newTimerPickerDialogueFragment.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                        int hour;
                        String am_pm;
                        if (hourOfDay > 12) {
                            hour = hourOfDay - 12;
                            am_pm = "PM";
                        } else {
                            hour = hourOfDay;
                            am_pm = "AM";
                        }
                        time.setText(hour + " : " + minute + " " + am_pm);
                        timeSelected=hour + " : " + minute + " " + am_pm;
                    }
                });


            }
        });












    }

    public static void showWitneses(){
        accidentRegistration.setVisibility(View.GONE);
        mViewPager.setVisibility(View.GONE);
        mViewPagerWitness.setVisibility(View.VISIBLE);
        drawerTitle.setVisibility(View.GONE);
        mTabs.setViewPager(mViewPagerWitness);
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


    View.OnTouchListener editTextOnTouchListener =new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.requestLayout();
            AccidentReportFormActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
            return false;
        }
    };


}
