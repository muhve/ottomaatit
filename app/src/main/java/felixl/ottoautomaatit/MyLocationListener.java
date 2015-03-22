package felixl.ottoautomaatit;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class MyLocationListener implements LocationListener {
    MapsActivity activity;

    public MyLocationListener(MapsActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onLocationChanged(Location location) {
         activity.updateMarkers();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        activity.updateMarkers();
    }

    @Override
    public void onProviderEnabled(String provider) {
        activity.updateMarkers();
    }

    @Override
    public void onProviderDisabled(String provider) {
        activity.updateMarkers();
    }
}
