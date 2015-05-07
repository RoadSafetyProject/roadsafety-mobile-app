package com.RSMSA.policeApp.Adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;

import com.RSMSA.policeApp.AccidentReportFormActivity;
import com.RSMSA.policeApp.Dhis2.DHIS2Modal;
import com.RSMSA.policeApp.MainOffence;
import com.RSMSA.policeApp.Models.AccidentVehicle;
import com.RSMSA.policeApp.Models.PassengerVehicle;
import com.RSMSA.policeApp.R;
import com.RSMSA.policeApp.Utils.CustomeTimeWatcher;
import com.android.datetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Coze on 7/3/13.
 */

public class ViewPagerAccidentsDetailsAdapter extends PagerAdapter {
    private static final String TAG="VIEW PAGER CINEMA HALL ADAPTER";
    private static Context context;
    private LayoutInflater inflater;
    List<String> tabnames;
    public ViewPagerAccidentsDetailsAdapter(Context context,  List<String> tnames) {
        this.context  = context;
        tabnames=tnames;

    }
    public static List<AccidentVehicle> accident=new ArrayList<AccidentVehicle>();
    public static List<List<PassengerVehicle>> passanger=new ArrayList<>();





    //get count of the number of pages
    @Override
    public int getCount() {
        return tabnames.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabnames.get(position);

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ScrollView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        android.widget.ScrollView itemView = (ScrollView)inflater.inflate(R.layout.accident_details, container,false);

        final AccidentVehicle accidentVehicle=new AccidentVehicle();
        Button addPasenger = (Button)itemView.findViewById(R.id.add_witness);
        Button next = (Button)itemView.findViewById(R.id.next);
        final LinearLayout linearLayout=(LinearLayout)itemView.findViewById(R.id.passengers_layout);

        if(position==tabnames.size()-1){
            next.setVisibility(View.VISIBLE);
        }else {
            next.setVisibility(View.GONE);
        }
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccidentReportFormActivity.showWitneses();
            }
        });

        final EditText fatalEdit=(EditText)itemView.findViewById(R.id.fatal_edit);
        fatalEdit.setOnFocusChangeListener(new CustomeTimeWatcher(accidentVehicle,"setFatal"));  

        final EditText simpleEdit=(EditText)itemView.findViewById(R.id.simple_edit);
        simpleEdit.setOnFocusChangeListener(new CustomeTimeWatcher(accidentVehicle,"setSimple"));

        EditText injuryEdit=(EditText)itemView.findViewById(R.id.injury_edit);
        injuryEdit.setOnFocusChangeListener(new CustomeTimeWatcher(accidentVehicle,"setSevere_injured"));

        EditText notInjuredEdit=(EditText)itemView.findViewById(R.id.not_injured_edit);
        notInjuredEdit.setOnFocusChangeListener(new CustomeTimeWatcher(accidentVehicle,"setOnly_damage"));

        EditText driverLicenceEdit=(EditText)itemView.findViewById(R.id.license_one);
        driverLicenceEdit.setOnFocusChangeListener(new CustomeTimeWatcher(accidentVehicle,"setLicence_no"){
            public void afterFocus(final String text, final EditText editText) {
                new AsyncTask<Void,Void,Boolean>(){
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        DHIS2Modal dhis2Modal = new DHIS2Modal("Driver",null,MainOffence.username,MainOffence.password);
                        JSONObject where = new JSONObject();
                        try {
                            where.put("value",text);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JSONArray driver=dhis2Modal.getEvent(where);
                        Log.d(TAG,"returned driver json = "+driver.toString());
                        JSONObject driverObject = null;
                        try {
                            driverObject = driver.getJSONObject(0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            if(driverObject.getString("Driver License Number").equals(text)){
                                accidentVehicle.setProgram_driver(driverObject.getString("id"));
                                return true;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e){
                            Log.e(TAG,"returned json object is null");
                        }
                        return false;
                    }

                    @Override
                    protected void onPostExecute(Boolean aVoid) {
                        super.onPostExecute(aVoid);
                        if(!aVoid){
                            editText.setTextColor(context.getResources().getColor(R.color.red));
                        }else{
                            editText.setTextColor(context.getResources().getColor(R.color.light_gray));
                        }
                    }
                }.execute();

            }
        });

        final EditText alcoholEdit=(EditText)itemView.findViewById(R.id.alcohol_edit);
        alcoholEdit.setOnFocusChangeListener(new CustomeTimeWatcher(accidentVehicle,"setAlcohol_percentage"));

        CheckBox drug=(CheckBox)itemView.findViewById(R.id.drug_edit);
        drug.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                accidentVehicle.setDrug(isChecked);
            }
        });

        CheckBox phone=(CheckBox)itemView.findViewById(R.id.phone_edit);
        phone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                accidentVehicle.setPhone_use(isChecked);
            }
        });


        EditText plateNumber=(EditText)itemView.findViewById(R.id.registration_number_one);
        plateNumber.setOnFocusChangeListener(new CustomeTimeWatcher(accidentVehicle,"setPlate_number"){
            public void afterFocus(final String text, final EditText editText) {
                new AsyncTask<Void,Void,Boolean>(){
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        DHIS2Modal dhis2Modal = new DHIS2Modal("Vehicle",null,MainOffence.username,MainOffence.password);
                        JSONObject where = new JSONObject();
                        try {
                            where.put("value",text);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JSONArray vehiclesArray=dhis2Modal.getEvent(where);
                        Log.d(TAG, "returned vehicle json = "+vehiclesArray.toString());
                        JSONObject vehicleObject = null;
                        try {
                            vehicleObject = vehiclesArray.getJSONObject(0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            if(vehicleObject.getString("Vehicle Plate Number").equals(text)){
                                accidentVehicle.setProgram_vehicle(vehicleObject.getString("id"));
                                return true;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e){
                            Log.e(TAG,"returned json object is null");
                        }
                        return false;
                    }

                    @Override
                    protected void onPostExecute(Boolean aVoid) {
                        super.onPostExecute(aVoid);
                        if(!aVoid){
                            editText.setTextColor(context.getResources().getColor(R.color.red));
                        }else{
                            editText.setTextColor(context.getResources().getColor(R.color.light_gray));
                        }
                    }
                }.execute();

            }
        });

        EditText repairCost=(EditText)itemView.findViewById(R.id.repair_amount_one);
        repairCost.setOnFocusChangeListener(new CustomeTimeWatcher(accidentVehicle,"setEstimated_repair"));

        EditText vehicleEdit=(EditText)itemView.findViewById(R.id.vehicle_title_edit);
        vehicleEdit.setOnFocusChangeListener(new CustomeTimeWatcher(accidentVehicle,"setVehicle"));

        EditText vehicleTotalEdit=(EditText)itemView.findViewById(R.id.vehicle_total_edit);
        vehicleTotalEdit.setOnFocusChangeListener(new CustomeTimeWatcher(accidentVehicle,"setVehicle_total"));

        EditText infastructureEdit=(EditText)itemView.findViewById(R.id.infrastructure_edit);
        infastructureEdit.setOnFocusChangeListener(new CustomeTimeWatcher(accidentVehicle,"setInfastructure"));

        EditText costEdit=(EditText)itemView.findViewById(R.id.rescue_cost_edit);
        costEdit.setOnFocusChangeListener(new CustomeTimeWatcher(accidentVehicle,"setCost"));

        final List <PassengerVehicle> passengers= new ArrayList<PassengerVehicle>();

        addPasenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PassengerVehicle passengerVehicle =new PassengerVehicle();
                LinearLayout passenger = (LinearLayout) inflater.inflate(R.layout.passenger, null);

                linearLayout.addView(passenger);

                EditText nameEdit=(EditText)passenger.findViewById(R.id.name_edit);
                nameEdit.setOnFocusChangeListener(new CustomeTimeWatcher(passengerVehicle,"setName"));

                final EditText dateOfBirth=(EditText)passenger.findViewById(R.id.dob_one);
                dateOfBirth.setOnFocusChangeListener(new CustomeTimeWatcher(passengerVehicle,"setDate_of_birth"));

                EditText physicalAddressEdit=(EditText)passenger.findViewById(R.id.physical_address_one);
                physicalAddressEdit.setOnFocusChangeListener(new CustomeTimeWatcher(passengerVehicle,"setPhysical_address"));

                EditText addressBoxEdit=(EditText)passenger.findViewById(R.id.address_box_one);
                addressBoxEdit.setOnFocusChangeListener(new CustomeTimeWatcher(passengerVehicle,"setAddress"));

                EditText nationalIDEdit=(EditText)passenger.findViewById(R.id.national_id_one);
                nationalIDEdit.setOnFocusChangeListener(new CustomeTimeWatcher(passengerVehicle,"setNational_id"));

                EditText phoneNoEdit=(EditText)passenger.findViewById(R.id.phone_no_one);
                phoneNoEdit.setOnFocusChangeListener(new CustomeTimeWatcher(passengerVehicle,"setPhone_no"));

                EditText alcoholPercentageEdit=(EditText)passenger.findViewById(R.id.alcohol_percentage);
                alcoholPercentageEdit.setOnFocusChangeListener(new CustomeTimeWatcher(passengerVehicle,"setAlcohol_percent"));


                CheckBox seatbeltCheckbox=(CheckBox)passenger.findViewById(R.id.seat_belt_check);
                seatbeltCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        passengerVehicle.setHelmet(isChecked);
                    }
                });

                Button date_picker=(Button)passenger.findViewById(R.id.date_picker);
                date_picker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog newDatePickerDialogueFragment;
                        newDatePickerDialogueFragment=new DatePickerDialog();
                        newDatePickerDialogueFragment.show(AccidentReportFormActivity.fragmentManager,"DatePickerDialogue");
                        newDatePickerDialogueFragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
                                dateOfBirth.setText(dayOfMonth + " / " + (monthOfYear + 1) + " / "
                                        + year);
                            }
                        });
                    }
                });

                final RadioButton male = (RadioButton)passenger.findViewById(R.id.male);
                male.setTypeface(MainOffence.Roboto_Regular);

                final RadioButton female = (RadioButton)passenger.findViewById(R.id.female);
                female.setTypeface(MainOffence.Roboto_Regular);

                passengerVehicle.setGender("Male");
                male.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked)
                            passengerVehicle.setGender("Male");
                    }
                });

                female.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked)
                            passengerVehicle.setGender("Female");
                    }
                });

                passengers.add(linearLayout.getChildCount()-1,passengerVehicle);

            }
        });

        accident.add(position,accidentVehicle);
        passanger.add(position,passengers);



        ((ViewPager) container).addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((ScrollView) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;//super.getItemPosition(object);
    }

}