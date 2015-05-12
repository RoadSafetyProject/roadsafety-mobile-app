package com.RSMSA.policeApp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.RSMSA.policeApp.Dhis2.DHIS2Config;
import com.RSMSA.policeApp.Dhis2.DHIS2Modal;
import com.RSMSA.policeApp.Models.Offence;
import com.RSMSA.policeApp.Models.Receipt;
import com.RSMSA.policeApp.Utils.Functions;
import com.RSMSA.policeApp.Utils.SystemBarTintManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.RSMSA.policeApp.Adapters.SpinnerAdapters.PaymentMethodSpinnerAdapter;
import com.RSMSA.policeApp.iRoadDB.IroadDatabase;

/**
 *  Created by Isaiah on 02/02/2015.
 */
public class OffenceReportForm extends ActionBarActivity{
    public int id;

    public static final String TAG="ReportForm";
    /**
     * Location variables
     */
    private TextView latituteField;
    private TextView longitudeField;
    private LocationManager locationManager;
    private String provider,dLicense,driverUid,vehicleUid;
    public String mLocation;
    public Toolbar toolbar;
    public ArrayList<String> desc = new ArrayList<String>();
    public ArrayList<String> type = new ArrayList<String>();
    public ArrayList<String> uids = new ArrayList<String>();
    public int offenceCount = 0,count = 0;
    boolean backFromChild = false;
    public TextView offense_type_text;
    public final int REPORT_RESULT = 1;
    public TextView license,plateNo,submit,submitText,LocationTitle;
    public String namePassed;
    public RelativeLayout submit_layout, submit_layout1;
    public boolean commit=true;
    public ArrayList<String> offensesToReport = new ArrayList<String>();
    public int amountToReport = 0;
    public ProgressBar progressBar;
    public TextView offencesSelectedTextView,offencesCostTitle,offensesCommittedTextview,ChargesAcceptanceTitle,issuerNameTitle, issuerRankNo,issuerDateTitle,PaymentTitle,paymentMethodTitle;
    public String offencesSelected="",paymentMethod="",plateNumberObtained="";
    public RelativeLayout report,summary;
    public EditText  receiptEditText,plateNumberEdit,licenceNumberEdit;
    public TextView chargesAcceptance,offencesCommittedTitle,inputs;
    private boolean paymentStatus;
    public static final String MyPREF  = "RoadSafetyApp";
    private SharedPreferences sharedpreferences;
    private String invalidLicence=null;
    private String expiredInsuarance=null;

    private double mLat,mLong;
    private Timer gpsTimer = new Timer();
    private Location lastLocation=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_offence);
        sharedpreferences=getSharedPreferences(MyPREF,Context.MODE_PRIVATE);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        ColorDrawable colorDrawable= new ColorDrawable(getResources().getColor(R.color.blue_900));
        tintManager.setTintDrawable(colorDrawable);

        RelativeLayout inputs=(RelativeLayout)findViewById(R.id.inputs);
        plateNumberEdit=(EditText)findViewById(R.id.plate_number_edit_text);
        licenceNumberEdit=(EditText)findViewById(R.id.licence_number_edit_text);



        final Bundle bundle=getIntent().getExtras();
        namePassed = bundle.getString("name");
        dLicense = bundle.getString("licence_number");
        plateNumberObtained=bundle.getString("plate_number");
        driverUid=bundle.getString("driverUid");
        vehicleUid=bundle.getString("vehicleUid");

        try {
            invalidLicence=bundle.getString("invalidLicence");
            expiredInsuarance=bundle.getString("expiredInsuarance");
        }catch (NullPointerException e){}




        if(dLicense.equals("") || dLicense==null){
            licenceNumberEdit.setVisibility(View.VISIBLE);
        }else if(plateNumberObtained.equals("") || plateNumberObtained==null){
            plateNumberEdit.setVisibility(View.VISIBLE);
        }


        submit = (TextView)findViewById(R.id.submit_text);

        plateNo =(TextView)findViewById(R.id.plate_no_);
        chargesAcceptance=(TextView)findViewById(R.id.charges_acceptance);
        chargesAcceptance.setTypeface(MainOffence.Roboto_Regular);

        offensesCommittedTextview=(TextView)findViewById(R.id.offences_committed_title);
        offensesCommittedTextview.setTypeface(MainOffence.Roboto_BoldCondensed);

        ChargesAcceptanceTitle=(TextView)findViewById(R.id.charges_acceptance_title);
        paymentMethodTitle=(TextView)findViewById(R.id.payment_method_title);
        PaymentTitle=(TextView)findViewById(R.id.payment_title);


        ChargesAcceptanceTitle.setTypeface(MainOffence.Roboto_BoldCondensed);
        paymentMethodTitle.setTypeface(MainOffence.Roboto_BoldCondensed);
        PaymentTitle.setTypeface(MainOffence.Roboto_BoldCondensed);


        offencesCostTitle=(TextView)findViewById(R.id.offences_cost_title);
        offencesCostTitle.setTypeface(MainOffence.Roboto_BoldCondensed);

        submitText = (TextView)findViewById(R.id.submit_text);

        license = (TextView)findViewById(R.id.license);
        license.setText(dLicense);

        report = (RelativeLayout)findViewById(R.id.report);
        summary = (RelativeLayout)findViewById(R.id.summary);
        submit_layout = (RelativeLayout)findViewById(R.id.submit_layout);

        submit_layout1 = (RelativeLayout)findViewById(R.id.submit_layout1);
        submit_layout1.setVisibility(View.GONE);


        progressBar = (ProgressBar)findViewById(R.id.pbar_report);

        TextView driverName = (TextView)findViewById(R.id.driver_name);
        driverName.setTypeface(MainOffence.Roboto_BoldCondensed);


        TextView plateNumberTitle = (TextView)findViewById(R.id.plate_no_title_);
        plateNumberTitle.setTypeface(MainOffence.Roboto_BoldCondensed);

        TextView driverLicense = (TextView)findViewById(R.id.driver_license);
        driverLicense.setTypeface(MainOffence.Roboto_BoldCondensed);

        RelativeLayout OffenseType = (RelativeLayout)findViewById(R.id.offense_type);
        OffenseType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OffenceReportForm.this, OffenseListActivity.class);
                OffenceReportForm.this.startActivityForResult(intent, REPORT_RESULT);
            }
        });

        offense_type_text = (TextView)findViewById(R.id.offense_type_text);

        offencesSelectedTextView = (TextView)findViewById(R.id.offence_list);
        offensesCommittedTextview = (TextView)findViewById(R.id.offences_committed);

        TextView name = (TextView)findViewById(R.id.name);
        name.setText(namePassed);

        final RadioButton court = (RadioButton)findViewById(R.id.court);
        court.setTypeface(MainOffence.Roboto_BoldCondensed);
        final RadioButton guilty = (RadioButton)findViewById(R.id.guilty);
        guilty.setTypeface(MainOffence.Roboto_BoldCondensed);

        guilty.setChecked(true);

        court.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b == true){
                    guilty.setChecked(false);
                    commit=false;
                }
            }
        });

        guilty.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b == true){
                    court.setChecked(false);
                    commit=true;
                }
            }
        });



        final String [] paymentMethodsArray=this.getResources().getStringArray(R.array.payment_methods);
        final Spinner paymentMethodSpinner=(Spinner)findViewById(R.id.payment_method_spinner);
        final RadioButton paid = (RadioButton)findViewById(R.id.paid);
        paid.setTypeface(MainOffence.Roboto_Regular);
        final RadioButton not_paid = (RadioButton)findViewById(R.id.not_paid);
        final TextView receipt_title= (TextView)findViewById(R.id.receipt_title);
        receiptEditText = (EditText)findViewById(R.id.receipt);

        receipt_title.setTypeface(MainOffence.Roboto_BoldCondensed);


        not_paid.setTypeface(MainOffence.Roboto_Regular);

        not_paid.setChecked(true);

        paymentMethodSpinner.setBackground(null);

        paid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b == true){
                    paymentStatus=true;
                    paymentMethodTitle.setVisibility(View.VISIBLE);
                    paymentMethodSpinner.setVisibility(View.VISIBLE);
                    paymentMethod=paymentMethodsArray[0];
                    paymentMethodSpinner.setSelection(0);
                    receipt_title.setVisibility(View.VISIBLE);
                    receiptEditText.setVisibility(View.VISIBLE);
                }
            }
        });

        not_paid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b == true){
                    paymentStatus=false;
                    paymentMethodTitle.setVisibility(View.GONE);
                    paymentMethodSpinner.setVisibility(View.GONE);
                    receipt_title.setVisibility(View.GONE);
                    receiptEditText.setVisibility(View.GONE);
                    paymentMethod="";
                    receiptEditText.setText("");

                }
            }
        });

        PaymentMethodSpinnerAdapter adapter = new PaymentMethodSpinnerAdapter(getSupportActionBar().getThemedContext(), R.layout.row_menu,paymentMethodsArray);
        paymentMethodSpinner.setAdapter(adapter);
        paymentMethodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                paymentMethod = paymentMethodsArray[position];
                if (position == 0) {
                    receiptEditText.setVisibility(View.VISIBLE);
                    receipt_title.setVisibility(View.VISIBLE);
                } else {
                    receiptEditText.setVisibility(View.GONE);
                    receipt_title.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
        if (backFromChild){
            backFromChild = false;
            offense_type_text.setText(count+" offenses selected");

            if(count == 0){
                submit_layout1.setVisibility(View.GONE);
                offencesSelectedTextView.setVisibility(View.GONE);
            }
            else{
                submit_layout1.setVisibility(View.VISIBLE);
                offencesSelectedTextView.setText(offencesSelected);
            }

            submit_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (  ( !license.getText().toString().equals("")) && ( !plateNo.getText().toString().equals("")))
                    {
                        NetAsync(view);

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),
                                "One or more fields are empty", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            submit_layout1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!plateNumberObtained.equals("") && !dLicense.equals("")) {
                        plateNo.setText(plateNumberObtained);
                        license.setText(dLicense);

                    }else if(plateNumberObtained.equals("") && !plateNumberEdit.getText().toString().equals("")) {
                        plateNumberObtained=plateNumberEdit.getText().toString();
                        plateNo.setText(plateNumberObtained);
                    } else if(dLicense.equals("") && !licenceNumberEdit.getText().toString().equals("")){
                        dLicense=licenceNumberEdit.getText().toString();
                        license.setText(dLicense);
                    } else{
                        Toast toast = Toast.makeText(OffenceReportForm.this,
                                "Please fill the required field", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }

                    if(commit)
                        chargesAcceptance.setText("Charges Accepted");
                    else{
                        chargesAcceptance.setText("Going to Court");
                    }
                    report.setVisibility(View.GONE);
                    summary.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(OffenceReportForm.this, Locale.getDefault());
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

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        gpsTimer.cancel();
    }

    /**
     * Async Task to check whether internet connection is working
     **/
    private class NetCheck extends AsyncTask< String, String, Boolean>
    {
        //private ProgressDialog nDialog;

        @Override
        protected void onPreExecute(){
            submitText.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(String... args){
            /**
             * Gets current device state and checks for working internet connection by trying Google.
             **/
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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
                submitText.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                Location location =getBestLocation();
                try {
                    mLat = location.getLatitude();
                    mLong = location.getLongitude();
                    new ProcessRegister().execute();
                }catch (NullPointerException e){
                    Functions.displayPromptForEnablingGPS(OffenceReportForm.this);
                }

            }
            else{
                submitText.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                //TODO should store the data in sql lite temporary until there will be network
            }
        }
    }

    /**
     *To process the data from the offense form
     */

    private class ProcessRegister extends AsyncTask <String, String, Boolean> {
        /**
         * Defining Process dialog
         **/
        String input_license,input_plateNumber,input_issuer_no;
        boolean input_commit;
        CharSequence Input_issuer;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            submitText.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            Input_issuer =MainOffence.username;
            input_issuer_no=(String)Input_issuer;
            input_license = license.getText().toString();
            input_plateNumber = plateNo.getText().toString();
            input_commit = commit;
            offenceCount=count;
        }
        @Override
        protected Boolean doInBackground(String... args) {
            PoliceFunction PFunction = new PoliceFunction();




            String place=getAddress(mLat,mLong);
            JSONObject event = new JSONObject();
            Calendar cl = Calendar.getInstance();


            DHIS2Modal  modal = new DHIS2Modal("Offence Event",null, MainOffence.username, MainOffence.password);
            String program = modal.getProgramByName("Offence Event").getId();
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
                event.put("program",program);
                event.put("orgUnit",organizationUnit);
                event.put("eventDate",Functions.getDateFromUnixTimestamp(cl.getTimeInMillis()));
                event.put("coordinate",coordinatesObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONArray dataValues = new JSONArray();
            JSONObject programPoliceDataElement = new JSONObject();
            String programPoliceUid = modal.getDataElementByName("Program_Police").getId();
            try {
                programPoliceDataElement.put("dataElement",programPoliceUid);
                //TODO implement login mechanism and store data in the datatbase
                programPoliceDataElement.put("value","aQylIO5YSxD");

                dataValues.put(programPoliceDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }





            JSONObject programDriverDataElement = new JSONObject();
            String program_DriverUid = modal.getDataElementByName("Program_Driver").getId();
            try {
                programDriverDataElement.put("value",driverUid);
                programDriverDataElement.put("dataElement",program_DriverUid);
                dataValues.put(programDriverDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }



            JSONObject programVehicleDataElement = new JSONObject();
            String programVehicleDataElementUid = modal.getDataElementByName("Program_Vehicle").getId();
            try {
                programVehicleDataElement.put("value",vehicleUid);
                programVehicleDataElement.put("dataElement",programVehicleDataElementUid);
                dataValues.put(programVehicleDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }



            JSONObject offenceFactsDataElement = new JSONObject();
            String offenceFactsUid = modal.getDataElementByName("Offence Facts").getId();
            try {
                //TODO implement an offence facts UI
                offenceFactsDataElement.put("value","");
                offenceFactsDataElement.put("dataElement",offenceFactsUid);
                dataValues.put(offenceFactsDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }



            JSONObject offenceDateDataElement = new JSONObject();
            String offenceDateUid = modal.getDataElementByName("Offence Date").getId();
            try {
                offenceDateDataElement.put("dataElement",offenceDateUid);
                offenceDateDataElement.put("value",Functions.getDateFromUnixTimestamp(cl.getTimeInMillis()));
                dataValues.put(offenceDateDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONObject latitudeDataElement = new JSONObject();
            String latitudeUid = modal.getDataElementByName("Latitude").getId();
            try {
                latitudeDataElement.put("dataElement",latitudeUid);
                latitudeDataElement.put("value",mLat);
                dataValues.put(latitudeDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }



            JSONObject longitudeDataElement = new JSONObject();
            String longitudeUid = modal.getDataElementByName("Longitude").getId();
            try {
                longitudeDataElement.put("dataElement",longitudeUid);
                longitudeDataElement.put("value",mLong);
                dataValues.put(longitudeDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONObject offencePlaceDataElement = new JSONObject();
            String offencePlaceUid = modal.getDataElementByName("Offence Place").getId();
            try {
                offencePlaceDataElement.put("dataElement",offencePlaceUid);
                offencePlaceDataElement.put("value",place);
                dataValues.put(offencePlaceDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONObject offenceAdmissionStatusDataElement = new JSONObject();
            String offenceAdmissionStatusUid = modal.getDataElementByName("Offence Admission Status").getId();
            try {
                offenceAdmissionStatusDataElement.put("dataElement",offenceAdmissionStatusUid);
                offenceAdmissionStatusDataElement.put("value",commit);
                dataValues.put(offenceAdmissionStatusDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }



            JSONObject offencePaidDataElement = new JSONObject();
            String offencePaidDataElementUid = modal.getDataElementByName("Offence Paid").getId();
            try {
                offencePaidDataElement.put("dataElement",offencePaidDataElementUid);
                offencePaidDataElement.put("value",paymentStatus);
                dataValues.put(offencePaidDataElement);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            if(paymentStatus){
                JSONObject offenceRecieptAmountDataElement = new JSONObject();
                String offenceRecieptAmountDataElementUid = modal.getDataElementByName("Offence Reciept Amount").getId();
                try {
                    offenceRecieptAmountDataElement.put("dataElement",offenceRecieptAmountDataElementUid);
                    offenceRecieptAmountDataElement.put("value",amountToReport+"");
                    dataValues.put(offenceRecieptAmountDataElement);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONObject offenceRecieptNumberDataElement = new JSONObject();
                String offenceRecieptNumberDataElementUid = modal.getDataElementByName("Offence Reciept Number").getId();
                try {
                    offenceRecieptNumberDataElement.put("dataElement",offenceRecieptNumberDataElementUid);
                    offenceRecieptNumberDataElement.put("value",receiptEditText.getText().toString());
                    dataValues.put(offenceRecieptNumberDataElement);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }



            try {
                event.put("dataValues",dataValues);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d(TAG,"sent offence event = "+event.toString());
            JSONParser jsonParser = new JSONParser();
            JSONObject resultObject = jsonParser.dhis2HttpRequest(DHIS2Config.BASE_URL+"api/events","POST",MainOffence.username,MainOffence.password,event);
            Log.d(TAG,"received offence event result = "+resultObject.toString());
            try {
                String reference = resultObject.getJSONArray("importSummaries").getJSONObject(0).getString("reference");
                for(int i=0;i<count;i++){
                    DHIS2Modal  offenceModal = new DHIS2Modal("Offence",null, MainOffence.username, MainOffence.password);
                    String OffenceUid = offenceModal.getProgramByName("Offence").getId();
                    try {

                        JSONObject offenceEvent = new JSONObject();
                        offenceEvent.put("program",OffenceUid);
                        offenceEvent.put("orgUnit",organizationUnit);
                        offenceEvent.put("eventDate",Functions.getDateFromUnixTimestamp(cl.getTimeInMillis()));


                        JSONArray offenceDataValues = new JSONArray();

                        JSONObject programOffenceEventDataElement = new JSONObject();
                        String programOffenceEventDataElementUid = modal.getDataElementByName("Program_Offence_Event").getId();
                        try {
                            programOffenceEventDataElement.put("dataElement",programOffenceEventDataElementUid);
                            programOffenceEventDataElement.put("value",reference);
                            offenceDataValues.put(programOffenceEventDataElement);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        JSONObject programOffenceRegistryDataElement = new JSONObject();
                        String programOffenceRegistryDataElementUid = modal.getDataElementByName("Program_Offence_Registry").getId();
                        try {
                            programOffenceRegistryDataElement.put("dataElement",programOffenceRegistryDataElementUid);
                            programOffenceRegistryDataElement.put("value",uids.get(i));
                            offenceDataValues.put(programOffenceRegistryDataElement);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        try {
                            offenceEvent.put("dataValues",offenceDataValues);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JSONObject resultObject2 = jsonParser.dhis2HttpRequest(DHIS2Config.BASE_URL+"api/events","POST",MainOffence.username,MainOffence.password,offenceEvent);
                        Log.d(TAG,"offence Program results = "+resultObject2.toString());


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            /**
             * Checks for success message.
             **/
            if (aBoolean){
                    Toast toast = Toast.makeText(OffenceReportForm.this,
                            "Offence reported successfully", Toast.LENGTH_SHORT);
                    toast.show();
                    finish();
            } else{
                submitText.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                Toast toast = Toast.makeText(OffenceReportForm.this,
                        "Offence reporting failed", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

    }

    public void NetAsync(View view){
        new NetCheck().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REPORT_RESULT) {
            if (resultCode == RESULT_OK) {
                // code for result
                final Bundle bundle = data.getExtras();

                Log.d("Database", "count at parent is "+count);
                desc = bundle.getStringArrayList("desc");
                type = bundle.getStringArrayList("type");
                uids = bundle.getStringArrayList("uids");
                OffenseListActivity.offenseDesc.clear();
                backFromChild = true;


                Log.d(TAG,"number of ids = "+ uids.size());

                try {
                    Log.d(TAG, " test data = " + invalidLicence);
                }catch (Exception e){}
                if(invalidLicence!=null && !invalidLicence.equals("")){
                    String offenceUid = invalidLicence;
                    boolean offenceIncluded=false;
                    for(String i : uids){
                        if(offenceUid.equals(i)){
                            offenceIncluded=true;
                        }
                    }

                    if(!offenceIncluded){
                        Log.d(TAG, " desc sze before = " + desc.size());
                        IroadDatabase db = new IroadDatabase(getApplicationContext());
                        int counter= uids.size();
                        uids.add(offenceUid);
                        desc.add(db.getAnOffenceDetail(false, offenceUid));
                        type.add(db.getAnOffenceDetail(true,offenceUid));
                    }

                }

                //TODO implement the above for expired licence



                count = desc.size();
                int counter = desc.size();
                if(counter>0){
                    offencesSelectedTextView.setVisibility(View.VISIBLE);
                    submit_layout1.setVisibility(View.VISIBLE);
                }else {
                    offencesSelectedTextView.setVisibility(View.GONE);
                    submit_layout1.setVisibility(View.GONE);
                }
                offencesSelected="";
                for (int i = 0; i<counter; i++){
                    if(i==counter-1) {
                        offencesSelected = offencesSelected + desc.get(i);
                    }else{
                        offencesSelected = offencesSelected + desc.get(i) + "\n\n";
                    }
                }
                offencesSelectedTextView.setText(offencesSelected);
                offensesCommittedTextview.setText(offencesSelected);
                amountToReport=0;
                for(int i=0; i<count; i++) {
                    if (type.get(i).equals("30000")) {
                        amountToReport += 30000;
                    }
                    else if (type.get(i).equals("20000")){
                        amountToReport += 20000;
                    }
                }
                Log.d(TAG,"amount to report = "+amountToReport);

                TextView offenceCost=(TextView)findViewById(R.id.costs);
                offenceCost.setTypeface(MainOffence.Roboto_Regular);
                offenceCost.setText(amountToReport+"");

            }
            if (resultCode == RESULT_CANCELED) {
                //TODO Write your code on no result return
            }
        }
    }


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
        long checkInterval = 60*1000;
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


}