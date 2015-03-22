package felixl.ottoautomaatit;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.felix.ottoautomaatit.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private LocationManager locationManager;
    private LocationListener locationListener;

    private ArrayList<Marker> markers;
    private Marker nearest;
    private List<LatLng> places;
    private List<String> providers;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        textView = (TextView) findViewById(R.id.textView);

        this.locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        this.locationListener = new MyLocationListener(this);
        this.providers = locationManager.getProviders(true);
        this.markers = new ArrayList<Marker>();
        this.places = new FileReader(this).doInBackground();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy
                                                           .Builder()
                                                           .permitAll()
                                                           .build();
            StrictMode.setThreadPolicy(policy);
        }

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 30000, 10, this.locationListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.locationManager.removeUpdates(this.locationListener);
    }

    @Override
    public void onMapReady(GoogleMap mMap){
        GoogleMap map = mMap;

        map.setMyLocationEnabled(true);
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 30000, 10, this.locationListener);

        LatLng myLatLng = new LatLng(getLastKnownLocation().getLatitude(),
                                     getLastKnownLocation().getLongitude());

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15));

        textView.setText("Etsitään lähintä automaattia...");

        for (LatLng place : places) {
            Marker marker = map.addMarker(new MarkerOptions().position(place).alpha(0.5f));
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.o));
            markers.add(marker);
        }

        nearest = markers.get(markers.size()-1);

        updateMarkers();
    }

    public void updateMarkers() {
        LatLng myLatLng = new LatLng(getLastKnownLocation().getLatitude(),
                                     getLastKnownLocation().getLongitude());

        float minDistance = Float.MAX_VALUE;

        for (Marker marker : markers) {
            LatLng place = marker.getPosition();
            float[] results = new float[3];
            Location.distanceBetween(myLatLng.latitude, myLatLng.longitude, place.latitude, place.longitude, results);

            double dist = results[0]/1000;
            dist = (double)Math.round(dist*100)/100;

            if ((int)results[0] >= 1000) {
                marker.setTitle(dist+" km");
            } else {
                marker.setTitle((int)results[0]+" m");
            }

            if (results[0] < minDistance) {
                minDistance = results[0];
                nearest.setAlpha(0.5f);
                nearest = marker;
                nearest.setAlpha(1f);
            }
        }

        textView.setText("Etäisyys: "+distanceToString(minDistance));
    }

    private String distanceToString(float minDistance) {
        String distS;
        double dist = minDistance/1000;
        dist = (double)Math.round(dist*100)/100;

        if (dist >= 1000) {
            distS = dist+" km";
        } else {
            distS = (int)minDistance+" m";
        }
        return distS;
    }

    private Location getLastKnownLocation() {
        Location bestLocation = null;

        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);

            if (l == null) {
                continue;
            }

            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }
}
