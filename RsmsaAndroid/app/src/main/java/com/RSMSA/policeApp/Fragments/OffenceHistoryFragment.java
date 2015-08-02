package com.RSMSA.policeApp.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import com.RSMSA.policeApp.JSONParser;
import com.RSMSA.policeApp.MainOffence;
import com.RSMSA.policeApp.OffenceReportForm;
import com.RSMSA.policeApp.PoliceFunction;
import com.RSMSA.policeApp.R;
import com.RSMSA.policeApp.Utils.Functions;
import com.RSMSA.policeApp.iRoadDB.IroadDatabase;
import com.google.zxing.integration.android.IntentIntegrator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
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
    public static String name= "",
            licenceNumber="",
            address="",
            gender="",
            dateOfBirth="",
            vehicleOwnerName="",
            phoneNumber="",scanContent="",drivingClass,plateNumber;

    private String invalidLicence=null;
    private String expiredInsuarance =null;

    //DHIS2 parameters stored for saving events
    private String driverUid;
    private String vehicleUid;
    private static final String startDate="2014-01-01";
    private static final String endDate="2024-01-01";
    private static final String orgUnit="zs9X8YYBOnK";

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
            JSONObject dataObject = new JSONObject();

            DHIS2Modal dhis2Modal = new DHIS2Modal("Driver",null,  MainOffence.username, MainOffence.password);
            String columnDriverLicenseNumber = dhis2Modal.getDataElementByName("Driver License Number").getId();
            String columnFullName = dhis2Modal.getDataElementByName("Full Name").getId();
            String columnPostalAddress = dhis2Modal.getDataElementByName("Postal Address").getId();
            String columnDateOfBirth = dhis2Modal.getDataElementByName("Date of Birth").getId();
            String columnGender = dhis2Modal.getDataElementByName("Gender").getId();
            String columnCurrentLicenseExpiryDate = dhis2Modal.getDataElementByName("Current License Expiry Date").getId();
            String columnPhoneNumber= dhis2Modal.getDataElementByName("Phone Number").getId();
            String columnDrivingClassName= dhis2Modal.getDataElementByName("Driving Class Name").getId();


            String programDriverUid = dhis2Modal.getProgramByName("Driver").getId();

            String driverUrl = DHIS2Config.BASE_URL+"api/analytics/events/query/"+programDriverUid+"/?startDate="+startDate+
                    "&endDate="+endDate+
                    "&dimension=ou:"+orgUnit+
                    "&dimension="+columnFullName+
                    "&dimension="+columnPostalAddress+
                    "&dimension="+columnDateOfBirth+
                    "&dimension="+columnGender+
                    "&dimension="+columnCurrentLicenseExpiryDate+
                    "&dimension="+columnPhoneNumber+
                    "&dimension="+columnDrivingClassName+
                    "&dimension="+columnDriverLicenseNumber+":EQ:"+input_license;

            Log.d(TAG,"analytics driver url = "+driverUrl);
            JSONParser jsonParser = new JSONParser();
            JSONObject receivedDriverObject = jsonParser.dhis2HttpRequest(driverUrl,"GET",MainOffence.username,MainOffence.password,null);
            Log.d(TAG, "received driver object = " + receivedDriverObject.toString());
            try {
                dataObject.put("driver", Functions.generateJson(receivedDriverObject.getJSONArray("headers"), receivedDriverObject.getJSONArray("rows")));
            } catch (JSONException e) {
                e.printStackTrace();
            }







            String columnVehiclePlateNumber = dhis2Modal.getDataElementByName("Vehicle Plate Number").getId();
            String programOffenceEventUid = dhis2Modal.getProgramByName("Offence Event").getId();
            String columnOffenceDate = dhis2Modal.getDataElementByName("Offence Date").getId();
            String columnOffencePlace = dhis2Modal.getDataElementByName("Offence Place").getId();
            String columnOffenceRegistryList = dhis2Modal.getDataElementByName("Offence Registry List").getId();
            String columnProgram_Driver = dhis2Modal.getDataElementByName("Program_Driver").getId();

            try {
                driverUid =dataObject.getJSONArray("driver").getJSONObject(dataObject.getJSONArray("driver").length()-1).getString("Event");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final String offenceEventUrl =  DHIS2Config.BASE_URL+"/api/analytics/events/query/"+programOffenceEventUid+"/?startDate="+startDate+
                    "&endDate="+endDate+
                    "&dimension=ou:"+orgUnit+
                    "&dimension="+columnOffenceDate+
                    "&dimension="+columnOffencePlace+
                    "&dimension="+columnOffenceRegistryList+
                    "&dimension="+columnVehiclePlateNumber+
                    "&dimension="+columnProgram_Driver+":EQ:"+driverUid;

            Log.d(TAG,"analytics offence url = "+offenceEventUrl);

            JSONParser jsonParser2 = new JSONParser();

            JSONObject offenceEventObject = jsonParser2.dhis2HttpRequest(offenceEventUrl,"GET",MainOffence.username,MainOffence.password,null);


            try {
                dataObject.put("Offences",Functions.generateJson(offenceEventObject.getJSONArray("headers"), offenceEventObject.getJSONArray("rows")));
                Log.d(TAG,"received offence object = "+offenceEventObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }




            String programVehicleUid = dhis2Modal.getProgramByName("Vehicle").getId();
            String columnVehicleOwnerName = dhis2Modal.getDataElementByName("Vehicle Owner Name").getId();
            String columnMake = dhis2Modal.getDataElementByName("Make").getId();
            String columnColor = dhis2Modal.getDataElementByName("Color").getId();
            String columnCurrentInsuranceComapanyName = dhis2Modal.getDataElementByName("Current Insurance Comapany Name").getId();
            String columnCurrentInsuranceExpiryDate = dhis2Modal.getDataElementByName("Current Insurance Expiry Date").getId();
            String columnLastInspectonResult = dhis2Modal.getDataElementByName("Last Inspecton Result").getId();
            String receivedPlateNumber= "";
            try {
                receivedPlateNumber = URLEncoder.encode(input_plate_number, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            Log.d(TAG,"Program driver uid = "+programDriverUid);
            Log.d(TAG,"Program vehicle uid = "+programVehicleUid);

            final String vehicleUrl = DHIS2Config.BASE_URL+"api/analytics/events/query/"+programVehicleUid+"/?startDate="+startDate+
                    "&endDate="+endDate+
                    "&dimension=ou:"+orgUnit+
                    "&dimension="+columnVehicleOwnerName+
                    "&dimension="+columnMake+
                    "&dimension="+columnColor+
                    "&dimension="+columnCurrentInsuranceComapanyName+
                    "&dimension="+columnCurrentInsuranceExpiryDate+
                    "&dimension="+columnLastInspectonResult+
                    "&dimension="+columnVehiclePlateNumber+":EQ:"+receivedPlateNumber;

            Log.d(TAG,"analytics vehicle url = "+vehicleUrl);

            JSONParser jsonParser1 = new JSONParser();
            JSONObject receivedVehicleObject = jsonParser1.dhis2HttpRequest(vehicleUrl,"GET",MainOffence.username,MainOffence.password,null);
            Log.d(TAG, "received vehicle object = " + receivedVehicleObject.toString());

            try {
                dataObject.put("vehicle", Functions.generateJson(receivedVehicleObject.getJSONArray("headers"), receivedVehicleObject.getJSONArray("rows")));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return dataObject;

        }

        @Override
        protected void onPostExecute(JSONObject json) {
            JSONObject driverObject = new JSONObject();
            JSONObject vehiclesObject = new JSONObject();
            JSONArray offenceArray = new JSONArray();
            try {
                Log.d(TAG,"Returned DHIS2 JSON = "+json.toString());
                driverObject = json.getJSONArray("driver").getJSONObject(json.getJSONArray("driver").length()-1);
                Log.d(TAG,"vehivle index = "+(json.getJSONArray("vehicle").length()-1));
                vehiclesObject = json.getJSONArray("vehicle").getJSONObject(json.getJSONArray("vehicle").length()-1);
                offenceArray = json.getJSONArray("Offences");
                vehicleUid = vehiclesObject.getString("Event");
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
                try {
                    contentView.findViewById(R.id.drivers_information_layout).setVisibility(View.VISIBLE);
                    name=driverObject.getString("Full Name");
                    licenceNumber = driverObject.getString("Driver License Number");
                    drivingClass = driverObject.getString("Driving Class Name");
                    address = driverObject.getString("Postal Address");
                    gender = driverObject.getString("Gender");
                    dateOfBirth = driverObject.getString("Date of Birth");
                    phoneNumber = driverObject.getString("Phone Number");
                    String driverLicenceExpiryDate=driverObject.getString("Current License Expiry Date");
                    SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyy-MM-dd");
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
                        IroadDatabase db = new IroadDatabase(getActivity().getApplicationContext());
                        Cursor cursor= db.query("SELECT  * FROM " + IroadDatabase.TABLE_OFFENCE_REGISTRY + " WHERE " + IroadDatabase.KEY_NATURE + " = 'Driving without a valid driving license'");
                        cursor.moveToFirst();
                        Log.d(TAG,"Nature = "+cursor.getString(cursor.getColumnIndex(IroadDatabase.KEY_NATURE)));
                        invalidLicence=cursor.getString(cursor.getColumnIndex(IroadDatabase.KEY_ID));
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


                for(int i=0; i<offenceArray.length(); i++){
                    LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    RelativeLayout historyItem = (RelativeLayout)inflater.inflate(R.layout.offence_history_item,null);
                    JSONObject jsonOffenceRow=new JSONObject();
                    String plateNumber="",place="",date="", offenceEvents = "";
                    try {
                        jsonOffenceRow=offenceArray.getJSONObject(i);
                        plateNumber=jsonOffenceRow.getString("Vehicle Plate Number");
                        date  = jsonOffenceRow.getString("Event date");
                        offenceEvents = jsonOffenceRow.getString("Offence Registry List");
                        place = jsonOffenceRow.getString("Offence Place");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    TextView plateNumberTextView=(TextView)historyItem.findViewById(R.id.plate_number);
                    TextView offenceTextView=(TextView)historyItem.findViewById(R.id.offense);
                    TextView placeTextView=(TextView)historyItem.findViewById(R.id.place);
                    TextView dateTextView=(TextView)historyItem.findViewById(R.id.date);
                    plateNumberTextView.setText(plateNumber);
                    offenceTextView.setText(offenceEvents);
                    placeTextView.setText(place);
                    dateTextView.setText(date);
                    offenceList.addView(historyItem);

                }

                try {
                    contentView.findViewById(R.id.vehicle_information_layout).setVisibility(View.VISIBLE);
                    vehicleOwnerName = vehiclesObject.getString("Vehicle Owner Name");
                    ownersNameTexView.setText(vehiclesObject.getString("Vehicle Owner Name"));
                    vehiclesMakeTextView.setText(vehiclesObject.getString("Make"));
                    vehclesColorTextView.setText(vehiclesObject.getString("Color"));
                    vehicleInsuarance.setText(vehiclesObject.getString("Current Insurance Comapany Name"));


                    String insuaranceLicenceExpiryDate=vehiclesObject.getString("Current Insurance Expiry Date");
                    SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = simpleFormatter.parse(insuaranceLicenceExpiryDate);
                    long receivedDate=date.getTime();

                    Log.d(TAG,"received date = "+receivedDate);
                    insuarenceExpiryDate.setText(vehiclesObject.getString("Current Insurance Expiry Date"));
                    Calendar calendar = Calendar.getInstance();
                    Log.d(TAG,"current date = "+calendar.getTimeInMillis());
                    if(calendar.getTimeInMillis()>receivedDate){
                        insuarenceExpiryDate.setTextColor(Color.RED);
                        errorBtn2.setVisibility(View.VISIBLE);
                    }else{
                        licenceExpiryDate.setTextColor(Color.WHITE);
                        errorBtn2.setVisibility(View.GONE);
                    }


                    vehiclesInspection.setText(vehiclesObject.getString("Last Inspecton Result"));

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
