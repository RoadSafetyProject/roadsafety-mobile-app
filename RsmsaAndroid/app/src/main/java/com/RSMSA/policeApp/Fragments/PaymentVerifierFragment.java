package com.RSMSA.policeApp.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.RSMSA.policeApp.Dhis2.DHIS2Config;
import com.RSMSA.policeApp.Dhis2.DHIS2Modal;
import com.RSMSA.policeApp.JSONParser;
import com.RSMSA.policeApp.MainOffence;
import com.RSMSA.policeApp.Models.Offence;
import com.RSMSA.policeApp.Dialogues.PaymentConfirmationDialogue;
import com.RSMSA.policeApp.PoliceFunction;
import com.RSMSA.policeApp.R;
import com.RSMSA.policeApp.Utils.Functions;
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
public class PaymentVerifierFragment extends Fragment {
    private static final String TAG = "PaymentVerifierFragment";
    private RelativeLayout contentView,inputnformation;
    private ProgressBar progressBar;
    private ScrollView verificationResults;
    private LinearLayout driversOffenceList,vehiclesOffenceList,vehiclesDetails,driversDetails;
    private TextView nameTextView,licenseNumberTextView,addressTextView,genderTextView,dateOfBirthTextView,
            phoneNumberTextView,makeTextView,plateNumberTextView,typeTextView,colorTextView;
    private ImageView scanBtn;
    private Button verifyBtn;
    private EditText licenseEdittext;
    private String   name= "",
            licenceNumber="",
            address="",
            gender="",
            dateOfBirth="",
            phoneNumber="",scanContent="",make="",type="",vehicle_owner="",mColor="",plateNumber="";
    private boolean driversPaymentVerification=true;


    //DHIS2 parameters stored for saving events
    private String driverUid;
    private String vehicleUid;
    private static final String startDate="0001-01-01";
    private static final String endDate="2024-01-01";
    private static final String orgUnit="zs9X8YYBOnK";

    private String receivedNumber="";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView=(RelativeLayout)inflater.inflate(R.layout.fragment_payment_verifier,container,false);
        /**
         * get the instances of the buttons at our view
         */

        progressBar = (ProgressBar)contentView.findViewById(R.id.pbar_main);

        inputnformation=(RelativeLayout)contentView.findViewById(R.id.input_information);
        verificationResults=(ScrollView)contentView.findViewById(R.id.verification_results);
        driversOffenceList = (LinearLayout)contentView.findViewById(R.id.history_list_driver);
        vehiclesOffenceList= (LinearLayout)contentView.findViewById(R.id.history_list_car);
        vehiclesDetails= (LinearLayout)contentView.findViewById(R.id.vehicles_details);
        driversDetails= (LinearLayout)contentView.findViewById(R.id.drivers_details);

        nameTextView=(TextView)contentView.findViewById(R.id.name);
        licenseNumberTextView=(TextView)contentView.findViewById(R.id.license_number);
        addressTextView =  (TextView)contentView.findViewById(R.id.address);
        genderTextView = (TextView) contentView.findViewById(R.id.gender);
        dateOfBirthTextView = (TextView) contentView.findViewById(R.id.date_of_birth);
        phoneNumberTextView = (TextView) contentView.findViewById(R.id.phone_number);
        makeTextView=(TextView)contentView.findViewById(R.id.make);

        plateNumberTextView=(TextView)contentView.findViewById(R.id.plate_number);
        typeTextView=(TextView)contentView.findViewById(R.id.type);
        colorTextView=(TextView)contentView.findViewById(R.id.color);


        scanBtn = (ImageView)contentView.findViewById(R.id.scan_button);
        verifyBtn = (Button)contentView.findViewById(R.id.verify);
        verifyBtn.setTypeface(MainOffence.Rosario_Bold);

        licenseEdittext = (EditText)contentView.findViewById(R.id.license_);


        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MainOffense", "verify button clicked");
                licenceNumber = licenseEdittext.getText().toString();

                if(licenceNumber.equals("")){
                    Toast toast = Toast.makeText(getActivity(),
                            " Field Empty!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    verifyBtn.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.bringToFront();
                    NetAsync(view);

                }
            }
        });

        final RadioButton licence = (RadioButton)contentView.findViewById(R.id.licence);
        licence.setTypeface(MainOffence.Roboto_Regular);
        final RadioButton plate = (RadioButton)contentView.findViewById(R.id.plate);
        plate.setTypeface(MainOffence.Roboto_Regular);

        licence.setChecked(true);

        licence.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b == true){
                    driversPaymentVerification=true;
                    licenseEdittext.setHint("Enter Licence No");
                }
            }
        });

        plate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b == true){
                    driversPaymentVerification=false;
                    licenseEdittext.setHint("Enter Plate No");
                }
            }
        });



        try {
            receivedNumber = getArguments().getString("licenceNumber");
        }catch (Exception e){}

        if(receivedNumber!=null){
            verifyLicenceNumber(receivedNumber);
        }else {
            try {
                receivedNumber = getArguments().getString("plateNumber");
            } catch (Exception e) {}

            if (!receivedNumber.equals("") || receivedNumber != null) {
                driversPaymentVerification=false;
                plate.setChecked(true);
                licence.setChecked(false);
                verifyLicenceNumber(receivedNumber);
            }
        }


        return contentView;


    }

    public void verifyLicenceNumber(String driversLicence){
        Log.d(TAG," drivers licence received == "+driversLicence);
        licenseEdittext.setText(driversLicence+"");
        licenceNumber =driversLicence;

        if(licenceNumber.equals("")){
            Toast toast = Toast.makeText(getActivity(),
                    " Field Empty!", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            verifyBtn.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.bringToFront();
            NetAsync(null);

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //retrieve scan result
        com.google.zxing.integration.android.IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if(resultCode == getActivity().RESULT_OK){
            /**
             * getting the scanned licence number to our temporary variable
             */
            scanContent = scanningResult.getContents();


            Log.d(TAG, scanContent+" SCANNED");

            licenseEdittext.setText(scanContent);
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


    public void onComplete(boolean saved, int i) {
        try {
            SwitchCompat compat = (SwitchCompat) driversOffenceList.getChildAt(i).findViewById(R.id.pay);
            compat.setChecked(saved);
            if (saved) {
                driversOffenceList.getChildAt(i).findViewById(R.id.pay).setVisibility(View.GONE);
            }
        }catch (NullPointerException e){
            SwitchCompat compat = (SwitchCompat) vehiclesOffenceList.getChildAt(i).findViewById(R.id.pay);
            compat.setChecked(saved);
            if (saved) {
                vehiclesOffenceList.getChildAt(i).findViewById(R.id.pay).setVisibility(View.GONE);
            }

        }
        Log.d(TAG,"index = "+i);

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
                Toast.makeText(getActivity().getApplicationContext(),"Error in Network Connection",
                        Toast.LENGTH_LONG).show();
            }

        }
    }

    /**
     *To process the data from the offense form
     */
    private class ProcessVerification extends AsyncTask <String, String, JSONObject> {
        String input_license;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.bringToFront();
            verifyBtn.setVisibility(View.INVISIBLE);
            input_license = licenseEdittext.getText().toString();
        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONObject dataObject = new JSONObject();
            DHIS2Modal dhis2Modal = new DHIS2Modal("Driver",null,  MainOffence.username, MainOffence.password);
            if(driversPaymentVerification) {
                String columnDriverLicenseNumber = dhis2Modal.getDataElementByName("Driver License Number").getId();
                String columnFullName = dhis2Modal.getDataElementByName("Full Name").getId();
                String columnPostalAddress = dhis2Modal.getDataElementByName("Postal Address").getId();
                String columnDateOfBirth = dhis2Modal.getDataElementByName("Date of Birth").getId();
                String columnGender = dhis2Modal.getDataElementByName("Gender").getId();
                String columnCurrentLicenseExpiryDate = dhis2Modal.getDataElementByName("Current License Expiry Date").getId();
                String columnPhoneNumber= dhis2Modal.getDataElementByName("Phone Number").getId();
                String columnDrivingClassName= dhis2Modal.getDataElementByName("Driving Class Name").getId();

                String programDriverUid = dhis2Modal.getProgramByName("Driver").getId();

                String driverUrl = "http://roadsafety.udsm.ac.tz/demo/api/analytics/events/query/"+programDriverUid+"/?startDate="+startDate+
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
                String columnOffencePaid = dhis2Modal.getDataElementByName("Offence Paid").getId();
                String columnOffenceRegistryList = dhis2Modal.getDataElementByName("Offence Registry List").getId();
                String columnProgram_Driver = dhis2Modal.getDataElementByName("Program_Driver").getId();
                String columnProgram_Police = dhis2Modal.getDataElementByName("Program_Police").getId();
                String columnProgram_Vehicle = dhis2Modal.getDataElementByName("Program_Vehicle").getId();
                String columnOffenceAdmissionStatus = dhis2Modal.getDataElementByName("Offence Admission Status").getId();
                String columnOffenceOffenceFacts = dhis2Modal.getDataElementByName("Offence Facts").getId();
                String columnOffenceReceiptAmount = dhis2Modal.getDataElementByName("Offence Reciept Amount").getId();
                String columnVehicleOwnerName = dhis2Modal.getDataElementByName("Vehicle Owner Name").getId();

                try {
                    driverUid =dataObject.getJSONArray("driver").getJSONObject(dataObject.getJSONArray("driver").length()-1).getString("Event");
                    Log.d(TAG,"Driver UID = "+driverUid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final String offenceEventUrl = "http://roadsafety.udsm.ac.tz/demo/api/analytics/events/query/"+programOffenceEventUid+"/?startDate="+startDate+
                        "&endDate="+endDate+
                        "&dimension=ou:"+orgUnit+
                        "&dimension="+columnOffenceDate+
                        "&dimension="+columnOffencePlace+
                        "&dimension="+columnOffenceRegistryList+
                        "&dimension="+columnVehiclePlateNumber+
                        "&dimension="+columnProgram_Police+
                        "&dimension="+columnProgram_Vehicle+
                        "&dimension="+columnOffenceAdmissionStatus+
                        "&dimension="+columnOffenceOffenceFacts+
                        "&dimension="+columnOffenceReceiptAmount+

                        "&dimension="+columnFullName+
                        "&dimension="+columnGender+
                        "&dimension="+columnDriverLicenseNumber+
                        "&dimension="+columnVehicleOwnerName+

                        "&dimension="+columnOffencePaid+":EQ:false"+
                        "&dimension="+columnProgram_Driver+":EQ:"+driverUid;

                Log.d(TAG,"analytics offence url = "+offenceEventUrl);

                JSONParser jsonParser2 = new JSONParser();

                JSONObject offenceEventObject = jsonParser2.dhis2HttpRequest(offenceEventUrl,"GET",MainOffence.username,MainOffence.password,null);


                try {
                    dataObject.put("Offences",Functions.generateJson(offenceEventObject.getJSONArray("headers"), offenceEventObject.getJSONArray("rows")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }




                Log.d(TAG,"Data object = "+dataObject.toString());


            }
            else {
                String programVehicleUid = dhis2Modal.getProgramByName("Vehicle").getId();
                String columnVehicleOwnerName = dhis2Modal.getDataElementByName("Vehicle Owner Name").getId();
                String columnMake = dhis2Modal.getDataElementByName("Make").getId();
                String columnColor = dhis2Modal.getDataElementByName("Color").getId();
                String columnCurrentInsuranceComapanyName = dhis2Modal.getDataElementByName("Current Insurance Comapany Name").getId();
                String columnCurrentInsuranceExpiryDate = dhis2Modal.getDataElementByName("Current Insurance Expiry Date").getId();
                String columnLastInspectonResult = dhis2Modal.getDataElementByName("Last Inspecton Result").getId();
                String columnVehiclePlateNumber = dhis2Modal.getDataElementByName("Vehicle Plate Number").getId();
                String receivedPlateNumber= "";
                try {
                    receivedPlateNumber = URLEncoder.encode(licenceNumber, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                final String vehicleUrl = "http://roadsafety.udsm.ac.tz/demo/api/analytics/events/query/"+programVehicleUid+"/?startDate="+startDate+
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


                try {
                    vehicleUid =dataObject.getJSONArray("vehicle").getJSONObject(dataObject.getJSONArray("vehicle").length()-1).getString("Event");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String programOffenceEventUid = dhis2Modal.getProgramByName("Offence Event").getId();
                String columnOffenceDate = dhis2Modal.getDataElementByName("Offence Date").getId();
                String columnOffencePlace = dhis2Modal.getDataElementByName("Offence Place").getId();
                String columnOffencePaid = dhis2Modal.getDataElementByName("Offence Paid").getId();
                String columnOffenceRegistryList = dhis2Modal.getDataElementByName("Offence Registry List").getId();
                String columnProgram_Driver = dhis2Modal.getDataElementByName("Program_Driver").getId();
                String columnProgram_Police = dhis2Modal.getDataElementByName("Program_Police").getId();
                String columnProgram_Vehicle = dhis2Modal.getDataElementByName("Program_Vehicle").getId();
                String columnOffenceAdmissionStatus = dhis2Modal.getDataElementByName("Offence Admission Status").getId();
                String columnOffenceOffenceFacts = dhis2Modal.getDataElementByName("Offence Facts").getId();
                String columnOffenceReceiptAmount = dhis2Modal.getDataElementByName("Offence Reciept Amount").getId();
                String columnGender = dhis2Modal.getDataElementByName("Gender").getId();
                String columnFullName = dhis2Modal.getDataElementByName("Full Name").getId();
                String columnDriverLicenseNumber = dhis2Modal.getDataElementByName("Driver License Number").getId();




                final String offenceEventUrl = "http://roadsafety.udsm.ac.tz/demo/api/analytics/events/query/"+programOffenceEventUid+"/?startDate="+startDate+
                        "&endDate="+endDate+
                        "&dimension=ou:"+orgUnit+
                        "&dimension="+columnOffenceDate+
                        "&dimension="+columnOffencePlace+
                        "&dimension="+columnOffenceRegistryList+
                        "&dimension="+columnVehiclePlateNumber+
                        "&dimension="+columnProgram_Police+
                        "&dimension="+columnOffenceAdmissionStatus+
                        "&dimension="+columnOffenceOffenceFacts+
                        "&dimension="+columnOffenceReceiptAmount+


                        "&dimension="+columnFullName+
                        "&dimension="+columnGender+
                        "&dimension="+columnDriverLicenseNumber+
                        "&dimension="+columnVehicleOwnerName+


                        "&dimension="+columnOffencePaid+":EQ:false"+
                        "&dimension="+columnProgram_Driver+
                        "&dimension="+columnProgram_Vehicle+":EQ:"+vehicleUid;

                Log.d(TAG,"analytics offence url = "+offenceEventUrl);

                JSONParser jsonParser2 = new JSONParser();

                JSONObject offenceEventObject = jsonParser2.dhis2HttpRequest(offenceEventUrl,"GET",MainOffence.username,MainOffence.password,null);


                try {
                    dataObject.put("Offences",Functions.generateJson(offenceEventObject.getJSONArray("headers"), offenceEventObject.getJSONArray("rows")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d(TAG,"Data object = "+dataObject.toString());
            }
            return dataObject;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            /**
             * starting with history
             */
            if(json==null) {
                Toast.makeText(getActivity().getApplicationContext(), "No data received!",
                        Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
                progressBar.bringToFront();
                verifyBtn.setVisibility(View.VISIBLE);
            }else{
                inputnformation.setVisibility(View.GONE);
                verificationResults.setVisibility(View.VISIBLE);
                if(driversPaymentVerification) {
                    driversDetails.setVisibility(View.VISIBLE);
                    vehiclesDetails.setVisibility(View.GONE);
                    JSONArray offences = new JSONArray();
                    JSONObject driverObject = new JSONObject();
                    try {
                        driverObject = json.getJSONArray("driver").getJSONObject(json.getJSONArray("driver").length()-1);
                        name = driverObject.getString("Full Name");
                        licenceNumber = driverObject.getString("Driver License Number");
                        address = driverObject.getString("Postal Address");
                        gender = driverObject.getString("Gender");
                        dateOfBirth = driverObject.getString("Date of Birth");
                        phoneNumber = driverObject.getString("Phone Number");
                        offences = json.getJSONArray("Offences");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e){}

                    nameTextView.setText(name);
                    licenseNumberTextView.setText(licenceNumber);
                    addressTextView.setText(address);
                    genderTextView.setText(gender);
                    dateOfBirthTextView.setText(dateOfBirth);
                    phoneNumberTextView.setText(phoneNumber);
                    for (int i = 0; i < offences.length(); i++) {
                        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        RelativeLayout historyItem = (RelativeLayout) inflater.inflate(R.layout.verify_drivers_offence_payment_item, null);
                        JSONObject jsonObjet = new JSONObject();
                        String vehicle_plate_number="";
                        String driversName = "", offenceName = "",
                                rank_no="",facts="",driver_license_number="",place="",offence_date=""
                                ,offenceId="",program_vehicle="",program_police="",program_driver="",latitude="",longitude="";
                        boolean offencePaid = false,admit = false;
                        try {
                            jsonObjet = offences.getJSONObject(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            offenceId = jsonObjet.getString("Event");
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }

                        driver_license_number = licenceNumber;
                        try {
                            vehicle_plate_number = jsonObjet.getString("Vehicle Plate Number");
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            offence_date = jsonObjet.getString("Offence Date");
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            program_driver = jsonObjet.getString("Program_Driver");
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            program_police = jsonObjet.getString("Program_Police");
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            program_vehicle = jsonObjet.getString("Program_Vehicle");
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            admit = jsonObjet.getBoolean("Offence Admission Status");
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            place = jsonObjet.getString("Offence Place");
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            latitude = jsonObjet.getString("Latitude");
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            longitude = jsonObjet.getString("Longitude");
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            offencePaid = jsonObjet.getBoolean("Offence Paid");
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try{
                            facts=jsonObjet.getString("Offence Facts");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        final Offence offence=new Offence();
                        offence.setRank_no(rank_no);
                        offence.setFacts(facts);
                        offence.setVehicle_plate_number(vehicle_plate_number);
                        offence.setDriver_license_number(driver_license_number);
                        offence.setOffence_date(offence_date);
                        offence.setPlace(place);
                        offence.setLatitude(latitude);
                        offence.setLongitude(longitude);
                        offence.setPaid(offencePaid);
                        offence.setAdmit(admit);
                        offence.setId(offenceId);
                        offence.setProgram_Driver(program_driver);
                        offence.setProgram_Police(program_police);
                        offence.setProgram_Vehicle(program_vehicle);
                        try {
                            offence.setGender(jsonObjet.getString("Gender"));
                            offence.setVehicle_owner_name(jsonObjet.getString("Vehicle Owner Name"));
                            offence.setOffence_registry_list(jsonObjet.getString("Offence Registry List"));
                            offence.setFull_Name(jsonObjet.getString("Full Name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        try {
                            Log.d(TAG,"Gender = "+jsonObjet.getString("Gender"));
                            Log.d(TAG,"Vehicle Owner Name = "+jsonObjet.getString("Vehicle Owner Name"));
                            Log.d(TAG,"Offence Registry List = "+jsonObjet.getString("Offence Registry List"));
                            Log.d(TAG,"Full Name = "+jsonObjet.getString("Full Name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                        TextView plateNumberTextView = (TextView) historyItem.findViewById(R.id.plate_number);
                        TextView offenceTextView = (TextView) historyItem.findViewById(R.id.offense);
                        TextView dateTextView = (TextView) historyItem.findViewById(R.id.date);
                        String events = null;
                        try {
                            events = jsonObjet.getString("Offence Registry List");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        int cost= 0;
                        try {
                            cost = jsonObjet.getInt("Offence Reciept Amount");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        plateNumberTextView.setText(vehicle_plate_number);
                        offenceTextView.setText(events);
                        TextView costTextView = (TextView) historyItem.findViewById(R.id.cost_driver);
                        costTextView.setText("Tsh "+cost);
                        dateTextView.setText(offence_date);
                        final SwitchCompat pay=(SwitchCompat)historyItem.findViewById(R.id.pay);
                        final int passedCost=cost;
                        final int index=i;
                        pay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    PaymentConfirmationDialogue paymentConfirmationDialogue = new PaymentConfirmationDialogue();
                                    paymentConfirmationDialogue.show(getFragmentManager(), "paymentVerifier");
                                    paymentConfirmationDialogue.setOffence(offence);
                                    paymentConfirmationDialogue.setCost(passedCost);
                                    paymentConfirmationDialogue.setName(name);
                                    paymentConfirmationDialogue.setIndex(index);

                                }
                            }
                        });

                        driversOffenceList.addView(historyItem);

                    }
                }else  {
                    driversDetails.setVisibility(View.GONE);
                    vehiclesDetails.setVisibility(View.VISIBLE);
                    JSONArray offences = new JSONArray();
                    JSONObject vehicleObject = new JSONObject();
                    String vehicle_plate_number="";
                    try {
                        vehicleObject = json.getJSONArray("vehicle").getJSONObject(json.getJSONArray("vehicle").length()-1);
                        offences = json.getJSONArray("Offences");
                        vehicle_plate_number=vehicleObject.getString("Vehicle Plate Number");
                        plateNumberTextView.setText(vehicle_plate_number);
                        makeTextView.setText(vehicleObject.getString("Make"));
                        typeTextView.setText(vehicleObject.getString("Body Type"));
                        colorTextView.setText(vehicleObject.getString("Color"));
                        vehicle_owner = vehicleObject.getString("Vehicle Owner Name");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG,"Offences size"+offences.length());



                    for (int i = 0; i < offences.length(); i++) {
                        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        RelativeLayout historyItem = (RelativeLayout) inflater.inflate(R.layout.verify_vehicles_offence_payment_item, null);
                        TextView driversNameTextView = (TextView) historyItem.findViewById(R.id.name);
                        TextView offenceTextView = (TextView) historyItem.findViewById(R.id.offense);
                        TextView totalCostTextView = (TextView) historyItem.findViewById(R.id.cost);
                        TextView dateTextView = (TextView) historyItem.findViewById(R.id.date);
                        JSONObject jsonObjet = new JSONObject();
                        String  offenceName = "",
                                rank_no="",facts="",driver_license_number="",place="",offence_date=""
                                ,offenceId="",program_vehicle="",program_police="",program_driver="",latitude="",longitude="";
                        boolean admit=false;
                        try {
                            jsonObjet = offences.getJSONObject(i);
                            driver_license_number=jsonObjet.getString("Driver License Number");
                            offence_date=jsonObjet.getString("Offence Date");
                            place=jsonObjet.getString("Offence Place");
                            program_driver=jsonObjet.getString("Program_Driver");
                            program_police=jsonObjet.getString("Program_Police");
                            program_vehicle=jsonObjet.getString("Program_Vehicle");
                            latitude=jsonObjet.getString("Latitude");
                            longitude=jsonObjet.getString("Longitude");
                            name=jsonObjet.getString("Full Name");
                            driversNameTextView.setText(name);
                            dateTextView.setText(offence_date);
                            admit = jsonObjet.getBoolean("Offence Admission Status");
                            facts=jsonObjet.getString("Offence Facts");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        final Offence offence=new Offence();
                        offence.setVehicle_plate_number(vehicle_plate_number);
                        offence.setDriver_license_number(driver_license_number);
                        offence.setOffence_date(offence_date);
                        offence.setPlace(place);
                        offence.setLatitude(latitude);
                        offence.setLongitude(longitude);
                        offence.setId(offenceId);
                        offence.setAdmit(admit);
                        offence.setFacts(facts);
                        offence.setProgram_Driver(program_driver);
                        offence.setProgram_Police(program_police);
                        offence.setProgram_Vehicle(program_vehicle);
                        try {
                            offence.setGender(jsonObjet.getString("Gender"));
                            offence.setVehicle_owner_name(jsonObjet.getString("Vehicle Owner Name"));
                            offence.setOffence_registry_list(jsonObjet.getString("Offence Registry List"));
                            offence.setFull_Name(jsonObjet.getString("Full Name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String events = null;
                        try {
                            events = jsonObjet.getString("Offence Registry List");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        int cost= 0;
                        try {
                            cost = jsonObjet.getInt("Offence Reciept Amount");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        offenceTextView.setText(events);
                        totalCostTextView.setText("Tsh "+cost);

                        final SwitchCompat pay=(SwitchCompat)historyItem.findViewById(R.id.pay);
                        final int passedCost=cost;
                        final int index=i;
                        pay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    PaymentConfirmationDialogue paymentConfirmationDialogue = new PaymentConfirmationDialogue();
                                    paymentConfirmationDialogue.show(getFragmentManager(), "tester");
                                    paymentConfirmationDialogue.setOffence(offence);
                                    paymentConfirmationDialogue.setCost(passedCost);
                                    paymentConfirmationDialogue.setName(name);
                                    paymentConfirmationDialogue.setIndex(index);

                                }
                            }
                        });


                        vehiclesOffenceList.addView(historyItem);
                    }
                }


            }


        }
    }
}
