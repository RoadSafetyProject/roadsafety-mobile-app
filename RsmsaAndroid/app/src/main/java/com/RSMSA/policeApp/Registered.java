package com.RSMSA.policeApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


public class Registered extends Activity {
    /**
     * defining class variables
     */
    public ArrayList<String> mList = new ArrayList<String>();

    public String offenseDone;

    public int totalAmount;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view);
        final Bundle bundle = getIntent().getExtras();
        mList = bundle.getStringArrayList("list");
        totalAmount = bundle.getInt("amount");

        DatabaseHandlerOffence db = new DatabaseHandlerOffence(getApplicationContext());
        HashMap offence = new HashMap();
        offence = db.getOffenceDetails();
        offenseDone = "";

        /**
         * populating a list of all offenses to one string for the summary
         */
        for (int i=0; i<mList.size(); i++){
            offenseDone += "-"+mList.get(i)+"\n\n";
        }


        /**
         * Displays the registration details in Text view
         **/
        final TextView license = (TextView)findViewById(R.id.licence);
        final TextView plateNumber = (TextView)findViewById(R.id.plateNumber);
        final TextView commit = (TextView)findViewById(R.id.commit);
        final TextView offense = (TextView)findViewById(R.id.offence);
        offense.setText(offenseDone);

        final TextView amount = (TextView)findViewById(R.id.amount);
        final TextView issuerNo = (TextView)findViewById(R.id.issuer);// obtain from the login session
        final TextView created_at = (TextView)findViewById(R.id.regat);
        license.setText((CharSequence) offence.get("license"));
        plateNumber.setText( (CharSequence) offence.get("plateNumber"));
        commit.setText((CharSequence) offence.get("commit"));
        issuerNo.setText( (CharSequence) offence.get("rankNo"));
        created_at.setText( (CharSequence) offence.get("created_at"));
        amount.setText(totalAmount+"");

        Button ok = (Button) findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), MainOffence.class);
                startActivityForResult(myIntent, 0);
                finish();
            }
        });
    }}

