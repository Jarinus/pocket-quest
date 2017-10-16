package nl.pocketquest.pocketquest;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.mapbox.services.android.location.LostLocationEngine;
import com.mapbox.services.android.location.MockLocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.location.LocationEnginePriority;
import com.mapbox.services.commons.models.Position;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements LocationEngineListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String TAG = MainActivity.class.getSimpleName();

    private LocationEngine locationEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationEngine = new LostLocationEngine(this);
        locationEngine.addLocationEngineListener(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        locationEngine.activate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected() {
        Log.i(LOG_TAG, "Connected to engine, we can now request updates.");
        locationEngine.requestLocationUpdates();
        locationEngine.getLastLocation();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged: ");
        if (location != null) {
            Log.i(LOG_TAG, "New location received: " + location.toString());
        }
    }
}