/*
 * omada17
 * Kassari Anastasia 3130088
 * Kourli Vasileia 3130101
 * Stavrinos Michail Taxiarchis 3130193
 */

package omada17.newyorkapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Vector;

public class FindBest extends Activity {

    private ArrayList<Coordinates> cords;
    private String datesql;
    private Vector<ArrayList<Checkin>> vec;
    private boolean results = false;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findbest);
        context = getApplicationContext();

        cords = (ArrayList<Coordinates>) getIntent().getSerializableExtra("cords");
        datesql = getIntent().getStringExtra("date");

        Button button = (Button) findViewById(R.id.press);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AsyncTaskThread att = new AsyncTaskThread();
                att.execute();

                TextView txt = (TextView) findViewById(R.id.results);
                txt.setText("Waiting for results...");
                txt.setMovementMethod(new ScrollingMovementMethod());

            }
        });

        Button finalbutton = (Button) findViewById(R.id.finalbutton);
        finalbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (results) {
                    //Send results to map
                    Intent intent = new Intent(getApplicationContext(), NYMaps.class);

                    intent.putExtra("vector", vec);

                    //Start the next activity
                    startActivity(intent);
                }
            }
        });



    }


    private class AsyncTaskThread extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected void onPreExecute()
        {

        }

        @Override
        protected Void doInBackground(Void... params) {
            findResults();
            return null;
        }


        @Override
        protected void onPostExecute(Void v)
        {

            System.out.println("Starting on post execute!");
            ListView list = (ListView) findViewById(R.id.list);
            String s="Results found!\nTap on result for more info and photos...\n";
            String values[] = new String[vec.size()];
            if ((vec == null) || (vec.size() == 0))
                s = "No results found...\n";

            results = true;
            TextView txt = (TextView) findViewById(R.id.results);
            txt.setText(s);

            for (int i=0;i< vec.size();i++)
            {
                ArrayList<Checkin> a = vec.get(i);
                values[i] = "("+(i+1)+") "+a.get(0).getPOIname()+"\n with "+a.get(a.size() - 1).getPOI() + " checkins and "+(a.size()-1)+" photo links.";
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.simple_string_cell, R.id.simplestring,values);

            list.setAdapter(adapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ArrayList<Checkin> a = vec.get(position);
                    String info = a.get(0).getPOIname()+"\nID: "+a.get(0).getPOI()+"\n"+a.get(0).getCoordinates().toString()+"\nNumber of checkins: "+a.get(a.size() - 1).getPOI()+"\n "+(a.size()-1)+" photos.";
                    String []urls = new String[a.size()-1];
                    for (int i=0; i<a.size()-1;i++)
                        urls[i] = a.get(i).getPhotoLink();

                    if (urls.length == 0)
                        return;

                    Intent intent = new Intent(context, LoadImage.class);
                    intent.putExtra("info",info);
                    intent.putExtra("urls",urls);
                    startActivity(intent);



                }
            });

        }

    }


    private void findResults() {
        if (cords == null)
            return;

        double lat1 = cords.get(0).getLatitude();
        double lat2 = cords.get(1).getLatitude();
        double long1 = cords.get(0).getLongitude();
        double long2 = cords.get(1).getLongitude();


        //Break cords for mappers

        System.out.println("Breaking cords");

        Coordinates[][] coordinates = new Coordinates[Constants.MAP_NUM][2];
        for (int i = 0; i < Constants.MAP_NUM; i++) {
            if (i == 0)
                coordinates[i][0] = new Coordinates(lat1 + i * ((lat2 - lat1) / Constants.MAP_NUM), long1);
            else
                coordinates[i][0] = new Coordinates(Math.nextAfter(lat1 + i * ((lat2 - lat1) / Constants.MAP_NUM), lat1 + i * ((lat2 - lat1) / Constants.MAP_NUM + 1)), long1);
            coordinates[i][1] = new Coordinates(lat1 + (i + 1) * ((lat2 - lat1) / Constants.MAP_NUM), long2);
        }

        for (int i = 0; i < Constants.MAP_NUM; i++) {
            System.out.println(coordinates[i][0].toString() + " \n" + coordinates[i][1].toString());
        }

        Thread[] threads = new Thread[Constants.MAP_NUM];
        for (int i = 0; i < Constants.MAP_NUM; i++) {
            threads[i] = new Thread(new Client(Constants.MAP_ADDRESS[i], Constants.MAP_PORT[i], coordinates[i], datesql));
            threads[i].start();
        }

        //Join threads
        try {
            for (int i = 0; i < Constants.MAP_NUM; i++)
                threads[i].join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Mappers ended...");
        System.out.println("\n\n------------------------------------");

        System.out.println("4)Contacting reducer!...");
        //Make thread for client<->reducer communication
        Thread c = new Thread(new Client(Constants.REDUCER_ADDRESS, Constants.REDUCER_PORT, Constants.CLIENT_PORT));
        c.start();

        try {
            c.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Returning vec");
        vec = Client.vec;
    }




}
