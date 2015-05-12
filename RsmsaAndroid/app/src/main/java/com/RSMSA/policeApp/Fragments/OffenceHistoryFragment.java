package com.RSMSA.policeApp.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.RSMSA.policeApp.Dhis2.DHIS2Config;
import com.RSMSA.policeApp.Dhis2.DHIS2Modal;
import com.RSMSA.policeApp.MainOffence;
import com.RSMSA.policeApp.OffenceReportForm;
import com.RSMSA.policeApp.PoliceFunction;
import com.RSMSA.policeApp.R;
import com.RSMSA.policeApp.Utils.Functions;
import com.google.zxing.integration.android.IntentIntegrator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by Coze on 9/6/2014.
 */
public class OffenceHistoryFragment extends Fragment {
    private static final String TAG = OffenceHistoryFragment.class.getSimpleName();
    private RelativeLayout contentView,inputnformation;
    private ProgressBar progressBar;
    private ScrollView verificationResults;
    private LinearLayout offenceList;
    private TextView insuarenceExpiryDate,licenceExpiryDate,nameTextView,licenseNumberTextView,addressTextView,genderTextView,dateOfBirthTextView,phoneNumberTextView,drivingClassTextVeiw;
    private TextView ownersNameTexView,vehiclesMakeTextView,vehclesColorTextView,vehicleInsuarance,vehiclesInspection;
    private ImageView scanBtn,errorBtn,errorBtn2;
    private Button verifyBtn,reportBtn,paymentBtn;
    private EditText license,plateNumberEditText;
    private String   name= "",
            licenceNumber="",
            address="",
            gender="",
            dateOfBirth="",
            phoneNumber="",scanContent="",drivingClass,plateNumber;

    private String invalidLicence=null;
    private String expiredInsuarance =null;

    //DHIS2 parameters stored for saving events
    private String driverUid;
    private String vehicleUid;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView=(RelativeLayout)inflater.inflate(R.layout.fragment_offence_history,container,false);
        /**
         * get the instances of the buttons at our view
         */

        progressBar = (ProgressBar)contentView.findViewById(R.id.pbar_main);

        inputnformation=(RelativeLayout)contentView.findViewById(R.id.input_information);
        verificationResults=(ScrollView)contentView.findViewById(R.id.verification_results);
        offenceList = (LinearLayout)contentView.findViewById(R.id.history_list);

        nameTextView=(TextView)contentView.findViewById(R.id.name);
        licenseNumberTextView=(TextView)contentView.findViewById(R.id.license_number);
        addressTextView =  (TextView)contentView.findViewById(R.id.address);
        genderTextView = (TextView) contentView.findViewById(R.id.gender);
        dateOfBirthTextView = (TextView) contentView.findViewById(R.id.date_of_birth);
        phoneNumberTextView = (TextView) contentView.findViewById(R.id.phone_number);
        drivingClassTextVeiw=(TextView)contentView.findViewById(R.id.driving_class);
        licenceExpiryDate=(TextView)contentView.findViewById(R.id.licence_expiry_date);
        insuarenceExpiryDate=(TextView)contentView.findViewById(R.id.insuarance_expiry_date);

        ownersNameTexView=(TextView)contentView.findViewById(R.id.owners_name);
        vehiclesMakeTextView=(TextView)contentView.findViewById(R.id.make);
        vehclesColorTextView=(TextView)contentView.findViewById(R.id.color);
        vehicleInsuarance=(TextView)contentView.findViewById(R.id.insuarance);
        vehiclesInspection=(TextView)contentView.findViewById(R.id.inspection);

        vehicleInsuarance=(TextView)contentView.findViewById(R.id.insuarance);
        vehiclesInspection=(TextView)contentView.findViewById(R.id.inspection);

        scanBtn = (ImageView)contentView.findViewById(R.id.scan_button);
        verifyBtn = (Button)contentView.findViewById(R.id.verify);
        verifyBtn.setTypeface(MainOffence.Rosario_Bold);
        errorBtn=(ImageView)contentView.findViewById(R.id.error_icon);
        errorBtn2=(ImageView)contentView.findViewById(R.id.error_icon_2);

        reportBtn = (Button)contentView.findViewById(R.id.report_button);
        reportBtn.setTypeface(MainOffence.Rosario_Bold);

        paymentBtn=(Button)contentView.findViewById(R.id.payment_button);


        license = (EditText)contentView.findViewById(R.id.license_);
        plateNumberEditText=(EditText)contentView.findViewById(R.id.plate_number);
        license.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                //reportBtn.setVisibility(View.VISIBLE);
                return false;
            }
        });


        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "verify button clicked");
                licenceNumber = license.getText().toString();
                plateNumber=plateNumberEditText.getText().toString();

                if(!licenceNumber.equals("") || !plateNumber.equals("") ){
                    verifyBtn.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.bringToFront();
                    NetAsync(view);
                }
                else {
                    Toast toast = Toast.makeText(getActivity(),
                            " Field Empty", Toast.LENGTH_SHORT);
                    toast.show();

                }
            }
        });


        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //inputnformation.setVisibility(View.VISIBLE);
                //verificationResults.setVisibility(View.GONE);
                progressBar.setVisibility(View.INVISIBLE);
                verifyBtn.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getActivity(), OffenceReportForm.class);
                intent.putExtra("name",name);
                intent.putExtra("licence_number",licenceNumber);
                intent.putExtra("plate_number",plateNumber);
                intent.putExtra("invalidLicence",invalidLicence);
                intent.putExtra("expiredInsuarance",expiredInsuarance);
                intent.putExtra("vehicleUid",vehicleUid);
                intent.putExtra("driverUid",driverUid);
                getActivity().startActivity(intent);
            }
        });

        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                if(!licenceNumber.equals(""))
                    bundle.putString("licenceNumber", licenceNumber);
                else
                    bundle.putString("plateNumber", plateNumber);

                PaymentVerifierFragment nextFrag= new PaymentVerifierFragment();
                nextFrag.setArguments(bundle);
                OffenceHistoryFragment.this.getFragmentManager().beginTransaction()
                        .replace(R.id.activityMain_content_frame, nextFrag,null)
                        .addToBackStack(null)
                        .commit();
            }
        });

        /**
         * adding the listener to the button
         */
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(getActivity());
                scanIntegrator.initiateScan();

            }
        });



        return contentView;


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(TAG,"results received");
        com.google.zxing.integration.android.IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if(resultCode == getActivity().RESULT_OK){
            /**
             * getting the scanned licence number to our temporary variable
             */
            scanContent = scanningResult.getContents();


            Log.d(TAG, scanContent+" SCANNED");

            license.setText(scanContent);
        }
        else if(resultCode == getActivity().RESULT_CANCELED){
            Toast toast = Toast.makeText(getActivity(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void NetAsync(View view){
        new NetCheck().execute();
    }
    /**
     * Async Task to check whether internet connection is working
     **/
    private class NetCheck extends AsyncTask< String, String, Boolean>
    {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            verifyBtn.setVisibility(View.INVISIBLE);
        }
        @Override
        protected Boolean doInBackground(String... args){
            /**
             * Gets current device state and checks for working internet connection by trying Google.
             **/
            ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL(DHIS2Config.BASE_URL);
                    HttpURLConnection urlconn = (HttpURLConnection) url.openConnection();
                    urlconn.setConnectTimeout(3000);
                    urlconn.connect();
                    if (urlconn.getResponseCode() == 200) {
                        return true;
                    }
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean th){
            if(th == true){
                new ProcessVerification().execute();
            }
            else{
                progressBar.setVisibility(View.GONE);
                verifyBtn.setVisibility(View.VISIBLE);
                verifyBtn.bringToFront();
                Toast.makeText(getActivity().getApplicationContext(), "Error in Network Connection",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     *To process the data from the offense form
     */
    private class ProcessVerification extends AsyncTask <String, String, JSONObject> {
        String input_license;
        String input_plate_number;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.bringToFront();
            verifyBtn.setVisibility(View.INVISIBLE);
            input_license = license.getText().toString();
            input_plate_number=plateNumberEditText.getText().toString();

        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONArray relations = new JSONArray();
            try {
                JSONObject relation = new JSONObject();
                relation.put("type","ONE_MANY");
                relation.put("name","Offence Event");

                JSONObject relation2 = new JSONObject();
                relation2.put("type","MANY_MANY");
                relation2.put("name","Driving Class");
                relation2.put("pivot","Driver Driving Class");

                relations.put(relation);
                relations.put(relation2);

            } catch (JSONException e) {
                Log.d(TAG,"Error creating relationships = "+e.getMessage());
            }

            DHIS2Modal dhis2Modal = new DHIS2Modal("Driver",relations,  MainOffence.username, MainOffence.password);

            JSONObject where = new JSONObject();
            try {
                where.put("value",input_license);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray drivers = dhis2Modal.getEvent(where);
            int count = drivers.length();
            for(int i=0;i<count;i++){
                JSONArray offenceEvents= null;
                try {
                    offenceEvents = drivers.getJSONObject(i).getJSONArray("Offence Event");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                int counter2 = offenceEvents.length();
                for(int j=0;j<counter2;j++){
                    String id = null;
                    JSONObject OffenceEvent = new JSONObject();
                    try {
                        OffenceEvent = offenceEvents.getJSONObject(j);
                        id = OffenceEvent.getString("id");
                        Log.d(TAG, "Event id = "+id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONArray offenceRelations = new JSONArray();
                    JSONObject offenceRelation = new JSONObject();
                    try {
                        offenceRelation.put("name", "Offence Registry");
                        offenceRelation.put("type","ONE_MANY");
                        offenceRelations.put(offenceRelation);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONObject whereEventId = new JSONObject();
                    try {
                        whereEventId.put("value",id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    DHIS2Modal dhis2ModalOffence = new DHIS2Modal("Offence",offenceRelations, MainOffence.username, MainOffence.password);
                    JSONArray offences=dhis2ModalOffence.getEvent(whereEventId);
                    try {
                        OffenceEvent.put("offences",offences);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            JSONObject vehicleRelation1 = new JSONObject();
            JSONObject vehicleRelation2 = new JSONObject();
            JSONArray vehicleRelations = new JSONArray();
            try {
                vehicleRelation1.put("type","ONE_MANY");
                vehicleRelation1.put("name","Vehicle Inspection");
                vehicleRelation2.put("type","MANY_MANY");
                vehicleRelation2.put("name","Insurance Company");
                vehicleRelation2.put("pivot","Vehicle Insurance History");
                vehicleRelations.put(vehicleRelation1);
                vehicleRelations.put(vehicleRelation2);

            } catch (JSONException e) {
                Log.d(TAG,"Error creating relationships = "+e.getMessage());
            }

            DHIS2Modal dhis2ModalVehicle = new DHIS2Modal("Vehicle",vehicleRelations, MainOffence.username, MainOffence.password);

            JSONObject whereVehicle = new JSONObject();
            try {
                whereVehicle.put("value",input_plate_number);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONArray vehicles= dhis2ModalVehicle.getEvent(whereVehicle);


            JSONObject json = new JSONObject();
            try {
                json.put("Driver",drivers);
                json.put("Vehicle",vehicles);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d(TAG,"Returned DHIS2 JSON = "+json.toString());
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            JSONObject driverObject = new JSONObject();
            JSONObject vehiclesObject = new JSONObject();
            try {
                Log.d(TAG,"Returned DHIS2 JSON = "+json.toString());
                driverObject = json.getJSONArray("Driver").getJSONObject(0);
                vehiclesObject = json.getJSONArray("Vehicle").getJSONObject(0);
                driverUid = driverObject.getString("id");
                vehicleUid = vehiclesObject.getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e){
                Toast.makeText(getActivity().getApplicationContext(), "No data received!",
                        Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
                progressBar.bringToFront();
                verifyBtn.setVisibility(View.VISIBLE);
            }
            if(true){
                inputnformation.setVisibility(View.GONE);
                verificationResults.setVisibility(View.VISIBLE);
                JSONArray offences = new JSONArray();
                JSONObject driverPersonInfo = new JSONObject();
                JSONObject driverDrivingClass = new JSONObject();
                try {
                    driverPersonInfo = driverObject.getJSONObject("Program_Person");
                    contentView.findViewById(R.id.drivers_information_layout).setVisibility(View.VISIBLE);
                    name=driverPersonInfo.getString("First Name")+" "+driverPersonInfo.getString("Middle Name")+" "+driverPersonInfo.getString("Last Name");
                    licenceNumber = driverObject.getString("Driver License Number");

                    //TODO uncomment this to add diving classes
                    //driverDrivingClass = driverObject.getJSONArray("Driver Driving Class").getJSONObject(0);
                    //drivingClass = driverDrivingClass.getString("Driving Class Name");
                    address = driverPersonInfo.getString("Postal Address");
                    gender = driverPersonInfo.getString("Gender");
                    dateOfBirth = driverPersonInfo.getString("Date of Birth");
                    phoneNumber = driverPersonInfo.getString("Phone Number");
                    String driverLicenceExpiryDate=driverObject.getString("Current License Expiry Date").substring(0,10);
                    SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyy-mm-dd");
                    Date date = simpleFormatter.parse(driverLicenceExpiryDate);
                    long receivedDate=date.getTime();

                    Log.d(TAG,"received date = "+receivedDate);
                    licenceExpiryDate.setText(driverLicenceExpiryDate);
                    Calendar calendar = Calendar.getInstance();
                    Log.d(TAG,"current date = "+calendar.getTimeInMillis());
                    if(calendar.getTimeInMillis()>receivedDate){
                        licenceExpiryDate.setTextColor(Color.RED);
                        errorBtn.setVisibility(View.VISIBLE);
                        //TODO query the valid id of invalid license offence
                        invalidLicence=4+"";
                    }else{
                        licenceExpiryDate.setTextColor(Color.WHITE);
                        errorBtn.setVisibility(View.GONE);
                    }
                    Log.d(TAG,"expiry date = "+driverLicenceExpiryDate);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                nameTextView.setText(name);
                licenseNumberTextView.setText(licenceNumber);
                addressTextView.setText(address);
                genderTextView.setText(gender);
                dateOfBirthTextView.setText(dateOfBirth);
                phoneNumberTextView.setText(phoneNumber);
                drivingClassTextVeiw.setText(drivingClass);

                try{
                    offences=driverObject.getJSONArray("Offence Event");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for(int i=0; i<offences.length(); i++){
                    LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    RelativeLayout historyItem = (RelativeLayout)inflater.inflate(R.layout.offence_history_item,null);
                    JSONObject jsonObjet=new JSONObject();
                    String plateNumber="",place="",date="";
                    JSONArray offenceEvents = new JSONArray();
                    try {
                        jsonObjet=offences.getJSONObject(i);
                        plateNumber=jsonObjet.getJSONObject("Program_Vehicle").getString("Vehicle Plate Number");
                        date  = jsonObjet.getString("Offence Date");
                        offenceEvents = jsonObjet.getJSONArray("offences");
                        place = jsonObjet.getString("Offence Place");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    TextView plateNumberTextView=(TextView)historyItem.findViewById(R.id.plate_number);
                    TextView offenceTextView=(TextView)historyItem.findViewById(R.id.offense);
                    TextView placeTextView=(TextView)historyItem.findViewById(R.id.place);
                    TextView dateTextView=(TextView)historyItem.findViewById(R.id.date);
                    String events="";
                    int counter=offenceEvents.length();
                    for(int y=0;y<counter;y++){
                        try {
                            JSONObject eventObject = offenceEvents.getJSONObject(y).getJSONObject("Program_Offence_Registry");
                            if(y!=counter-1)
                                events+=eventObject.getString("Nature")+" | ";
                            else
                                events+=eventObject.getString("Nature");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    plateNumberTextView.setText(plateNumber);
                    offenceTextView.setText(events);
                    placeTextView.setText(place);
                    dateTextView.setText(date);
                    offenceList.addView(historyItem);

                }

                try {
                    contentView.findViewById(R.id.vehicle_information_layout).setVisibility(View.VISIBLE);
                    Log.d(TAG, "vehicles object = " + vehiclesObject.toString());

                    ownersNameTexView.setText(vehiclesObject.getString("Vehicle Owner Name"));
                    vehiclesMakeTextView.setText(vehiclesObject.getString("Make"));
                    vehclesColorTextView.setText(vehiclesObject.getString("Color"));
                    //TODO fix insuarance
//                    JSONObject insuaranceObject = vehiclesObject.getJSONArray("Vehicle Insurance History").getJSONObject(0);
//                    JSONObject insuarance=vehiclesObject.getJSONObject("insurance");
//                    JSONObject inspection=vehiclesObject.getJSONObject("inspection");
//
//                    JSONObject company=insuarance.getJSONObject("company");
//
//                    vehicleInsuarance.setText(company.getString("company_name"));
//
//                    int pass=Integer.parseInt(inspection.getString("pass"));
//
//                    int fail=Integer.parseInt(inspection.getString("fail"));
//                    String percent=inspection.getString("parcent");
//
//                    vehiclesInspection.setText(pass+"/"+(pass+fail)+" | PASS = ("+percent+"%)");
//
//                    final String insuaranceExpiryDate=insuarance.getString("end_date").substring(0,10);
//                    SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyy-mm-dd");
//                    Date date = simpleFormatter.parse(insuaranceExpiryDate);
//                    long receivedDate=date.getTime();
//
//                    insuarenceExpiryDate.setText(insuaranceExpiryDate);
//                    Calendar calendar = Calendar.getInstance();
//
//                    long currentTime=calendar.getTimeInMillis();
//
//                    Log.d(TAG,"DATE.. = "+date.toString());
//                    Log.d(TAG,"received time.. = "+receivedDate);
//                    Log.d(TAG,"current time.. = "+currentTime);
//                    if(currentTime>receivedDate){
//                        insuarenceExpiryDate.setTextColor(Color.RED);
//                        errorBtn2.setVisibility(View.VISIBLE);
//                        expiredInsuarance =5+"";
//                    }else{
//                        insuarenceExpiryDate.setTextColor(Color.WHITE);
//                        errorBtn2.setVisibility(View.GONE);
//                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }else{
                Toast.makeText(getActivity().getApplicationContext(), "Incorrect Plate Number or Plate Number",
                        Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
                progressBar.bringToFront();
                verifyBtn.setVisibility(View.VISIBLE);
            }


        }
    }
}
