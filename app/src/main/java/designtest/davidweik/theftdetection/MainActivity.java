package designtest.davidweik.theftdetection;

import android.content.SharedPreferences;
import android.location.Location;
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
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    
    NumberPicker maxDistance;

    GoogleApiClient mGoogleApiClient;
    Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildGoogleApiClient();

        maxDistance = (NumberPicker) findViewById(R.id.distance_from_home);
        maxDistance.setMinValue(0);
        maxDistance.setMaxValue(500);

        mGoogleApiClient.connect();

    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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

    @Override
    public void onConnected(Bundle bundle) {
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
