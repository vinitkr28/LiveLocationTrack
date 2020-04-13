package vinit.livelocationtrack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LiveLocationBroadcast.LiveLocationUpdate {
    private static final String TAG = MapsActivity.class.getName();

    private GoogleMap mMap;

    private boolean mAlreadyStartedService = false;

    private LiveLocationBroadcast mLiveLocationBroadcast;

    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mLiveLocationBroadcast = new LiveLocationBroadcast(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {

        LocalBroadcastManager.getInstance(this).registerReceiver(mLiveLocationBroadcast, new IntentFilter(LiveLocationBroadcast.ACTION_LOCATION_BROADCAST));
        if (!isLiveLocationRunning(LiveLocationUpdateService.class)) {
            startLocationTracking();
        }
        super.onResume();
    }

    private boolean isLiveLocationRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng launchLocation = new LatLng(12.9098522, 77.6796368);
        marker =  mMap.addMarker(new MarkerOptions().position(launchLocation).title("I am here."));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(launchLocation));
    }

    @Override
    public void onLiveLocationValueUpdated(Location location) {
        if (location == null) return;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

       mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
        marker.setPosition(latLng);
    }

    private void startLocationTracking() {
        Log.d(TAG, "startLocationTracking");

        if (!mAlreadyStartedService) {

            Intent intent = new Intent(this, LiveLocationUpdateService.class);
            startService(intent);

        }
    }
}
