package com.RSMSA.policeApp.Dialogues;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.RSMSA.policeApp.Models.Offence;
import com.RSMSA.policeApp.Models.Receipt;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

import com.RSMSA.policeApp.Adapters.SpinnerAdapters.PaymentMethodSpinnerAdapter;
import com.RSMSA.policeApp.PoliceFunction;
import com.RSMSA.policeApp.R;


/**
 * Dialog allowing users to select a date.
 */
public class PaymentConfirmationDialogue extends DialogFragment {
    private static final String TAG="DefaultCountryChooserDialogue";
    private String[] countries;
    private SharedPreferences sharedPrefs;
    private String storedCountry;
    private int count=0,index;
    private Offence offence;
    private JSONArray events;
    private boolean sentSucessfully=false;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    private OnCompleteListener mListener;


    public void setName(String name) {
        this.name = name;
    }

    private String paymentMethod,name;
    private int cost;
    private EditText receiptEditText;


    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setEvents(JSONArray events) {
        this.events = events;
    }

    public PaymentConfirmationDialogue() {
    }

    public void setOffence(Offence offence) {
        this.offence = offence;
    }

    // public PaymentConfirmationDialogue(Offence offence,) {
    //}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Activity activity = getActivity();
        activity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());



    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);

        RelativeLayout relativeLayout =(RelativeLayout)inflater.inflate(R.layout.dialogue_payment_confirmation, container, false);



        TextView costTextView=(TextView)relativeLayout.findViewById(R.id.place);
        costTextView.setText(cost+"");

        TextView nameTextView=(TextView)relativeLayout.findViewById(R.id.name);
        nameTextView.setText(name);

        TextView licenceNumberTextView=(TextView)relativeLayout.findViewById(R.id.licence_number);
        licenceNumberTextView.setText(offence.getDriver_license_number()+"");

        TextView plate_numberTextView=(TextView)relativeLayout.findViewById(R.id.plate_number);
        plate_numberTextView.setText(offence.getVehicle_plate_number());


        final String [] paymentMethodsArray=this.getResources().getStringArray(R.array.payment_methods);
        Spinner paymentMethodSpinner= (Spinner)relativeLayout.findViewById(R.id.payment_method_spinner);
        PaymentMethodSpinnerAdapter adapter = new PaymentMethodSpinnerAdapter(getActivity(), R.layout.row_menu,paymentMethodsArray);
        paymentMethodSpinner.setAdapter(adapter);
        paymentMethodSpinner.setBackground(null);
        paymentMethodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                paymentMethod=paymentMethodsArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        receiptEditText=(EditText)relativeLayout.findViewById(R.id.receipt);

        TextView okButton=(TextView)relativeLayout.findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(receiptEditText.getText().toString().equals("")){
                    receiptEditText.setHintTextColor(Color.RED);
                }else {
                    new ProcessRegister().execute();
                }
            }
        });

        TextView cancelButton=(TextView)relativeLayout.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mListener.onComplete(false, index);
                dismiss();
            }
        });


        return relativeLayout;
    }

    private class ProcessRegister extends AsyncTask<String, String, JSONObject> {
        String input_license,input_plateNumber,input_issuer_no;
        boolean input_commit;
        CharSequence Input_issuer;

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONObject json=new JSONObject();
            Calendar cl=Calendar.getInstance();
            Receipt receipt=new Receipt();
            receipt.setReceipt_number(receiptEditText.getText()+"");
            receipt.setAmount(cost+"");
            receipt.setPayment_mode(paymentMethod);
            receipt.setDate(cl.getTimeInMillis());

            PoliceFunction PFunction = new PoliceFunction();
            json = PFunction.registerOffence(offence.getjson(offence),receipt.getjson(receipt),events);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            Log.d(TAG,"JSONreceived == "+json);
            sentSucessfully=true;
            dismiss();
            Toast toast = Toast.makeText(getActivity(),
                    "Payment saved successfully", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    @Override
    public void dismiss() {
        super.dismiss();
        mListener.onComplete(sentSucessfully, index);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onActivityCreated(Bundle arg0){
        super.onActivityCreated(arg0);
        getDialog().getWindow().getAttributes().windowAnimations= R.style.dialogue_animation;
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mListener = (OnCompleteListener)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }

    public static interface OnCompleteListener {
        public abstract void onComplete(boolean saved, int index);
    }

    public void onAttach(Fragment activity) {
        super.onAttach(activity.getActivity());
        try {
            this.mListener = (OnCompleteListener)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }


}
