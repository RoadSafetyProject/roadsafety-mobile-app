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

import com.RSMSA.policeApp.Adapters.SpinnerAdapters.PaymentMethodSpinnerAdapter;
/**
 *  Created by Isaiah on 02/02/2015.
 */
public class OffenceReportForm extends ActionBarActivity implements LocationListener{

    /**
     *  JSON Response node names.
     **/
    private static String KEY_ERROR = "error";
    private static String KEY_SUCCESS = "status";
    private static String KEY_UID = "id";
    private static String KEY_PLATE_NUMBER = "plateNumber";
    private static String KEY_OFFENSE = "offence";
    private static String KEY_COMMIT = "commit";
    private static String KEY_LICENSE = "license";
    private static String KEY_RANK_NO = "rankNo";
    private static String KEY_AMOUNT = "amount";
    private static String KEY_CREATED_AT = "created_at";
    private static String KEY_LATITUDE = "latitude";
    private static String KEY_LONGITUDE = "longitude";
    public int id;

    public static final String TAG="ReportForm";
    /**
     * Location variables
     */
    private TextView latituteField;
    private TextView longitudeField;
    private LocationManager locationManager;
    private String provider,dLicense;
    public String mLocation;
    public double mLat;
    public double mLong;
    public Toolbar toolbar;
    public ArrayList<String> desc = new ArrayList<String>();
    public ArrayList<String> type = new ArrayList<String>();
    public ArrayList<Integer> ids = new ArrayList<Integer>();
    public int[] offenseTag = new int[10];
    public int offenceCount = 0,count = 0;
    boolean backFromChild = false;
    public TextView offense_type_text;
    public final int REPORT_RESULT = 1;
    public TextView date_text,license,plateNo,submit,submitText,LocationTitle;
    public String namePassed;
    public RelativeLayout submit_layout, submit_layout1;
    public boolean commit=true;
    public ArrayList<String> offensesToReport = new ArrayList<String>();
    public int amountToReport = 0;
    public ProgressBar progressBar;
    public TextView offencesSelectedTextView,offencesCostTitle,offensesCommittedTextview,ChargesAcceptanceTitle,issuerNameTitle, issuerRankNo,issuerDateTitle,PaymentTitle,paymentMethodTitle,locationTitle;
    public String offencesSelected="",paymentMethod="",plateNumberObtained="";
    public RelativeLayout report,summary;
    public EditText  receiptEditText,plateNumberEdit,licenceNumberEdit;
    public TextView chargesAcceptance,offencesCommittedTitle,inputs;
    private boolean paymentStatus;
    public static final String MyPREF  = "RoadSafetyApp";
    private SharedPreferences sharedpreferences;
    private String invalidLicence=null;
    private String expiredInsuarance=null;

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

        try {
            invalidLicence=bundle.getString("invalidLicence");
            expiredInsuarance=bundle.getString("expiredInsuarance");
        }catch (NullPointerException e){}




        if(dLicense.equals("") || dLicense==null){
            licenceNumberEdit.setVisibility(View.VISIBLE);
        }else if(plateNumberObtained.equals("") || plateNumberObtained==null){
            plateNumberEdit.setVisibility(View.VISIBLE);
        }


        /**
         * initializing location variables
         */
        latituteField = (TextView) findViewById(R.id.location);

        /**
         * get the location manager
         */
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        /**
         * define the criteria of how to select the location
         * provider -> use default
         */
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location;
        try{
            location = locationManager.getLastKnownLocation(provider);
        }catch (Exception e){
            provider = locationManager.getBestProvider(criteria,true);
            location = locationManager.getLastKnownLocation(provider);
        }



        /**
         * initialize location fields
         */
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            latituteField.setText("Location error!");
            //longitudeField.setText("Location not available");
        }

        submit = (TextView)findViewById(R.id.submit_text);

        plateNo =(TextView)findViewById(R.id.plate_no_);
        chargesAcceptance=(TextView)findViewById(R.id.charges_acceptance);
        chargesAcceptance.setTypeface(MainOffence.Roboto_Regular);

        offensesCommittedTextview=(TextView)findViewById(R.id.offences_committed_title);
        offensesCommittedTextview.setTypeface(MainOffence.Roboto_BoldCondensed);

        ChargesAcceptanceTitle=(TextView)findViewById(R.id.charges_acceptance_title);

        issuerNameTitle=(TextView)findViewById(R.id.issuer_name);
        issuerRankNo =(TextView)findViewById(R.id.rank_no);
        issuerDateTitle=(TextView)findViewById(R.id.date);
        locationTitle=(TextView)findViewById(R.id.location_title);
        paymentMethodTitle=(TextView)findViewById(R.id.payment_method_title);
        PaymentTitle=(TextView)findViewById(R.id.payment_title);


        ChargesAcceptanceTitle.setTypeface(MainOffence.Roboto_BoldCondensed);
        issuerNameTitle.setTypeface(MainOffence.Roboto_BoldCondensed);
        issuerRankNo.setTypeface(MainOffence.Roboto_BoldCondensed);;
        issuerDateTitle.setTypeface(MainOffence.Roboto_BoldCondensed);;
        locationTitle.setTypeface(MainOffence.Roboto_BoldCondensed);
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

        date_text = (TextView)findViewById(R.id.date_text);

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
                paymentMethod=paymentMethodsArray[position];
                if(position==0){
                    receiptEditText.setVisibility(View.VISIBLE);
                    receipt_title.setVisibility(View.VISIBLE);
                }else{
                    receiptEditText.setVisibility(View.GONE);
                    receipt_title.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TextView policeName = (TextView)findViewById(R.id.issuer_name_text);
        TextView rankNo = (TextView)findViewById(R.id.rank_no_text);

        policeName.setText(sharedpreferences.getString("name",""));
        rankNo.setText(sharedpreferences.getString("rank_no",""));
        Calendar calendar = Calendar.getInstance();
        date_text.setText(Functions.getDateFromUnixTimestamp(calendar.getTimeInMillis()));

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
        locationManager.requestLocationUpdates(provider, 400, 1, this);
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
//            String add =   "Country        : "+ obj.getCountryName();
//            add = add + " , " + obj.getCountryCode();
//            add = add + "\n Admin Area     : " + obj.getAdminArea();
//            add = add + "\n Sub Admin Area : " + obj.getSubAdminArea();
//            add = add + "\n Locality       : " + obj.getLocality();
//            add = add + "\n Address Line   : " + obj.getAddressLine(0);
//            add = add + "\n" + obj.getSubThoroughfare();
//            add = add + "\n" + obj.getPostalCode();

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
        locationManager.removeUpdates(this);
    }


    @Override
    public void onLocationChanged(Location location) {
        double lat = (double) (location.getLatitude());
        double lng = (double) (location.getLongitude());
        mLat = lat;
        mLong = lng;
        mLocation = getAddress(lat, lng);
        latituteField.setText(mLocation);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
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
                    URL url = new URL("http://www.google.com");
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
                new ProcessRegister().execute();
            }
            else{
                submitText.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                //should store the data in sql lite temporary until there will be network
            }
        }
    }

    /**
     *To process the data from the offense form
     */

    private class ProcessRegister extends AsyncTask <String, String, JSONObject> {
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
            //TODO uncomment this line and set the rank number received from login saved into the database
            Input_issuer ="R111";//(CharSequence)User.get("rankNo");
            input_issuer_no=(String)Input_issuer;
            input_license = license.getText().toString();
            input_plateNumber = plateNo.getText().toString();
            input_commit = commit;
            offenceCount=count;
        }
        @Override
        protected JSONObject doInBackground(String... args) {
            PoliceFunction PFunction = new PoliceFunction();
            String locaton=getAddress(mLat,mLong);
            JSONObject json;
            Calendar cl = Calendar.getInstance();

            Offence offence=new Offence();

            offence.setOffence_date(cl.getTimeInMillis());
            offence.setAdmit(input_commit);
            offence.setDriver_license_number(input_license);
            offence.setLatitude(mLat);
            offence.setLongitude(mLong);
            offence.setVehicle_plate_number(input_plateNumber);
            offence.setRank_no(input_issuer_no);
            offence.setFacts("");
            offence.setPaid(paymentStatus);
            offence.setPlace(locaton);


            JSONObject object = offence.getjson(offence);

            Receipt receipt=new Receipt();
            receipt.setReceipt_number(receiptEditText.getText()+"");
            receipt.setAmount(amountToReport+"");
            receipt.setPayment_mode(paymentMethod);
            receipt.setDate(cl.getTimeInMillis());
            JSONObject receiptObject = receipt.getjson(receipt);


            JSONArray events=new JSONArray();
            int count = ids.size();
            for (int i = 0;i<count;i++){
                JSONObject id=new JSONObject();
                try {
                    id.put("id",ids.get(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                events.put(id);
            }


            json = PFunction.registerOffence(object,receiptObject,events);

            Log.d(TAG,"json received from server is = "+json);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            Log.d(TAG,"json received  received = "+json);
            /**
             * Checks for success message.
             **/
            try {
                if (json != null && json.getString("status") != null){
                    String status = json.getString("status");
                    Log.d(TAG,"Status received = "+status);
                    if(status.equals("OK")){
                        Toast toast = Toast.makeText(OffenceReportForm.this,
                                "Offence reported successfully", Toast.LENGTH_SHORT);
                        toast.show();
                        finish();
                    }else{
                        Toast toast = Toast.makeText(OffenceReportForm.this,
                                "Offence reporting failed", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                else{
                    submitText.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
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
                offenseTag = bundle.getIntArray("tag");
                desc = bundle.getStringArrayList("desc");
                type = bundle.getStringArrayList("type");
                ids = bundle.getIntegerArrayList("ids");
                OffenseListActivity.offenseDesc.clear();
                backFromChild = true;


                Log.d(TAG,"number of ids = "+ids.size());

                try {
                    Log.d(TAG, " test data = " + invalidLicence);
                }catch (Exception e){}
                if(invalidLicence!=null && !invalidLicence.equals("")){
                    int offence = Integer.parseInt(invalidLicence);
                    Log.d(TAG, " test data integer = " + offence);
                    boolean offenceIncluded=false;
                    for(int i : ids){
                        if(offence==i){
                            offenceIncluded=true;
                        }
                    }

                    Log.d(TAG, " test data included = " + offenceIncluded);

                    if(!offenceIncluded){

                        Log.d(TAG, " desc sze before = " + desc.size());
                        DatabaseHandlerOffence databaseHandlerOffence=new DatabaseHandlerOffence(this);
                        int counter=ids.size();
                        ids.add(offence);
                        desc.add(databaseHandlerOffence.getAnOffenceNature(false,offence));
                        type.add(databaseHandlerOffence.getAnOffenceNature(true,offence));

                        Log.d(TAG, " desc sze after = " + desc.size());
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
                // Write your code on no result return
            }
        }
    }

}
