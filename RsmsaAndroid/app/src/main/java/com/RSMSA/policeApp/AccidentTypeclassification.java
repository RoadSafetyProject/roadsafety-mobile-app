package com.RSMSA.policeApp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
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


import com.RSMSA.policeApp.Models.AccidentVehicle;
import com.RSMSA.policeApp.Models.PassengerVehicle;
import com.RSMSA.policeApp.Models.Witness;
import com.RSMSA.policeApp.Utils.AndroidMultiPartEntity;
import com.RSMSA.policeApp.Utils.SystemBarTintManager;
import com.gc.materialdesign.views.ProgressBarDetermininate;

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
import java.util.List;

import adapters.ViewPagerAccidentsDetailsAdapter;
import adapters.ViewPagerWitnessesAdapter;
import customViews.TouchFeedbackEnabledRelativeLayout;


/**
 *  Created by isaiah on 10/22/2014.
 */
public class AccidentTypeclassification extends ActionBarActivity {
    private static final String TAG=AccidentTypeclassification.class.getSimpleName();

    /**
     * JSON Response node names.
     */
    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";

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
    public String positionSelected = "-1";
    private LinearLayout driversSignaturesLayouts, witnessSignatureLayouts;

    /**
     *
     * Accident type classification spinner
     */
    public Spinner atcSpinner, junctionStructureSpinner, junctionControlSpinner, roadTypeSpinner, surfaceTypeSpinner, roadStructureSpinner,
            surfaceStatusSpinner, roadSurfaceSpinner, lightSpinner, whetherSpinner, controlSpinner;

    /**
     * Spinner Adapter declaration
     */
    public SpinnerAdapter dataAdapter;

    public ImageView scroller, policeImage, driverAImage, driverBImage, witnessImage;

    public TextView policeHint, driverAHint, driverBHint, witnessHint, sketchHint, sthinSelected;

    public static int currentSignature;

    public static final int SIGNATURE_ACTIVITY = 2;

    private Toolbar toolbar;

    private String imagePath=null, videoPath=null;

    private boolean isImage=true,isDriver=true;


    long totalSize = 0;

    private JSONObject postAccident;

    private String filePath;
    private ProgressBar progressBarDetermininate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accident_type_classification);

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
                Intent intent = new Intent(AccidentTypeclassification.this, CaptureSignature.class);
                currentSignature = POLICE_SIGNATURE;
                startActivityForResult(intent, SIGNATURE_ACTIVITY);

            }
        });

        TouchFeedbackEnabledRelativeLayout descriptionDrawing = (TouchFeedbackEnabledRelativeLayout)findViewById(R.id.acc_description);
        descriptionDrawing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccidentTypeclassification.this, CaptureSignature.class);
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
                    Intent intent = new Intent(AccidentTypeclassification.this, CaptureSignature.class);
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
                    Intent intent = new Intent(AccidentTypeclassification.this, CaptureSignature.class);
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
        list.add("Single vehicle accident");
        list.add("Accidents between vehicles driving same travel direction (2 or more vehicles)");
        list.add("Accidents between vehicles driving opposite travel direction (2 or more vehicles)");
        list.add("Accidents at a junction turning in same or opposite direction (2 or more vehi.)");
        list.add("Collision at a junction between two or more participants");
        list.add("Accident w. parked vehicles");
        list.add("Pedestrian, animals and other accidents");

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
        weather.add("Clear");
        weather.add("Cloudy");
        weather.add("Storm");
        weather.add("Fog");

        final List<String> control = new ArrayList<String>();
        control.add("Trafic Signal");
        control.add("No Trafic Signal");
        control.add("Lane Marking");
        control.add("Speed Limit/Sign");



        final List<String> violation = new ArrayList<String>();
        violation.add("Overspeed");
        violation.add("Overload");
        violation.add("Distance Keeping");
        violation.add("White Lane Cross");
        violation.add("Red Light");
        violation.add("Over Taking");
        violation.add("Wrong Direction");
        violation.add("Drink and Drive");
        violation.add("Careless Pedestrian");
        violation.add("Unsecure Load");
        violation.add("Zebra Crossing");
        violation.add("Others");

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
//                Log.d(TAG,"video path = "+videoPath);
//                Log.d(TAG,"image path = "+imagePath);
//                textButton.setVisibility(View.GONE);
                  UploadFileToServer uploadFileToServer = new UploadFileToServer();
                  uploadFileToServer.execute();


                postAccident = new JSONObject();
                for(int i=0;i< ViewPagerAccidentsDetailsAdapter.accident.size();i++){
                    AccidentVehicle accidentVehicle=ViewPagerAccidentsDetailsAdapter.accident.get(i);
                    List<PassengerVehicle> passengerVehicle=ViewPagerAccidentsDetailsAdapter.passanger.get(i);
                    JSONArray passengersJsonArray=new JSONArray();
                    for(int p=0;p<passengerVehicle.size();p++){
                        try {
                            passengersJsonArray.put(p, passengerVehicle.get(p).getjson(passengerVehicle.get(p)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    JSONObject vehicleJson = new JSONObject();
                    try {
                        vehicleJson.put("vehicleDetails", accidentVehicle.getjson(accidentVehicle));
                        vehicleJson.put("passengers", passengersJsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        AccidentReportFormActivity.vehicles.put(i, vehicleJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                for(int i=0;i< ViewPagerWitnessesAdapter.witnesses.size();i++){
                    Witness witness=ViewPagerWitnessesAdapter.witnesses.get(i);
                    try {
                        AccidentReportFormActivity.witnesses.put(i, witness.getjson(witness));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                try {
                    postAccident.put("accident",AccidentReportFormActivity.accident.getjson(AccidentReportFormActivity.accident));
                    postAccident.put("vehicles",AccidentReportFormActivity.vehicles);
                    postAccident.put("witnesses",AccidentReportFormActivity.witnesses);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final PoliceFunction policeFunction= new PoliceFunction();
                new AsyncTask<Void,Void,Void>(){

                    @Override
                    protected Void doInBackground(Void... params) {
                        policeFunction.registerAccident(postAccident);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        Toast.makeText(getApplicationContext(),
                                "DATA SENT", Toast.LENGTH_LONG).show();
                    }
                }.execute();

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
            ViewPagerAccidentsDetailsAdapter.accident.get(i).setViolations(violation.get(0));
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
                Intent atcselectintent = new Intent(AccidentTypeclassification.this, AtcSelect.class);
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

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {

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
        protected void onPostExecute(String s) {
            finishButton.setVisibility(View.VISIBLE);
            progressBarDetermininate.setVisibility(View.GONE);
            Log.e(TAG, "Response from server: " + s);
            Toast.makeText(getApplicationContext(),
                    "Files uploaded successfully", Toast.LENGTH_LONG).show();
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBarDetermininate.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBarDetermininate.setProgress(progress[0]);

            // updating percentage value
            //txtPercentage.setText(String.valueOf(progress[0]) + "%");
            super.onProgressUpdate(progress);
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }
        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(PoliceFunction.sendMediaApi);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {
                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                final int numberOfDrivers=ViewPagerAccidentsDetailsAdapter.accident.size();
                final int numberOfWitnesses=ViewPagerWitnessesAdapter.witnesses.size();

                int status=4+numberOfDrivers+numberOfWitnesses;



                //TODO obtain both video path and imagePath from Accident object
                try{
                    File sourceFile = new File(videoPath);
                    entity.addPart("file1", new FileBody(sourceFile));
                }catch (Exception e){}

                try{
                    File sourceFile = new File(imagePath);
                    entity.addPart("file2", new FileBody(sourceFile));
                }catch (Exception e){}

                try{
                    File sourceFile = new File(AccidentReportFormActivity.accident.getPolice_signatureFilePath());
                    entity.addPart("file3", new FileBody(sourceFile));
                }catch (Exception e){}

                try{
                    File sourceFile = new File(AccidentReportFormActivity.accident.getDescriptionFilePath());
                    entity.addPart("file4", new FileBody(sourceFile));
                }catch (Exception e){}


                for(int i=5;i<=status-numberOfDrivers;i++){
                    try{
                        File sourceFile = new File(ViewPagerAccidentsDetailsAdapter.accident.get(i-5).getSignatureFilePath());
                        entity.addPart("file"+i, new FileBody(sourceFile));
                    }catch (Exception e){}
                }


                for(int i=status-numberOfWitnesses+1;i<=status;i++){
                    try{
                        File sourceFile = new File(ViewPagerWitnessesAdapter.witnesses.get(i-status+numberOfWitnesses-1).getSignatureFilePath());
                        entity.addPart("file"+i, new FileBody(sourceFile));
                    }catch (Exception e){}
                }

                entity.addPart("status", new StringBody(status+""));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }
    }
}
