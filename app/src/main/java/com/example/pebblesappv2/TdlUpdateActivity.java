package com.example.pebblesappv2;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.Calendar;

/**
 * Created by ChunFaiHung on 2017/2/13.
 */

public class TdlUpdateActivity extends AppCompatActivity {

    private String org_date;
    private String org_time;
    private String org_desc;
//    private String org_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_td);
        // Setting toolbar interface
        Toolbar toolbar = (Toolbar) findViewById(R.id.tdl_update_toolbar);
        setSupportActionBar(toolbar);

        // Get passed values from TdlActivity
        Intent updateI = getIntent();
        org_date = updateI.getStringExtra("Org_Date");
        org_time = updateI.getStringExtra("Org_Time");
        org_desc = updateI.getStringExtra("Org_Desc");
//        org_id = updateI.getStringExtra("Org_Id");

        // Declaration of vars for both textviews for the pickers
        final TextView dateView = (TextView) findViewById(R.id.update_date_input);
        final TextView timeView = (TextView) findViewById(R.id.update_time_input);
        final TextView descView = (TextView) findViewById(R.id.update_desc_input);

        // Setting the original values
        dateView.setText(org_date);
        timeView.setText(org_time);
        descView.setText(org_desc);

        // Date Picker Dialog and Date Setter
        dateView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //To show current date in the datepicker
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(TdlUpdateActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        // TODO Auto-generated method stub
                    /*      Your code   to get date and time    */
                        selectedmonth = selectedmonth + 1;
                        DecimalFormat mFormat = new DecimalFormat("00");
                        dateView.setText("" + selectedyear + "-" + mFormat.format(selectedmonth) + "-" + mFormat.format(selectedday));
                        timeView.requestFocus();
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select Date");
                mDatePicker.show();
            }
        });

        // Time Picker Dialog and Time Setting
        timeView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(TdlUpdateActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        DecimalFormat timeFormat = new DecimalFormat("00");
                        timeView.setText( timeFormat.format(selectedHour) + ":" + timeFormat.format(selectedMinute));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });


        // OKAY / CANCEL onclick listeners
        Button okayButt = (Button) findViewById(R.id.update_ok_butt);
        Button cancelButt = (Button) findViewById(R.id.update_cancel_btn);

        okayButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendResult();
            }
        });
        cancelButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void sendResult() {
        String dateInput = ((TextView) findViewById(R.id.update_date_input)).getText().toString();
        String timeInput = ((TextView) findViewById(R.id.update_time_input)).getText().toString();
        String descInput = ((TextView) findViewById(R.id.update_desc_input)).getText().toString();
        Intent i = new Intent();
        i.putExtra("dateInput", dateInput);
        i.putExtra("timeInput", timeInput);
        i.putExtra("descInput", descInput);
        setResult(RESULT_OK, i);
        finish();
    }

//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.tdl_drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        if (id == R.id.tdl_page) {
//
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.tdl_drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
}
