/*
 * omada17
 * Kassari Anastasia 3130088
 * Kourli Vasileia 3130101
 * Stavrinos Michail Taxiarchis 3130193
 */

package omada17.newyorkapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;


public class EnterCoordinates extends Activity {


    private EditText lat1,lat2,long1,long2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinates);

        Button button = (Button) findViewById(R.id.button);

        lat1 = (EditText) findViewById(R.id.lat1);
        lat2 = (EditText) findViewById(R.id.lat2);
        long1 = (EditText) findViewById(R.id.long1);
        long2 = (EditText) findViewById(R.id.long2);

        lat1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lat1.setText("");
            }
        });

        lat2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lat2.setText("");
            }
        });

        long1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long1.setText("");
            }
        });

        long2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long2.setText("");
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                double la1 = 0 , la2 = 0, lo1 = 0, lo2 = 0;

                try {
                    la1 = Double.parseDouble(lat1.getText().toString());
                }
                catch (NumberFormatException e)
                {};

                try {
                    la2 = Double.parseDouble(lat2.getText().toString());
                }
                catch (NumberFormatException e)
                {};

                try {
                    lo1 = Double.parseDouble(long1.getText().toString());
                }
                catch (NumberFormatException e)
                {};

                try {
                    lo2 = Double.parseDouble(long2.getText().toString());
                }
                catch (NumberFormatException e)
                {};

                if (checkLatLongs(la1,la2,lo1,lo2))
                {
                    ArrayList<Coordinates> cords = new ArrayList<Coordinates>();
                    if (la2 < la1)
                    {
                        double temp = la1;
                        la1 = la2;
                        la2 = temp;
                    }

                    if (lo2 < lo1)
                    {
                        double temp = lo1;
                        lo1 = lo2;
                        lo2 = temp;
                    }
                    cords.add(new Coordinates(la1,lo1));
                    cords.add(new Coordinates(la2,lo2));

                    //Send results to map
                    Intent intent = new Intent(getApplicationContext(),InputDates.class);

                    intent.putExtra("cords", cords);

                    //Start the next activity
                    startActivity(intent);
                }
                else
                {
                    ((EditText) findViewById(R.id.error)).setVisibility(View.VISIBLE);
                }

            }
        });


    }


    private boolean checkLatLongs(double la1, double la2, double lo1, double lo2)
    {
        //if one of them is not initialized by the user
        if (la1 == 0 || la2 == 0 || lo1 == 0 || lo2 == 0)
            return false;

        if (la2 < la1)
        {
            double temp = la1;
            la1 = la2;
            la2 = temp;
        }

        if (lo2 < lo1)
        {
            double temp = lo1;
            lo1= lo2;
            lo2 = temp;
        }

        if (la1 < Constants.MIN_LAT || la1 > Constants.MAX_LAT)
            return false;
        if (la2 < la1 || la2 > Constants.MAX_LAT)
            return false;
        if (lo1 > Constants.MAX_LONG || lo1 < Constants.MIN_LONG)
            return false;
        if (lo2 > Constants.MAX_LONG || lo2 < lo1)
            return false;

        return true;
    }


}
