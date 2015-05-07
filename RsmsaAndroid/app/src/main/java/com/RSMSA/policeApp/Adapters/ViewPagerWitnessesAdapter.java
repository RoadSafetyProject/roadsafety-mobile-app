package com.RSMSA.policeApp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.RSMSA.policeApp.AccidentReportFormActivity;
import com.RSMSA.policeApp.AccidentTypeclassificationActivity;
import com.RSMSA.policeApp.Dhis2.DHIS2Modal;
import com.RSMSA.policeApp.MainOffence;
import com.RSMSA.policeApp.Models.Witness;
import com.RSMSA.policeApp.R;
import com.RSMSA.policeApp.Utils.CustomeTimeWatcher;
import com.android.datetimepicker.date.DatePickerDialog;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Coze on 7/3/13.
 */

public class ViewPagerWitnessesAdapter extends PagerAdapter {
    private static final String TAG="VIEW PAGER CINEMA HALL ADAPTER";
    private static Context context;
    private LayoutInflater inflater;
    List<String> tabnames;

    public ViewPagerWitnessesAdapter(Context context, List<String> tnames) {
        this.context  = context;
        tabnames=tnames;

    }

    public static List<Witness> witnesses=new ArrayList<Witness>();




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
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final Witness witness=new Witness();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout itemView = (RelativeLayout)inflater.inflate(R.layout.witness, container,false);
        Button nextButton = (Button)itemView.findViewById(R.id.next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,AccidentTypeclassificationActivity.class);
                if(AccidentReportFormActivity.imagePath!=null) {
                    intent.putExtra("imagePath",AccidentReportFormActivity.imagePath );
                }
                if(AccidentReportFormActivity.videoPath!=null) {
                    intent.putExtra("videoPath",AccidentReportFormActivity.videoPath );
                }
                context.startActivity(intent);
            }
        });

        if(position==tabnames.size()-1){
            nextButton.setVisibility(View.VISIBLE);
        }else{
            nextButton.setVisibility(View.INVISIBLE);
        }

        EditText nameEdit=(EditText)itemView.findViewById(R.id.name_edit);
        nameEdit.setOnFocusChangeListener(new CustomeTimeWatcher(witness,"setName"));

        final EditText dateOfBirth=(EditText)itemView.findViewById(R.id.dob_one);
        dateOfBirth.setOnFocusChangeListener(new CustomeTimeWatcher(witness,"setDate_of_birth"));

        EditText physicalAddressEdit=(EditText)itemView.findViewById(R.id.physical_address_one);
        physicalAddressEdit.setOnFocusChangeListener(new CustomeTimeWatcher(witness,"setPhysical_address"));

        EditText addressBoxEdit=(EditText)itemView.findViewById(R.id.address_box_one);
        addressBoxEdit.setOnFocusChangeListener(new CustomeTimeWatcher(witness,"setAddress"));

        EditText nationalIDEdit=(EditText)itemView.findViewById(R.id.national_id_one);
        nationalIDEdit.setOnFocusChangeListener(new CustomeTimeWatcher(witness,"setNational_id"));

        EditText phoneNoEdit=(EditText)itemView.findViewById(R.id.phone_no_one);
        phoneNoEdit.setOnFocusChangeListener(new CustomeTimeWatcher(witness,"setPhone_no"));




        Button date_picker=(Button)itemView.findViewById(R.id.date_picker);
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


        final RadioButton male = (RadioButton)itemView.findViewById(R.id.male);
        male.setTypeface(MainOffence.Roboto_Regular);

        final RadioButton female = (RadioButton)itemView.findViewById(R.id.female);
        female.setTypeface(MainOffence.Roboto_Regular);

        witness.setGender("Male");
        male.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    witness.setGender("Male");
            }
        });

        female.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    witness.setGender("Female");
            }
        });



        try {
            AccidentReportFormActivity.witnesses.put(position,witness.getjson(witness));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        witnesses.add(position,witness);

        ((ViewPager) container).addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((RelativeLayout) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;//super.getItemPosition(object);
    }

}