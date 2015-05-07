package com.RSMSA.policeApp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.RSMSA.policeApp.Dhis2.DHIS2Config;
import com.RSMSA.policeApp.Dhis2.DHIS2Modal;
import com.RSMSA.policeApp.Dialogues.AtcSelectDialogue;
import com.RSMSA.policeApp.Models.AccidentVehicle;
import com.RSMSA.policeApp.Models.PassengerVehicle;
import com.RSMSA.policeApp.Models.Witness;
import com.RSMSA.policeApp.Utils.AndroidMultiPartEntity;
import com.RSMSA.policeApp.Utils.Functions;
import com.RSMSA.policeApp.Utils.SystemBarTintManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.RSMSA.policeApp.Adapters.ViewPagerAccidentsDetailsAdapter;
import com.RSMSA.policeApp.Adapters.ViewPagerWitnessesAdapter;
import com.RSMSA.policeApp.CustomViews.TouchFeedbackEnabledRelativeLayout;
import com.RSMSA.policeApp.iRoadDB.IroadDatabase;


/**
 *  Created by isaiah on 10/22/2014.
 */
public class AccidentTypeclassificationActivity extends ActionBarActivity {
    private static final String TAG=AccidentTypeclassificationActivity.class.getSimpleName();
    /**
     * Select buttons
     */
    Button accidentTypeSelectButton, finishButton;
    public static final int POLICE_SIGNATURE = 100, DRIVER_A_SIGNATURE = 17, DRIVER_B_SIGNATURE = 23,
            WITNES_SIGNATURE = 24, DESC_SKETCH = 104;
    int selectedSpinner, selectedjunctionStructureSpinner, selectedjunctionControlSpinner, selectedroadTypeSpinner,
            selectedsurfaceTypeSpinner, selectedroadStructureSpinner, selectedsurfaceStatusSpinner, selectedroadSurfaceSpinner,
            selectedlightSpinner, selectedwhetherSpinner, selectedcontrolSpinner,selecteddefectOneSpinner,
            selecteddefectTwoSpinner;
    public final int REPORT_RESULT = 1;
    private LinearLayout driversSignaturesLayouts, witnessSignatureLayouts;
    /**
     *
     * Accident type classification spinner
     */
    public Spinner atcSpinner, junctionStructureSpinner, junctionControlSpinner, roadTypeSpinner, surfaceTypeSpinner, roadStructureSpinner,
            surfaceStatusSpinner, roadSurfaceSpinner, lightSpinner, whetherSpinner, controlSpinner;
    public ImageView scroller, policeImage, driverAImage, driverBImage, witnessImage;
    public TextView policeHint, driverAHint, driverBHint, witnessHint, sketchHint, sthinSelected;
    public static int currentSignature;
    public static final int SIGNATURE_ACTIVITY = 2;
    private Toolbar toolbar;
    private String imagePath=null, videoPath=null;
    private boolean isImage=true,isDriver=true;
    private long totalSize = 0;
    private String filePath;
    private ProgressBar progressBarDetermininate;
    private IroadDatabase iroadDatabase;
    private double mLat,mLong;
    private Timer gpsTimer = new Timer();
    private Location lastLocation=null;
    private String urlVideo="",urlImage="",urlPoliceSignature="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accident_type_classification);
        iroadDatabase = new IroadDatabase(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }


        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        final ColorDrawable colorDrawable= new ColorDrawable(getResources().getColor(R.color.blue_900));
        tintManager.setTintDrawable(colorDrawable);

        Intent intent = getIntent();
        imagePath = intent.getStringExtra("imagePath");
        videoPath = intent.getStringExtra("videoPath");
        isImage = intent.getBooleanExtra("isImage", true);


        scroller = (ImageView) findViewById(R.id.sign_capture);
        sketchHint = (TextView)findViewById(R.id.sketch_hint);
        sthinSelected = (TextView)findViewById(R.id.selected_atc);
        policeImage = (ImageView)findViewById(R.id.img_police);
        policeHint = (TextView)findViewById(R.id.police_hint);
        accidentTypeSelectButton = (Button)findViewById(R.id.accident_type_select_button);

        TouchFeedbackEnabledRelativeLayout police_signature = (TouchFeedbackEnabledRelativeLayout)findViewById(R.id.img_police_sign);
        police_signature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccidentTypeclassificationActivity.this, CaptureSignatureActivity.class);
                currentSignature = POLICE_SIGNATURE;
                startActivityForResult(intent, SIGNATURE_ACTIVITY);

            }
        });

        TouchFeedbackEnabledRelativeLayout descriptionDrawing = (TouchFeedbackEnabledRelativeLayout)findViewById(R.id.acc_description);
        descriptionDrawing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccidentTypeclassificationActivity.this, CaptureSignatureActivity.class);
                currentSignature = DESC_SKETCH;
                startActivityForResult(intent,SIGNATURE_ACTIVITY);
            }
        });

        progressBarDetermininate=(ProgressBar)findViewById(R.id.progressBar);
        driversSignaturesLayouts =(LinearLayout)findViewById(R.id.drivers_signatures_layouts);
        for(int r=0;r<ViewPagerAccidentsDetailsAdapter.accident.size();r++){
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            RelativeLayout driversSignature = (RelativeLayout)inflater.inflate(R.layout.drivers_signature,null);
            TouchFeedbackEnabledRelativeLayout rd1 = (TouchFeedbackEnabledRelativeLayout)driversSignature.findViewById(R.id.img_dr_a_sign);
            final int count=r;
            rd1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AccidentTypeclassificationActivity.this, CaptureSignatureActivity.class);
                    currentSignature =count;
                    isDriver=true;
                    startActivityForResult(intent,SIGNATURE_ACTIVITY);

                }
            });
            driversSignaturesLayouts.addView(driversSignature);

        }

        witnessSignatureLayouts =(LinearLayout)findViewById(R.id.witnessses_signatures_layouts);
        for(int r=0;r< ViewPagerWitnessesAdapter.witnesses.size();r++){
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            RelativeLayout driversSignature = (RelativeLayout)inflater.inflate(R.layout.witness_signature,null);
            TouchFeedbackEnabledRelativeLayout witnessSignatureLayout = (TouchFeedbackEnabledRelativeLayout)driversSignature.findViewById(R.id.img_dr_a_sign);
            final int count=r;
            witnessSignatureLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AccidentTypeclassificationActivity.this, CaptureSignatureActivity.class);
                    currentSignature = count;
                    isDriver = false;
                    startActivityForResult(intent, SIGNATURE_ACTIVITY);
                }
            });
            witnessSignatureLayouts.addView(driversSignature);
        }

        atcSpinner = (Spinner) findViewById(R.id.atc_spinner);
        junctionStructureSpinner = (Spinner) findViewById(R.id.junction_structure_spinner);
        junctionControlSpinner = (Spinner) findViewById(R.id.junction_control_spinner);
        roadTypeSpinner = (Spinner) findViewById(R.id.road_type_spinner);
        surfaceTypeSpinner = (Spinner) findViewById(R.id.surface_type_spinner);
        roadStructureSpinner = (Spinner) findViewById(R.id.road_structure_spinner);
        surfaceStatusSpinner = (Spinner) findViewById(R.id.road_status_spinner);
        roadSurfaceSpinner = (Spinner) findViewById(R.id.road_surface_spinner);
        lightSpinner = (Spinner) findViewById(R.id.light_spinner);
        whetherSpinner = (Spinner) findViewById(R.id.wheather_spinner);
        controlSpinner = (Spinner) findViewById(R.id.control_spinner);




        List<String> list = new ArrayList<String>();
        Cursor cursor = iroadDatabase.query("SELECT * FROM "+IroadDatabase.TABLE_OPTION_SETS+" WHERE "+IroadDatabase.KEY_NAME+" = 'Accident Type Classification'");
        int cnt = cursor.getCount();
        for (int i = 0; i < cnt; i++){
            cursor.moveToPosition(i);
            list.add(cursor.getString(cursor.getColumnIndex(IroadDatabase.KEY_OPTION_NAME)));
        }

        final List<String> junctionStructure = new ArrayList<String>();
        junctionStructure.add("Crossing Roads");
        junctionStructure.add("Round About");
        junctionStructure.add("T Junction");
        junctionStructure.add("Y Junction");
        junctionStructure.add("Staggered Junction");
        junctionStructure.add("Junc > 4 Arms");
        junctionStructure.add("Bridge/Fly over");
        junctionStructure.add("Rail Cross Manned");
        junctionStructure.add("Rail Cross No Sign");
        junctionStructure.add("Pedestrian Cross");
        junctionStructure.add("none");

        final List<String> junctionControl = new ArrayList<String>();
        junctionControl.add("Uncontrolled");
        junctionControl.add("Police Officer");
        junctionControl.add("Traffic Signs");
        junctionControl.add("Traffic Light");
        junctionControl.add("Flashing signal");
        junctionControl.add("none");

        final List<String> roadType = new ArrayList<String>();
        roadType.add("Trunk Roads");
        roadType.add("Regional Roads");
        roadType.add("District Roads");
        roadType.add("City Roads");
        roadType.add("Rural Roads");
        roadType.add("Bridges");

        final List<String> surfaceType = new ArrayList<String>();
        surfaceType.add("Paved");
        surfaceType.add("Unpaved");
        surfaceType.add("concrete");
        surfaceType.add("Metal Bridge");
        surfaceType.add("Gravel");
        surfaceType.add("Sandy");

        final List<String> roadStructure = new ArrayList<String>();
        roadStructure.add("No Lanes");
        roadStructure.add("1, 2, 3 Lanes");
        roadStructure.add("Hard Shoulder");
        roadStructure.add("Straight");
        roadStructure.add("Slight Curve");
        roadStructure.add("Sharp Curve");

        final List<String> surfaceStatus = new ArrayList<String>();
        surfaceStatus.add("Flat Road");
        surfaceStatus.add("Gentle Slope");
        surfaceStatus.add("Steep Slope");
        surfaceStatus.add("Hump/Bump");
        surfaceStatus.add("Dip (Hole/Drift)");
        surfaceStatus.add("Road Works");

        final List<String> roadSurface = new ArrayList<String>();
        roadSurface.add("Dry");
        roadSurface.add("Wet");
        roadSurface.add("Rain");
        roadSurface.add("Water");
        roadSurface.add("Muddy");
        roadSurface.add("Debris");

        final List<String> light = new ArrayList<String>();
        light.add("Day");
        light.add("Twilight");
        light.add("Night");
        light.add("Smoke");
        light.add("Street Light");


        final List<String> weather = new ArrayList<String>();
        Cursor cursor2 = iroadDatabase.query("SELECT * FROM "+IroadDatabase.TABLE_OPTION_SETS+" WHERE "+IroadDatabase.KEY_NAME+" = 'Weather'");
        int cnt2 = cursor2.getCount();
        Log.d(TAG,"weater count = "+cnt2);
        for (int i = 0; i < cnt2; i++){
            cursor2.moveToPosition(i);
            weather.add(cursor2.getString(cursor2.getColumnIndex(IroadDatabase.KEY_OPTION_NAME)));
        }

        final List<String> control = new ArrayList<String>();
        control.add("Trafic Signal");
        control.add("No Trafic Signal");
        control.add("Lane Marking");
        control.add("Speed Limit/Sign");



        final List<String> violation = new ArrayList<String>();
        Cursor cursor3 = iroadDatabase.query("SELECT * FROM "+IroadDatabase.TABLE_OPTION_SETS+" WHERE "+IroadDatabase.KEY_NAME+" = 'Cause of Accident'");
        int cnt3 = cursor3.getCount();
        for (int i = 0; i < cnt3; i++){
            cursor3.moveToPosition(i);
            violation.add(cursor3.getString(cursor3.getColumnIndex(IroadDatabase.KEY_OPTION_NAME)));
        }

        final List<String> defects = new ArrayList<String>();
        defects.add("Breaks");
        defects.add("Bad Light");
        defects.add("Bad Tyre");
        defects.add("Tyre Burst");
        defects.add("Others");

        finishButton = (Button) findViewById(R.id.finish_button);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File [] files = new File[3];
                try {
                    files[0] = new File(videoPath);

                }catch (Exception e){
                    files[0]= new File("");
                }


                try {
                    files[1] = new File(imagePath);

                }catch (Exception e){
                    files[1]= new File("");
                }
                try {
                    files[2] = new File(AccidentReportFormActivity.accident.getPolice_signatureFilePath());

                }catch (Exception e){
                    files[2]= new File("");
                }

                UploadFileToServer.execute(files);


            }
        });




        atcSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSpinner = i;
                if (i == 0){
                    selectedSpinner = 234;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        AccidentReportFormActivity.accident.setJunction_control(junctionControl.get(0));
        junctionControlSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    selectedjunctionControlSpinner = 178;
                } else if (i == 1) {
                    selectedjunctionControlSpinner = 177;
                } else if (i == 2) {
                    selectedjunctionControlSpinner = 176;
                } else if (i == 3) {
                    selectedjunctionControlSpinner = 175;
                } else if (i == 4) {
                    selectedjunctionControlSpinner = 174;
                } else if (i == 5) {
                    selectedjunctionControlSpinner = 173;
                }
                AccidentReportFormActivity.accident.setJunction_control(junctionControl.get(0));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        AccidentReportFormActivity.accident.setJunction_structure(junctionStructure.get(0));
        junctionStructureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0) {
                    selectedjunctionStructureSpinner = 189;
                } else if (i == 1) {
                    selectedjunctionStructureSpinner = 188;
                } else if (i == 2) {
                    selectedjunctionStructureSpinner = 187;
                } else if (i == 3) {
                    selectedjunctionStructureSpinner = 186;
                } else if (i == 4) {
                    selectedjunctionStructureSpinner = 185;
                } else if (i == 5) {
                    selectedjunctionStructureSpinner = 184;
                } else if (i == 6) {
                    selectedjunctionStructureSpinner = 183;
                } else if (i == 7) {
                    selectedjunctionStructureSpinner = 182;
                } else if (i == 8) {
                    selectedjunctionStructureSpinner = 181;
                } else if (i == 9) {
                    selectedjunctionStructureSpinner = 180;
                } else if (i == 10) {
                    selectedjunctionStructureSpinner = 179;
                }
                AccidentReportFormActivity.accident.setJunction_structure(junctionStructure.get(i));

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        AccidentReportFormActivity.accident.setRoad_type(roadType.get(0));
        roadTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0) {
                    selectedroadTypeSpinner = 172;
                } else if (i == 1) {
                    selectedroadTypeSpinner = 171;
                } else if (i == 2) {
                    selectedroadTypeSpinner = 170;
                } else if (i == 3) {
                    selectedroadTypeSpinner = 169;
                } else if (i == 4) {
                    selectedroadTypeSpinner = 168;
                } else if (i == 5) {
                    selectedroadTypeSpinner = 167;
                }
                AccidentReportFormActivity.accident.setRoad_type(roadType.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        AccidentReportFormActivity.accident.setSurface_type(surfaceType.get(0));
        surfaceTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0) {
                    selectedsurfaceTypeSpinner = 166;
                } else if (i == 1) {
                    selectedsurfaceTypeSpinner = 165;
                } else if (i == 2) {
                    selectedsurfaceTypeSpinner = 164;
                } else if (i == 3) {
                    selectedsurfaceTypeSpinner = 163;
                } else if (i == 4) {
                    selectedsurfaceTypeSpinner = 162;
                } else if (i == 5) {
                    selectedsurfaceTypeSpinner = 161;
                }
                AccidentReportFormActivity.accident.setRoad_type(surfaceType.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        AccidentReportFormActivity.accident.setRoad_structure(roadStructure.get(0));
        roadStructureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0) {
                    selectedroadStructureSpinner = 160;
                } else if (i == 1) {
                    selectedroadStructureSpinner = 159;
                } else if (i == 2) {
                    selectedroadStructureSpinner = 158;
                } else if (i == 3) {
                    selectedroadStructureSpinner = 157;
                } else if (i == 4) {
                    selectedroadStructureSpinner = 156;
                } else if (i == 5) {
                    selectedroadStructureSpinner = 155;
                }
                AccidentReportFormActivity.accident.setRoad_structure(roadStructure.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        AccidentReportFormActivity.accident.setSurface_status(surfaceStatus.get(0));
        surfaceStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    selectedsurfaceStatusSpinner = 154;
                } else if (i == 1) {
                    selectedsurfaceStatusSpinner = 153;
                } else if (i == 2) {
                    selectedsurfaceStatusSpinner = 152;
                } else if (i == 3) {
                    selectedsurfaceStatusSpinner = 151;
                } else if (i == 4) {
                    selectedsurfaceStatusSpinner = 150;
                } else if (i == 5) {
                    selectedsurfaceStatusSpinner = 149;
                }
                AccidentReportFormActivity.accident.setSurface_status(surfaceStatus.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        AccidentReportFormActivity.accident.setRoad_surface(roadSurface.get(0));
        roadSurfaceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0) {
                    selectedroadSurfaceSpinner = 148;
                } else if (i == 1) {
                    selectedroadSurfaceSpinner = 147;
                } else if (i == 2) {
                    selectedroadSurfaceSpinner = 146;
                } else if (i == 3) {
                    selectedroadSurfaceSpinner = 145;
                } else if (i == 4) {
                    selectedroadSurfaceSpinner = 144;
                } else if (i == 5) {
                    selectedroadSurfaceSpinner = 143;
                }
                AccidentReportFormActivity.accident.setRoad_surface(roadSurface.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        AccidentReportFormActivity.accident.setLight(light.get(0));
        lightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0) {
                    selectedlightSpinner = 142;
                } else if (i == 1) {
                    selectedlightSpinner = 141;
                } else if (i == 2) {
                    selectedlightSpinner = 140;
                } else if (i == 3) {
                    selectedlightSpinner = 139;
                } else if (i == 4) {
                    selectedlightSpinner = 138;
                }
                AccidentReportFormActivity.accident.setLight(light.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        AccidentReportFormActivity.accident.setWeather(weather.get(0));
        whetherSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0) {
                    selectedwhetherSpinner = 137;
                } else if (i == 1) {
                    selectedwhetherSpinner = 136;
                } else if (i == 2) {
                    selectedwhetherSpinner = 135;
                } else if (i == 3) {
                    selectedwhetherSpinner = 134;
                }
                AccidentReportFormActivity.accident.setWeather(weather.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        AccidentReportFormActivity.accident.setControl(control.get(0));
        controlSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0){
                    selectedcontrolSpinner = 133;
                }else if(i==1){
                    selectedcontrolSpinner = 132;
                }else if(i==2){
                    selectedcontrolSpinner = 131;
                }else if(i==3){
                    selectedcontrolSpinner = 130;
                }
                AccidentReportFormActivity.accident.setControl(control.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        LinearLayout violationsLayout=(LinearLayout)findViewById(R.id.violations_layouts);
        for( int i=0;i<ViewPagerAccidentsDetailsAdapter.accident.size();i++){
            final int count=i;
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            RelativeLayout vehiclesViolations = (RelativeLayout)inflater.inflate(R.layout.vehicles_violations,null);
            Spinner violationSpinner = (Spinner) vehiclesViolations.findViewById(R.id.violations_spinner);

            TextView violationsTitle=(TextView)vehiclesViolations.findViewById(R.id.violations_title);
            violationsTitle.append(" "+(i+1));


            ArrayAdapter<String> violationOneAdapter = new ArrayAdapter<String>
                    (this, android.R.layout.simple_spinner_item,violation);
            violationOneAdapter.setDropDownViewResource
                    (android.R.layout.simple_spinner_dropdown_item);

            violationSpinner.setAdapter(violationOneAdapter);
            //ViewPagerAccidentsDetailsAdapter.accident.get(i).setViolations(violation.get(0));
            violationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    ViewPagerAccidentsDetailsAdapter.accident.get(count).setViolations(violation.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            violationsLayout.addView(vehiclesViolations);
        }

        LinearLayout defectsLayout=(LinearLayout)findViewById(R.id.defects_layouts);
        for( int i=0;i<ViewPagerAccidentsDetailsAdapter.accident.size();i++){
            final int count=i;
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            RelativeLayout vehiclesDefects = (RelativeLayout)inflater.inflate(R.layout.vehicle_defects,null);
            Spinner defectsSpinner = (Spinner) vehiclesDefects.findViewById(R.id.defects_spinner);

            TextView defectsTitle=(TextView)vehiclesDefects.findViewById(R.id.defects_title);
            defectsTitle.append((i+1)+" Defects");


            ArrayAdapter<String> defectAdapter = new ArrayAdapter<String>
                    (this, android.R.layout.simple_spinner_item,defects);
            defectAdapter.setDropDownViewResource
                    (android.R.layout.simple_spinner_dropdown_item);

            defectsSpinner.setAdapter(defectAdapter);
            ViewPagerAccidentsDetailsAdapter.accident.get(i).setDefects(defects.get(0));
            defectsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    ViewPagerAccidentsDetailsAdapter.accident.get(count).setDefects(defects.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            defectsLayout.addView(vehiclesDefects);
        }


        ArrayAdapter<String> atc_adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,list);
        atc_adapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        atcSpinner.setAdapter(atc_adapter);


        ArrayAdapter<String> junction_structure = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,junctionStructure);
        junction_structure.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        junctionStructureSpinner.setAdapter(junction_structure);

        ArrayAdapter<String> junctionControlAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,junctionControl);
        junctionControlAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        junctionControlSpinner.setAdapter(junctionControlAdapter);

        ArrayAdapter<String> roadTypeAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,roadType);
        roadTypeAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        roadTypeSpinner.setAdapter(roadTypeAdapter);

        ArrayAdapter<String> surfaceTypeAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,surfaceType);
        surfaceTypeAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        surfaceTypeSpinner.setAdapter(surfaceTypeAdapter);

        ArrayAdapter<String> roadStructureAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,roadStructure);
        roadStructureAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        roadStructureSpinner.setAdapter(roadStructureAdapter);

        ArrayAdapter<String> surfaceStatusAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,surfaceStatus);
        surfaceStatusAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        surfaceStatusSpinner.setAdapter(surfaceStatusAdapter);

        ArrayAdapter<String> roadSurfaceAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,roadSurface);
        roadSurfaceAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        roadSurfaceSpinner.setAdapter(roadSurfaceAdapter);

        ArrayAdapter<String> lightAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,light);
        lightAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        lightSpinner.setAdapter(lightAdapter);

        ArrayAdapter<String> weatherAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,weather);
        weatherAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        whetherSpinner.setAdapter(weatherAdapter);

        ArrayAdapter<String> controlAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,control);
        controlAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        controlSpinner.setAdapter(controlAdapter);


        accidentTypeSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent atcselectintent = new Intent(AccidentTypeclassificationActivity.this, AtcSelectDialogue.class);
                atcselectintent.putExtra("classification", selectedSpinner+"");
                Log.d("selected", atcSpinner.getSelectedItem()+"");
                startActivity(atcselectintent);
            }
        });
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

    @Override
    public void onResume(){
        super.onResume();
        startRecording();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gpsTimer.cancel();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REPORT_RESULT) {
            if (resultCode == RESULT_OK) {
                // code for result
                final Bundle bundle = data.getExtras();
                Log.d("resulty", bundle.getString("item")+"############");
            }
            if (resultCode == RESULT_CANCELED) {
                // Write your code on no result return
            }
        }

        else if(requestCode == SIGNATURE_ACTIVITY){

            if (resultCode == RESULT_OK) {

                Bundle bundle = data.getExtras();
                String status  = bundle.getString("status");
                String myfile = bundle.getString("path");
                String myStringImage = bundle.getString("path_to_file");
                String myImagename=bundle.getString("file_name");
                filePath=myStringImage;
                if(status.equalsIgnoreCase("done")){
                    Log.d("path to file ==  ", myStringImage);
                    Bitmap bmImg = BitmapFactory.decodeFile(myfile);
                    if (currentSignature == POLICE_SIGNATURE){
                        policeHint.setVisibility(View.GONE);
                        AccidentReportFormActivity.accident.setPolice_signature(myImagename);
                        AccidentReportFormActivity.accident.setPolice_signatureFilePath(myStringImage);
                        policeImage.setImageBitmap(bmImg);
                    }else if (currentSignature == DESC_SKETCH){
                        sketchHint.setVisibility(View.GONE);
                        AccidentReportFormActivity.accident.setDescription(myImagename);
                        AccidentReportFormActivity.accident.setDescriptionFilePath(myStringImage);
                        scroller.setImageBitmap(bmImg);
                    }else if(isDriver){
                        RelativeLayout sign=(RelativeLayout) driversSignaturesLayouts.getChildAt(currentSignature);
                        sign.findViewById(R.id.driverA_hint).setVisibility(View.GONE);
                        ViewPagerAccidentsDetailsAdapter.accident.get(currentSignature).setSignature(myImagename);
                        ViewPagerAccidentsDetailsAdapter.accident.get(currentSignature).setSignatureFilePath(myStringImage);
                        ((ImageView)sign.findViewById(R.id.img_driver)).setImageBitmap(bmImg);
                    }else{
                        RelativeLayout sign=(RelativeLayout) witnessSignatureLayouts.getChildAt(currentSignature);
                        sign.findViewById(R.id.driverA_hint).setVisibility(View.GONE);
                        ViewPagerWitnessesAdapter.witnesses.get(currentSignature).setSignature(myImagename);
                        ViewPagerWitnessesAdapter.witnesses.get(currentSignature).setSignatureFilePath(myStringImage);
                        ((ImageView)sign.findViewById(R.id.img_driver)).setImageBitmap(bmImg);
                    }
                }
            }
            else {
                if (currentSignature == POLICE_SIGNATURE){
                    policeHint.setVisibility(View.VISIBLE);
                    policeImage.setImageBitmap(null);
                }
                else if (currentSignature == DRIVER_A_SIGNATURE){
                    driverAHint.setVisibility(View.VISIBLE);
                    driverAImage.setImageBitmap(null);
                }
                else if (currentSignature == DRIVER_B_SIGNATURE){
                    driverBHint.setVisibility(View.VISIBLE);
                    driverBImage.setImageBitmap(null);
                }
                else if (currentSignature == WITNES_SIGNATURE){
                    witnessHint.setVisibility(View.VISIBLE);
                    witnessImage.setImageBitmap(null);
                }
            }
        }
    }

    AsyncTask  UploadFileToServer = new  AsyncTask<File, Integer, Void>() {


        @Override
        protected Void doInBackground(File... params) {
            File [] files = params;
            int counter = params.length;
            for(int i=0;i<counter;i++) {
                JSONObject resultjson = uploadFile(files[i]);
                try {
                    String responceUrl = resultjson.getJSONArray("documents").getJSONObject(0).getString("href");
                    switch (i){
                        case 0:
                            urlVideo = responceUrl;
                            break;
                        case 1:
                            urlImage = responceUrl;
                            break;
                        case 2:
                            urlPoliceSignature = responceUrl;
                            break;
                    }

                } catch (Exception e) {
                    switch (i){
                        case 0:
                            urlVideo = "";
                            break;
                        case 1:
                            urlImage = "";
                            break;
                        case 2:
                            urlPoliceSignature = "";
                            break;
                    }
                }
            }

            Location location =getBestLocation();
            mLat=location.getLatitude();
            mLong=location.getLongitude();

            String placeName=getAddress(mLat,mLong);
            Calendar cl = Calendar.getInstance();


            JSONObject eventAccident = new JSONObject();
            DHIS2Modal accidentModal = new DHIS2Modal("Accident",null,MainOffence.username,MainOffence.password);

            String accidentProgramUid = accidentModal.getProgramByName("Accident").getId();
            //TODO handle users with multiple orgUnits
            String organizationUnit = MainOffence.orgUnit;

            JSONObject coordinatesObject = new JSONObject();
            try {
                coordinatesObject.put("latitude",mLat);
                coordinatesObject.put("longitude",mLong);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                eventAccident.put("program",accidentProgramUid);
                eventAccident.put("orgUnit",organizationUnit);
                eventAccident.put("eventDate",Functions.getDateFromUnixTimestamp(cl.getTimeInMillis()));
                eventAccident.put("coordinate",coordinatesObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONArray values = new JSONArray();
            JSONObject programPoliceDataElement = new JSONObject();
            String programPoliceUid = accidentModal.getDataElementByName("Program_Police").getId();
            try {
                programPoliceDataElement.put("dataElement",programPoliceUid);
                //TODO implement login mechanism and store data in the datatbase
                programPoliceDataElement.put("value","aQylIO5YSxD");
                values.put(programPoliceDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONObject roadNumberDataElement = new JSONObject();
            String roadNumberDataElementUid = accidentModal.getDataElementByName("Road Number").getId();
            try {
                roadNumberDataElement.put("dataElement",roadNumberDataElementUid);
                roadNumberDataElement.put("value",AccidentReportFormActivity.accident.getRoad_number());
                values.put(roadNumberDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONObject intersectionMarkDataElement = new JSONObject();
            String intersectionMarkDataElementtUid = accidentModal.getDataElementByName("Intersection Mark").getId();
            try {
                intersectionMarkDataElement.put("dataElement",intersectionMarkDataElementtUid);
                intersectionMarkDataElement.put("value",AccidentReportFormActivity.accident.getIntersection_km_mark());
                values.put(intersectionMarkDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONObject intersectionNameDataElement = new JSONObject();
            String intersectionNameDataElementtUid = accidentModal.getDataElementByName("Intersection Name").getId();
            try {
                intersectionNameDataElement.put("dataElement",intersectionNameDataElementtUid);
                intersectionNameDataElement.put("value",AccidentReportFormActivity.accident.getIntersection_name());
                values.put(intersectionNameDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }



            JSONObject roadMarkDataElement = new JSONObject();
            String roadMarkDataElementUid = accidentModal.getDataElementByName("Road Mark").getId();
            try {
                roadMarkDataElement.put("dataElement",roadMarkDataElementUid);
                roadMarkDataElement.put("value",AccidentReportFormActivity.accident.getRoad_km_mark());
                values.put(roadMarkDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONObject causeOfAccidentDataElement = new JSONObject();
            String causeOfAccidentDataElementUid = accidentModal.getDataElementByName("Cause of Accident").getId();
            try {
                causeOfAccidentDataElement.put("dataElement",causeOfAccidentDataElementUid);
                //TODO fix this with the correct corresponding value
                causeOfAccidentDataElement.put("value","");
                values.put(causeOfAccidentDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONObject weatherDataElement = new JSONObject();
            String weatherDataElementUid = accidentModal.getDataElementByName("Weather").getId();
            try {
                weatherDataElement.put("dataElement",weatherDataElementUid);
                weatherDataElement.put("value",AccidentReportFormActivity.accident.getWeather());
                values.put(weatherDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONObject latitudeDataElement = new JSONObject();
            String latitudeDataElementUid = accidentModal.getDataElementByName("Latitude").getId();
            try {
                latitudeDataElement.put("dataElement",latitudeDataElementUid);
                latitudeDataElement.put("value",mLat);
                values.put(latitudeDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }




            JSONObject LongitudeDataElement = new JSONObject();
            String LongitudeDataElementUid = accidentModal.getDataElementByName("Longitude").getId();
            try {
                LongitudeDataElement.put("dataElement",LongitudeDataElementUid);
                LongitudeDataElement.put("value",mLong);
                values.put(LongitudeDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONObject roadNameDataElement = new JSONObject();
            String roadNameDataElementUid = accidentModal.getDataElementByName("Road Name").getId();
            try {
                roadNameDataElement.put("dataElement",roadNameDataElementUid);
                roadNameDataElement.put("value",AccidentReportFormActivity.accident.getRoad_name());
                values.put(roadNameDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONObject timeOfAccidentDataElement = new JSONObject();
            String timeOfAccidentDataElementUid = accidentModal.getDataElementByName("Time of Accident").getId();
            try {
                timeOfAccidentDataElement.put("dataElement",timeOfAccidentDataElementUid);
                timeOfAccidentDataElement.put("value",Functions.getDateFromUnixTimestamp(cl.getTimeInMillis()));
                values.put(timeOfAccidentDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONObject numberOfSimpleInjuriesDataElement = new JSONObject();
            String numberOfSimpleInjuriesDataElementUid = accidentModal.getDataElementByName("Number of Simple Injuries").getId();
            try {
                numberOfSimpleInjuriesDataElement.put("dataElement",numberOfSimpleInjuriesDataElementUid);
                numberOfSimpleInjuriesDataElement.put("value",AccidentReportFormActivity.accident.getSimple());
                values.put(numberOfSimpleInjuriesDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }



            JSONObject numberOfFatalInjuriesDataElement = new JSONObject();
            String numberOfFatalInjuriesDataElementUid = accidentModal.getDataElementByName("Number of Fatal Injuries").getId();
            try {
                numberOfFatalInjuriesDataElement.put("dataElement", numberOfFatalInjuriesDataElementUid);
                numberOfFatalInjuriesDataElement.put("value", AccidentReportFormActivity.accident.getFatal());
                values.put(numberOfFatalInjuriesDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }



            JSONObject accidentClassDataElement = new JSONObject();
            String accidentClassDataElementUid = accidentModal.getDataElementByName("Accident Class").getId();
            try {
                accidentClassDataElement.put("dataElement", accidentClassDataElementUid);
                //Fix this with the correct value
                accidentClassDataElement.put("value","");
                values.put(accidentClassDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }



            JSONObject numberOfSevereInjuriesDataElement = new JSONObject();
            String numberOfSevereInjuriesDataElementUid = accidentModal.getDataElementByName("Number of Severe Injuries").getId();
            try {
                numberOfSevereInjuriesDataElement.put("dataElement", numberOfSevereInjuriesDataElementUid);
                numberOfSevereInjuriesDataElement.put("value", AccidentReportFormActivity.accident.getSevere_injured());
                values.put(numberOfSevereInjuriesDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }



            JSONObject accidentTypeClassificationDataElement = new JSONObject();
            String accidentTypeClassificationDataElementUid = accidentModal.getDataElementByName("Accident Type").getId();
            try {
                accidentTypeClassificationDataElement.put("dataElement", accidentTypeClassificationDataElementUid);
                accidentTypeClassificationDataElement.put("value", AccidentReportFormActivity.accident.getAccident_type());
                values.put(accidentTypeClassificationDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONObject accidentTypeDetailDataElement = new JSONObject();
            String accidentTypeDetailDataElementUid = accidentModal.getDataElementByName("Accident Type Detail").getId();
            try {
                accidentTypeDetailDataElement.put("dataElement", accidentTypeDetailDataElementUid);
                //TODO GET the right info..
                accidentTypeDetailDataElement.put("value", "");
                values.put(accidentTypeDetailDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }



            JSONObject  area0fAccidentDataElement = new JSONObject();
            String area0fAccidentDataElementUid = accidentModal.getDataElementByName("Area of Accident").getId();
            try {
                area0fAccidentDataElement.put("dataElement", area0fAccidentDataElementUid);
                area0fAccidentDataElement.put("value", AccidentReportFormActivity.accident.getArea_name());
                values.put(area0fAccidentDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }



            JSONObject  intersectionNumberDataElement = new JSONObject();
            String intersectionNumberDataElementUid = accidentModal.getDataElementByName("Intersection Number").getId();
            try {
                intersectionNumberDataElement.put("dataElement", intersectionNumberDataElementUid);
                intersectionNumberDataElement.put("value", AccidentReportFormActivity.accident.getIntersection_number());
                values.put(intersectionNumberDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONObject  accidentImageDataElement = new JSONObject();
            String accidentImageDataElementUid = accidentModal.getDataElementByName("Accident Image").getId();
            try {
                accidentImageDataElement.put("dataElement", accidentImageDataElementUid);
                accidentImageDataElement.put("value", urlImage);
                values.put(accidentImageDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONObject  accidentVideoDataElement = new JSONObject();
            String accidentVideoDataElementUid = accidentModal.getDataElementByName("Accident Video").getId();
            try {
                accidentVideoDataElement.put("dataElement", accidentVideoDataElementUid);
                accidentVideoDataElement.put("value", urlVideo);
                values.put(accidentVideoDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONObject  accidentSignatureDataElement = new JSONObject();
            String accidentSignatureDataElementUid = accidentModal.getDataElementByName("Signature").getId();
            try {
                accidentSignatureDataElement.put("dataElement", accidentSignatureDataElementUid);
                accidentSignatureDataElement.put("value", urlPoliceSignature);
                values.put(accidentSignatureDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                eventAccident.put("dataValues",values);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONParser jsonParser = new JSONParser();
            JSONObject result = jsonParser.dhis2HttpRequest(DHIS2Config.BASE_URL+"api/events","POST",MainOffence.username,MainOffence.password,eventAccident);

            try {
                String reference = result.getJSONArray("importSummaries").getJSONObject(0).getString("reference");
                int size= ViewPagerAccidentsDetailsAdapter.accident.size();
                for(int i=0;i<size;i++){
                    AccidentVehicle accidentVehicle=ViewPagerAccidentsDetailsAdapter.accident.get(i);

                    JSONObject eventAccidentVehicle = new JSONObject();
                    DHIS2Modal accidentVehicleModal = new DHIS2Modal("Accident Vehicle",null,MainOffence.username,MainOffence.password);

                    String accidentVehicleProgramUid = accidentVehicleModal.getProgramByName("Accident Vehicle").getId();
                    try {
                        eventAccidentVehicle.put("program",accidentVehicleProgramUid);
                        eventAccidentVehicle.put("orgUnit",organizationUnit);
                        eventAccidentVehicle.put("eventDate",Functions.getDateFromUnixTimestamp(cl.getTimeInMillis()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    JSONArray accidentVehicleDataValues = new JSONArray();
                    JSONObject programAccidentVehicleDataElement = new JSONObject();
                    String programAccidentVehicleDataElementUid = accidentVehicleModal.getDataElementByName("Program_Accident").getId();
                    try {
                        programAccidentVehicleDataElement.put("dataElement",programAccidentVehicleDataElementUid);
                        programAccidentVehicleDataElement.put("value",reference);
                        accidentVehicleDataValues.put(programAccidentVehicleDataElement);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONObject programVehicleDataElement = new JSONObject();
                    String programVehicleDataElementUid = accidentVehicleModal.getDataElementByName("Program_Vehicle").getId();
                    try {
                        programVehicleDataElement.put("dataElement",programVehicleDataElementUid);
                        programVehicleDataElement.put("value",accidentVehicle.getProgram_vehicle());
                        accidentVehicleDataValues.put(programVehicleDataElement);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    JSONObject programDriverDataElement = new JSONObject();
                    String programDriverDataElementUid = accidentVehicleModal.getDataElementByName("Program_Driver").getId();
                    try {
                        programDriverDataElement.put("dataElement",programDriverDataElementUid);
                        programDriverDataElement.put("value",accidentVehicle.getProgram_driver());
                        accidentVehicleDataValues.put(programDriverDataElement);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    File file = new File(accidentVehicle.getSignatureFilePath());
                    String signatureUrl="";
                    try {
                        JSONObject signatureResultJson = uploadFile(file);
                        signatureUrl = signatureResultJson.getJSONArray("documents").getJSONObject(0).getString("href");
                    }catch (Exception e){}

                    JSONObject signatureDataElement = new JSONObject();
                    String signatureDataElementUid = accidentVehicleModal.getDataElementByName("Signature").getId();
                    try {
                        signatureDataElement.put("dataElement",signatureDataElementUid);
                        signatureDataElement.put("value",signatureUrl);
                        accidentVehicleDataValues.put(signatureDataElement);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    JSONObject numberofFatalInjuriesDataElement = new JSONObject();
                    String numberofFatalInjuriesDataElementUid = accidentVehicleModal.getDataElementByName("Number of Fatal Injuries").getId();
                    try {
                        numberofFatalInjuriesDataElement.put("dataElement",numberofFatalInjuriesDataElementUid);
                        numberofFatalInjuriesDataElement.put("value",signatureUrl);
                        accidentVehicleDataValues.put(numberofFatalInjuriesDataElement);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                    JSONObject numberOfNotDamagedDataElement = new JSONObject();
                    String numberOfNotDamagedDataElementUid = accidentVehicleModal.getDataElementByName("Number of Not Damaged").getId();
                    try {
                        numberOfNotDamagedDataElement.put("dataElement",numberOfNotDamagedDataElementUid);
                        numberOfNotDamagedDataElement.put("value",signatureUrl);
                        accidentVehicleDataValues.put(numberOfNotDamagedDataElement);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    JSONObject numberOfSimpleVehiclesInjuriesDataElement = new JSONObject();
                    String numberOfSimpleVehiclesInjuriesDataElementUid = accidentVehicleModal.getDataElementByName("Number of Simple Injuries").getId();
                    try {
                        numberOfSimpleVehiclesInjuriesDataElement.put("dataElement",numberOfSimpleVehiclesInjuriesDataElementUid);
                        numberOfSimpleVehiclesInjuriesDataElement.put("value",signatureUrl);
                        accidentVehicleDataValues.put(numberOfSimpleVehiclesInjuriesDataElement);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                    JSONObject numberOfSevereInjuriesDriverDataElement = new JSONObject();
                    String numberOfSevereInjuriesDriverDataElementUid = accidentVehicleModal.getDataElementByName("Number of Severe Injuries").getId();
                    try {
                        numberOfSevereInjuriesDriverDataElement.put("dataElement",numberOfSevereInjuriesDriverDataElementUid);
                        numberOfSevereInjuriesDriverDataElement.put("value",signatureUrl);
                        accidentVehicleDataValues.put(numberOfSevereInjuriesDriverDataElement);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        eventAccidentVehicle.put("dataValues",accidentVehicleDataValues);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONObject result2 = jsonParser.dhis2HttpRequest(DHIS2Config.BASE_URL+"api/events","POST",MainOffence.username,MainOffence.password,eventAccidentVehicle);


                    Log.d(TAG,"vehicles "+i+" responce json = "+result2.toString());

//                    List<PassengerVehicle> passengerVehicle=ViewPagerAccidentsDetailsAdapter.passanger.get(i);
//                    JSONArray passengersJsonArray=new JSONArray();
//                    for(int p=0;p<passengerVehicle.size();p++){
//                        try {
//                            passengersJsonArray.put(p, passengerVehicle.get(p).getjson(passengerVehicle.get(p)));
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBarDetermininate.setVisibility(View.VISIBLE);
            progressBarDetermininate.setProgress(0);
            finishButton.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(),
                    "uploading Files and information to the server", Toast.LENGTH_LONG).show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            finishButton.setVisibility(View.VISIBLE);
            progressBarDetermininate.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(),
                    "Files uploaded successfully", Toast.LENGTH_LONG).show();

            Log.d(TAG,"video file url = "+urlVideo);
            Log.d(TAG,"image file url = "+urlImage);
            Log.d(TAG,"police signature file url = "+urlPoliceSignature);

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressBarDetermininate.setVisibility(View.VISIBLE);
            progressBarDetermininate.setProgress(progress[0]);
            super.onProgressUpdate(progress);
        }

        @SuppressWarnings("deprecation")
        private JSONObject uploadFile(File sourceFile) {
            String responseString = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(DHIS2Config.BASE_URL+"dhis-web-reporting/saveDocument.action");
            String base64EncodedCredentials = "Basic " + Base64.encodeToString(
                    (MainOffence.username + ":" + MainOffence.password).getBytes(),
                    Base64.NO_WRAP);

            Log.d(TAG,"encoded credentials = "+base64EncodedCredentials);

            httppost.setHeader("Authorization", base64EncodedCredentials);


            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {
                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });


                //TODO obtain both video path and imagePath from Accident object
                try{
                    Log.d(TAG,"file name = "+sourceFile.getName());
                    entity.addPart("upload", new FileBody(sourceFile));
                    entity.addPart("name", new StringBody(sourceFile.getName()));
                    entity.addPart("external", new StringBody("false"));
                    entity.addPart("id", new StringBody(""));
                    entity.addPart("url", new StringBody("http://"));
                }catch (Exception e){
                    Log.d(TAG, "error adding a file to a post");
                }


                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);

                    JSONParser jsonParser = new JSONParser();
                    JSONObject object = jsonParser.dhis2HttpRequest(DHIS2Config.BASE_URL+"api/documents.json?paging=false&filter=name:eq:"+sourceFile.getName(),"GET",MainOffence.username,MainOffence.password,null);
                    return object;
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return null;
        }
    };

    /**
     * try to get the 'best' location selected from all providers
     */
    private Location getBestLocation() {
        Location gpslocation = getLocationByProvider(LocationManager.GPS_PROVIDER);
        Location networkLocation =  getLocationByProvider(LocationManager.NETWORK_PROVIDER);
        // if we have only one location available, the choice is easy
        if (gpslocation == null) {
            Log.d(TAG, "No GPS Location available.");
            return networkLocation;
        }
        if (networkLocation == null) {
            Log.d(TAG, "No Network Location available");
            return gpslocation;
        }
        // a locationupdate is considered 'old' if its older than the configured
        // update interval. this means, we didn't get a
        // update from this provider since the last check
        long old = System.currentTimeMillis() - 1*60*60*1000;
        boolean gpsIsOld = (gpslocation.getTime() < old);
        boolean networkIsOld = (networkLocation.getTime() < old);
        // gps is current and available, gps is better than network
        if (!gpsIsOld) {
            Log.d(TAG, "Returning current GPS Location");
            return gpslocation;
        }
        // gps is old, we can't trust it. use network location
        if (!networkIsOld) {
            Log.d(TAG, "GPS is old, Network is current, returning network");
            return networkLocation;
        }
        // both are old return the newer of those two
        if (gpslocation.getTime() > networkLocation.getTime()) {
            Log.d(TAG, "Both are old, returning gps(newer)");
            return gpslocation;
        } else {
            Log.d(TAG, "Both are old, returning network(newer)");
            return networkLocation;
        }
    }

    /**
     * get the last known location from a specific provider (network/gps)
     */
    private Location getLocationByProvider(String provider) {
        Location location = null;
        LocationManager locationManager = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
        try {
            if (locationManager.isProviderEnabled(provider)) {
                location = locationManager.getLastKnownLocation(provider);
            }
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Cannot acces Provider " + provider);
        }
        return location;
    }

    /**
     * Start listening and recording locations
     */
    public void startRecording() {
        gpsTimer.cancel();
        gpsTimer = new Timer();
        long checkInterval = 5*60*1000;
        long minDistance = 1000;
        // receive updates
        LocationManager locationManager = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
        for (String s : locationManager.getAllProviders()) {
            locationManager.requestLocationUpdates(s, checkInterval,
                    minDistance, new LocationListener() {

                        @Override
                        public void onStatusChanged(String provider,
                                                    int status, Bundle extras) {}

                        @Override
                        public void onProviderEnabled(String provider) {}

                        @Override
                        public void onProviderDisabled(String provider) {}

                        @Override
                        public void onLocationChanged(Location location) {
                            // if this is a gps location, we can use it
                            if (location.getProvider().equals(
                                    LocationManager.GPS_PROVIDER)) {
                                doLocationUpdate(location, true);
                            }
                        }
                    });
        }
        // start the gps receiver thread
        gpsTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                Location location = getBestLocation();
                doLocationUpdate(location, false);
            }
        }, 0, checkInterval);
    }

    /**
     * Performe a location update either by force or due to location or distance change
     * @param l
     * @param force
     */
    public void doLocationUpdate(Location l, boolean force) {
        long minDistance = 1000;
        Log.d(TAG, "update received:" + l);
        if (l == null) {
            Log.d(TAG, "Empty location");
            if (force)
                Toast.makeText(this, "Current location not available",
                        Toast.LENGTH_SHORT).show();
            return;
        }
        if (lastLocation != null) {
            float distance = l.distanceTo(lastLocation);
            Log.d(TAG, "Distance to last: " + distance);
            if (l.distanceTo(lastLocation) < minDistance && !force) {
                Log.d(TAG, "Position didn't change");
                return;
            }
            if (l.getAccuracy() >= lastLocation.getAccuracy()
                    && l.distanceTo(lastLocation) < l.getAccuracy() && !force) {
                Log.d(TAG,
                        "Accuracy got worse and we are still "
                                + "within the accuracy range.. Not updating");
                return;
            }
        }
    }


    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(AccidentTypeclassificationActivity.this, Locale.getDefault());
        String address = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add ="";
            if(obj.getAdminArea()!=null){
                add = add + obj.getAdminArea();
            }
            if (obj.getSubAdminArea()!=null){
                add = add + ", "+obj.getSubAdminArea();
            }
            if (obj.getAddressLine(0)!=null){
                add = add + ", "+ obj.getAddressLine(0);
            }
            address = add;

            Log.v("IGA", "Address" + add);
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e){

        }
        return address;
    }
}
