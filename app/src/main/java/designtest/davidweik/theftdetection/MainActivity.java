package designtest.davidweik.theftdetection;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;

    NumberPicker maxDistance;
    Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        maxDistance = (NumberPicker) findViewById(R.id.distance_from_home);
        maxDistance.setMinValue(0);
        maxDistance.setMaxValue(500);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                SharedPreferences sharedPref = getSharedPreferences("mypref",0);
                int disc = sharedPref.getInt("enteredDistance", 0);
                float latitudeHome = sharedPref.getFloat("latitude", 0);
                float longitudeHome = sharedPref.getFloat("longitude", 0);

                float latitudeCurrent = (float) location.getLatitude();
                float longitudeCurrent = (float) location.getLongitude();

                Location selected_location=new Location("locationA");
                selected_location.setLatitude(latitudeHome);
                selected_location.setLongitude(longitudeHome);
                Location near_locations=new Location("locationA");
                near_locations.setLatitude(latitudeCurrent);
                near_locations.setLongitude(longitudeCurrent);

                float distance=selected_location.distanceTo(near_locations);

                if (distance > disc) {
                    Uri alarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                    MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), alarm);
                    mediaPlayer.start();
                } else {
                    Toast.makeText(MainActivity.this, "Nothing has been stolen......yet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
        }catch (SecurityException se) {
            se.printStackTrace();
        }
        checkLocation();
    }

    private void checkLocation() {
        try {
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, locationListener);
            lastLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
        } catch (SecurityException se) {
            System.out.println("Oh no");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveDistance(View view) {
        SharedPreferences sharedPref = getSharedPreferences("mypref", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("enteredDistance", maxDistance.getValue());
        editor.commit();
    }

    public void saveGeoLoc(View view) {
        float latitude = (float) lastLocation.getLatitude();
        float longitude = (float) lastLocation.getLongitude();

        SharedPreferences sharedPref = getSharedPreferences("mypref", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat("latitude", latitude);
        editor.putFloat("longitude", longitude);
        editor.commit();
    }

    public void calcutedDistance(View view) {
        SharedPreferences sharedPref = getSharedPreferences("mypref",0);
        int disc = sharedPref.getInt("enteredDistance", 0);
        float latitudeHome = sharedPref.getFloat("latitude", 0);
        float longitudeHome = sharedPref.getFloat("longitude", 0);

        float latitudeCurrent = (float) lastLocation.getLatitude();
        float longitudeCurrent = (float) lastLocation.getLongitude();

        Location selected_location=new Location("locationA");
        selected_location.setLatitude(latitudeHome);
        selected_location.setLongitude(longitudeHome);
        Location near_locations=new Location("locationA");
        near_locations.setLatitude(latitudeCurrent);
        near_locations.setLongitude(longitudeCurrent);

        float distance=selected_location.distanceTo(near_locations);

        if (distance > disc) {
            Uri alarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), alarm);
            mediaPlayer.start();
        } else {
            Toast.makeText(MainActivity.this, "Nothing has been stolen......yet", Toast.LENGTH_SHORT).show();
        }
    }

    public void clearStoredValues(View view) {
        SharedPreferences sharedPref = getSharedPreferences("mypref",0);
        SharedPreferences.Editor editor1 = sharedPref.edit();
        editor1.clear();
        editor1.commit();
    }

}
