package com.RSMSA.policeApp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 *  Created by Isaiah on 8/27/2014.
 */
public class VerifyCarActivity extends Activity {

    /**
     * defining the instances of our view elements
     */

    public Button okay_button;

    public TextView licenseHeader;

    public TextView nameTitle;

    public TextView addressTitle;

    public TextView expiryDater;

    public TextView statusTitle;

    public TextView carInfoTitle;

    public TextView makeTitle;

    public TextView carTypeTitle;

    public TextView carColorTitle;

    public TextView name;

    public TextView address;

    public TextView Viewname;

    public TextView Viewtype;

    public TextView ViewCarcolor;

    public TextView Viewmake;

    public TextView Viewaddress;

    public TextView ViewexiryDate;

    public TextView ViewLicenceclass;

    public TextView Viewstatus;


    /**
     * Defining our class variables
     */
    public String plateNumber;

    public String licenseNumber;

    public String type;

    public String Carcolor;

    public String make;

    public String Drivername;

    public String Driveraddress;

    public String Licenceclass;

    public String expiryDate;

    public String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        final Bundle bundle=getIntent().getExtras();
        plateNumber = bundle.getString("plateNo");
        licenseNumber = bundle.getString("license");
        type = bundle.getString("type");
        Carcolor = bundle.getString("color");
        make = bundle.getString("make");
        Drivername = bundle.getString("name");
        Driveraddress = bundle.getString("address");

        expiryDate = bundle.getString("expiryDate");
        Licenceclass = bundle.getString("class");
        status = bundle.getString("status");
        setContentView(R.layout.verify_car_activity);

        /**
         * call the init() method to instantiate all our class variables
         */
        init();
    }


    /**
     * the method to instantiate all our class variables
     */
    public void init(){

        /**
         * getting instances of our view elements
         */
        okay_button = (Button)findViewById(R.id.okay_button);

        addressTitle = (TextView)findViewById(R.id.address_title);
        addressTitle.setTypeface(MainOffence.Roboto_Condensed);

        carColorTitle = (TextView)findViewById(R.id.car_color_title);
        carColorTitle.setTypeface(MainOffence.Roboto_Condensed);

        carInfoTitle = (TextView)findViewById(R.id.car_info_title);
        carInfoTitle.setTypeface(MainOffence.Rosario_Bold);

        carTypeTitle = (TextView)findViewById(R.id.car_type_title);
        carTypeTitle.setTypeface(MainOffence.Roboto_Condensed);

        makeTitle = (TextView)findViewById(R.id.make_title);
        makeTitle.setTypeface(MainOffence.Roboto_Condensed);

        nameTitle = (TextView)findViewById(R.id.name_title);
        nameTitle.setTypeface(MainOffence.Roboto_Condensed);


        Viewname = (TextView)findViewById(R.id.name);
        Viewname.setText(Drivername);

        Viewaddress = (TextView)findViewById(R.id.address);
        Viewaddress.setText(Driveraddress);

        ViewLicenceclass = (TextView)findViewById(R.id.licence_class);
        ViewLicenceclass.setText(Licenceclass);

        ViewexiryDate = (TextView)findViewById(R.id.date);
        ViewexiryDate.setText(expiryDate);

        Viewstatus = (TextView)findViewById(R.id.status);
        Viewstatus.setText(status);

        Viewmake = (TextView)findViewById(R.id.make);
        Viewmake.setText(make);

        Viewtype = (TextView)findViewById(R.id.car_type);
        Viewtype.setText(type);

        ViewCarcolor = (TextView)findViewById(R.id.color);
        ViewCarcolor.setText(Carcolor);

        statusTitle = (TextView)findViewById(R.id.status_title);
        statusTitle.setTypeface(MainOffence.Roboto_Condensed);

        licenseHeader = (TextView)findViewById(R.id.content_header);
        licenseHeader.setTypeface(MainOffence.Rosario_Bold);

        expiryDater = (TextView)findViewById(R.id.expiry_date);
        expiryDater.setTypeface(MainOffence.Roboto_Condensed);

        okay_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });
    }

    /**
     * Overiding h to return back results to the previous activity
     */
    @Override
    public void finish(){
        Intent returnIntent=new Intent();
        setResult(RESULT_OK,returnIntent);
        super.finish();
    }

}
