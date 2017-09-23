/*
 * omada17
 * Kassari Anastasia 3130088
 * Kourli Vasileia 3130101
 * Stavrinos Michail Taxiarchis 3130193
 */

package omada17.newyorkapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

public class InputDates extends Activity {

    Button btn1,btn2;
    int year_x, month_x, day_x, hour_x, minute_x, year_y, month_y, day_y, hour_y, minute_y;
    boolean first = false, last = false, nodates = false;
    int dates = 0;
    ArrayList<Coordinates> cords;
    String datesql ="";

    //dialog id
    static final int DATE_DIALOG_ID1 = 1;
    static final int DATE_DIALOG_ID2 = 2;
    static final int TIME_DIALOG_ID1 = 3;
    static final int TIME_DIALOG_ID2 = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inputdate);

        year_x = 2012;
        year_y = year_x;

        month_x = 2;
        month_y = month_x;

        day_x = 4;
        day_y = day_x;


        cords = (ArrayList<Coordinates>) getIntent().getSerializableExtra("cords");

        showDialogOnButtonClick();
    }

    public void showDialogOnButtonClick()
    {
        btn1 = (Button) findViewById(R.id.frombutton);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID1);
            }
        });

        btn2 = (Button) findViewById(R.id.tobutton);

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID2);
            }
        });


        CheckBox check = (CheckBox) findViewById(R.id.checkBox);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!nodates)
                    ((TextView) findViewById(R.id.printdates)).setText("Continuing without dates...");
                else
                    ((TextView) findViewById(R.id.printdates)).setText(" ");
                nodates = !nodates;
            }
        });


        ((CheckBox) findViewById(R.id.first)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                first = !first;
            }
        });

        ((CheckBox) findViewById(R.id.last)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                last = !last;
            }
        });

        Button next = (Button) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((first && last) || nodates) {
                    //send null date
                    datesql = null;
                }
                else if (first) {
                    //send time < date_y
                    datesql = "time <= \'"+year_y+"-"+month_y+"-"+day_y+" "+hour_y+":"+minute_y+" \'";
                }
                else if (last) {
                    //send date_x < time
                    datesql = "time >= \'"+year_x+"-"+month_x+"-"+day_x+" "+hour_x+":"+minute_x+" \'";
                } else {
                    //send date_x < time and time < date_y
                    datesql = "time >= \'"+year_x+"-"+month_x+"-"+day_x+" "+hour_x+":"+minute_x+" \' AND time <= \'"+year_y+"-"+month_y+"-"+day_y+" "+hour_y+":"+minute_y+" \'";
                }

                Intent intent = new Intent(getApplicationContext(),FindBest.class);

                intent.putExtra("cords", cords);
                intent.putExtra("date",datesql);

                //Start the next activity
                startActivity(intent);

            }
        });



    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {
            case 1: return new DatePickerDialog(this, dpickerListener1, year_x, month_x, day_x);

            case 2: return new DatePickerDialog(this, dpickerListener2, year_y ,month_y, day_y);

            case 3: return new TimePickerDialog(this, tpickerListener1,  hour_x, minute_x, DateFormat.is24HourFormat(this));

            case 4: return new TimePickerDialog(this, tpickerListener2, hour_y, minute_y, DateFormat.is24HourFormat(this));

            default: return null;
        }
    }

    private DatePickerDialog.OnDateSetListener dpickerListener1 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            year_x = year;
            month_x = monthOfYear+1;
            day_x = dayOfMonth;

            Toast.makeText(InputDates.this, day_x + "/" + month_x + "/" + year_x, Toast.LENGTH_LONG).show();

            dates++;

            //show time dialog
            showDialog(TIME_DIALOG_ID1);
        }
    };

    private DatePickerDialog.OnDateSetListener dpickerListener2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            year_y = year;
            month_y = monthOfYear+1;
            day_y = dayOfMonth;

            Toast.makeText(InputDates.this, day_y + "/" + month_y + "/" +  year_y, Toast.LENGTH_LONG).show();

            dates++;

            //show time dialog
            showDialog(TIME_DIALOG_ID2);

        }
    };

    private TimePickerDialog.OnTimeSetListener tpickerListener1 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            hour_x = hourOfDay;
            minute_x = minute;

            Toast.makeText(InputDates.this, hour_x+":"+(minute_x < 10? "0"+minute_x : minute_x), Toast.LENGTH_LONG).show();

            dates++;

        }
    };

    private TimePickerDialog.OnTimeSetListener tpickerListener2 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            hour_y = hourOfDay;
            minute_y = minute;

            if (checkDates()) {

                Toast.makeText(InputDates.this, hour_y + ":" +(minute_y < 10? "0"+minute_y : minute_y), Toast.LENGTH_LONG).show();

                dates++;
                if (dates >= 4)
                {
                    ((TextView) findViewById(R.id.printdates)).setText("FROM: " + day_x + "/" + month_x + "/" + year_x  + " "+hour_x + ":" +(minute_x < 10? "0"+minute_x : minute_x)+ "\nTO: " + day_y + "/" + month_y + "/" + year_y + " "+hour_y + ":" +(minute_y < 10? "0"+minute_y : minute_y));

                }
            } else {
                dates--;
                Toast.makeText(InputDates.this, "Incorrect dates...", Toast.LENGTH_LONG).show();

            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    private boolean checkDates()
    {
        Date d1 = new Date(year_x, month_x, day_x, hour_x, minute_x);
        Date d2 = new Date(year_y ,month_y, day_y, hour_y,minute_y);
        if (d2.before(d1))
            return false;
        else
            return true;
    }


}
