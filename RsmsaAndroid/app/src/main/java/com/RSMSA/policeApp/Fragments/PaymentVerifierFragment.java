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
import com.RSMSA.policeApp.MainOffence;
import com.RSMSA.policeApp.Models.Offence;
import com.RSMSA.policeApp.Dialogues.PaymentConfirmationDialogue;
import com.RSMSA.policeApp.PoliceFunction;
import com.RSMSA.policeApp.R;
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




        String receivedNumber="";
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
    private class ProcessVerification extends AsyncTask <String, String, JSONArray> {
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
        protected JSONArray doInBackground(String... args) {
            PoliceFunction PFunction = new PoliceFunction();
            JSONObject json=new JSONObject();
            String url;
            if(driversPaymentVerification) {
                JSONArray relations = new JSONArray();
                try {
                    JSONObject relation = new JSONObject();
                    relation.put("type","ONE_MANY");
                    relation.put("name","Offence Event");
                    relations.put(relation);
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
                    JSONArray list = new JSONArray();
                    for(int j=0;j<counter2;j++){
                        String id = null;
                        JSONObject OffenceEvent = new JSONObject();
                        try {
                            OffenceEvent = offenceEvents.getJSONObject(j);
                            id = OffenceEvent.getString("id");
                            Log.d(TAG, "Offence Event id = "+id);
                            if(OffenceEvent.getBoolean("Offence Paid")){
                                Log.d(TAG," offence paid = "+OffenceEvent.getBoolean("Offence Paid"));
                                Log.d(TAG," offence index = "+j);
                                list.put(j);

                                Log.d(TAG, "continue");
                                continue;
                            };
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

                    int counter= list.length();
                    for (int p=0;p<counter;p++){
                        try {
                            Log.d(TAG,"removing an offence event");
                            //TODO implement this for api 18 and less
                            offenceEvents.remove(list.getInt(p));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }




                Log.d(TAG,"Returned DHIS2 JSON = "+drivers.toString());
                return drivers;

            }
            else {

                JSONObject vehicleRelation1 = new JSONObject();
                JSONArray vehicleRelations = new JSONArray();
                try {
                    vehicleRelation1.put("type","ONE_MANY");
                    vehicleRelation1.put("name","Offence Event");
                    vehicleRelations.put(vehicleRelation1);

                } catch (JSONException e) {
                    Log.d(TAG,"Error creating relationships = "+e.getMessage());
                }

                DHIS2Modal dhis2ModalVehicle = new DHIS2Modal("Vehicle",vehicleRelations, MainOffence.username, MainOffence.password);

                JSONObject whereVehicle = new JSONObject();
                try {
                    whereVehicle.put("value",licenceNumber);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                JSONArray vehicles= dhis2ModalVehicle.getEvent(whereVehicle);
                int count = vehicles.length();
                for(int i=0;i<count;i++){
                    JSONArray offenceEvents= null;
                    try {
                        offenceEvents = vehicles.getJSONObject(i).getJSONArray("Offence Event");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int counter2 = offenceEvents.length();
                    JSONArray list = new JSONArray();
                    for(int j=0;j<counter2;j++){
                        String id = null;
                        JSONObject OffenceEvent = new JSONObject();
                        try {
                            OffenceEvent = offenceEvents.getJSONObject(j);
                            id = OffenceEvent.getString("id");
                            if(OffenceEvent.getBoolean("Offence Paid")){
                                list.put(j);
                                continue;
                            };
                            Log.d(TAG, "Event id = "+id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        Log.d(TAG,"Before offence relations");
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
                        Log.d(TAG,"offence registries = "+offences.toString());
                        try {
                            OffenceEvent.put("offences",offences);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    int counter= list.length();
                    for (int p=0;p<counter;p++){
                        try {

                            Log.d(TAG,"removing an offence event");
                            //TODO implement this for api 18 and less
                            offenceEvents.remove(list.getInt(p));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                Log.d(TAG,"Returned DHIS2 JSON = "+vehicles.toString());
                return vehicles;
            }
        }

        @Override
        protected void onPostExecute(JSONArray json) {
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
                    JSONObject object = new JSONObject();
                    JSONObject driverObject = new JSONObject();
                    try {
                        object = json.getJSONObject(0);
                        driverObject = object.getJSONObject("Program_Person");
                        name = driverObject.getString("First Name") + " " + driverObject.getString("Middle Name")+ " " + driverObject.getString("Last Name");
                        licenceNumber = object.getString("Driver License Number");
                        address = driverObject.getString("Postal Address");
                        gender = driverObject.getString("Gender");
                        dateOfBirth = driverObject.getString("Date of Birth");
                        phoneNumber = driverObject.getString("Phone Number");
                        offences = object.getJSONArray("Offence Event");
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
                        JSONArray offenceEvents = new JSONArray();
                        try {
                            jsonObjet = offences.getJSONObject(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            offenceEvents = jsonObjet.getJSONArray("offences");
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            offenceId = jsonObjet.getString("id");
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }

                        driver_license_number = licenceNumber;
                        try {
                            vehicle_plate_number = jsonObjet.getJSONObject("Program_Vehicle").getString("Vehicle Plate Number");
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            offence_date = jsonObjet.getString("Offence Date");
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            program_driver = jsonObjet.getJSONObject("Program_Driver").getString("id");
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            program_police = jsonObjet.getJSONObject("Program_Police").getString("id");
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            program_vehicle = jsonObjet.getJSONObject("Program_Vehicle").getString("id");
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
                        try {
                            admit = jsonObjet.getBoolean("Offence Admission Status");
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




                        TextView plateNumberTextView = (TextView) historyItem.findViewById(R.id.plate_number);
                        TextView offenceTextView = (TextView) historyItem.findViewById(R.id.offense);
                        TextView dateTextView = (TextView) historyItem.findViewById(R.id.date);
                        String events = "";
                        int cost=0;


                        for (int y = 0; y < offenceEvents.length(); y++) {
                            try {
                                JSONObject eventObject = offenceEvents.getJSONObject(y).getJSONObject("Program_Offence_Registry");
                                events += eventObject.getString("Nature") + " | ";
                                cost+=Integer.parseInt(eventObject.getString("Amount"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }


                        plateNumberTextView.setText(vehicle_plate_number);
                        offenceTextView.setText(events);
                        TextView costTextView = (TextView) historyItem.findViewById(R.id.cost_driver);
                        costTextView.setText("Tsh "+cost);
                        dateTextView.setText(offence_date);
                        final JSONArray eventArray=offenceEvents;
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
                                    paymentConfirmationDialogue.setEvents(eventArray);
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
                        vehicleObject = json.getJSONObject(0);
                        offences = vehicleObject.getJSONArray("Offence Event");
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
                        String driversName = "", offenceName = "",
                                rank_no="",facts="",driver_license_number="",place="",offence_date=""
                                ,offenceId="",program_vehicle="",program_police="",program_driver="",latitude="",longitude="";
                        boolean admit=false;
                        JSONArray offenceEvents = new JSONArray();
                        try {
                            jsonObjet = offences.getJSONObject(i);
                            offenceEvents = jsonObjet.getJSONArray("offences");
                            driver_license_number=jsonObjet.getJSONObject("Program_Driver").getString("Driver License Number");
                            offence_date=jsonObjet.getString("Offence Date");
                            place=jsonObjet.getString("Offence Place");
                            program_driver=jsonObjet.getJSONObject("Program_Driver").getString("id");
                            program_police=jsonObjet.getJSONObject("Program_Police").getString("id");
                            program_vehicle=jsonObjet.getJSONObject("Program_Vehicle").getString("id");
                            latitude=jsonObjet.getString("Latitude");
                            longitude=jsonObjet.getString("Longitude");
                            name=jsonObjet.getJSONObject("Program_Driver").getJSONObject("Program_Person").getString("First Name")
                                    + " " +jsonObjet.getJSONObject("Program_Driver").getJSONObject("Program_Person").getString("Middle Name")
                                    + " " +jsonObjet.getJSONObject("Program_Driver").getJSONObject("Program_Person").getString("Last Name");
                            offenceId=jsonObjet.getString("id");
                            driversNameTextView.setText(driversName);
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

                        String events = "";
                        int cost=0;
                        for (int y = 0; y < offenceEvents.length(); y++) {
                            try {
                                JSONObject ob = offenceEvents.getJSONObject(y).getJSONObject("Program_Offence_Registry");
                                events += ob.getString("Description") + " | ";
                                cost+=Integer.parseInt(ob.getString("Amount"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        offenceTextView.setText(events);
                        totalCostTextView.setText("Tsh "+cost);

                        final JSONArray eventArray=offenceEvents;
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
                                    paymentConfirmationDialogue.setEvents(eventArray);
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
