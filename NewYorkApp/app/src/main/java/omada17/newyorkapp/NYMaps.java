/*
 * omada17
 * Kassari Anastasia 3130088
 * Kourli Vasileia 3130101
 * Stavrinos Michail Taxiarchis 3130193
 */
package omada17.newyorkapp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class NYMaps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private static final LatLng NY = new LatLng(40.763889, -73.98);
    private ArrayList<ArrayList<Checkin>> vec;
    private MarkerOptions options = new MarkerOptions();
    private ArrayList<Marker> markers = new ArrayList<Marker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nymaps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        vec = (ArrayList<ArrayList<Checkin>>) getIntent().getSerializableExtra("vector");

        int i=1;
        for(ArrayList<Checkin> list : vec) {
            Checkin c = list.get(0);
            options.position(new LatLng(c.getCoordinates().getLatitude(), c.getCoordinates().getLongitude()));
            options.title("("+i + ") " + c.getPOIname());
            options.snippet(c.getCoordinates().toString());
            markers.add(mMap.addMarker(options));
            i++;

            mMap.moveCamera(CameraUpdateFactory.newLatLng(NY));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        }

    }

}
