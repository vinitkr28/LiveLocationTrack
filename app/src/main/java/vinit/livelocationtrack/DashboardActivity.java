package vinit.livelocationtrack;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.snackbar.Snackbar;

public class DashboardActivity extends LiveLocationParentActivity implements LiveLocationBroadcast.LiveLocationUpdate{
    private static final String TAG = DashboardActivity.class.getName();

    private Context context;
    private TextView showLiveLocationData;
    private Button liveLocationData;

    private boolean mAlreadyStartedService = false;

    private LiveLocationBroadcast mLiveLocationBroadcast;

    private boolean isLiveLocationServiceRunning = false;

    private Location mLocation;

    private static final String LIVE_DATA_COLOR = "#000099", STOP_DATA_COLOR = "#FF0000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        showLiveLocationData = findViewById(R.id.showLiveLocationData);
        liveLocationData = findViewById(R.id.liveLocationData);
        context = this;

        mLiveLocationBroadcast = new LiveLocationBroadcast(this);
    }

    @Override
    public void onResume() {
        super.onResume();
//        checkGoogleService();
//
        LocalBroadcastManager.getInstance(this).registerReceiver(mLiveLocationBroadcast, new IntentFilter(LiveLocationBroadcast.ACTION_LOCATION_BROADCAST));
        if (isLiveLocationRunning(LiveLocationUpdateService.class)) {
            liveLocationData.setText(R.string.stop_live_location);
            isLiveLocationServiceRunning = true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

//        if (mLiveLocationBroadcast != null)
//            unregisterReceiver(mLiveLocationBroadcast);
    }

    private void startLocationTracking() {
        Log.d(TAG, "startLocationTracking");

        if (!mAlreadyStartedService && showLiveLocationData != null) {

            showLiveLocationData.setText(R.string.msg_location_service_started);

            Intent intent = new Intent(this, LiveLocationUpdateService.class);
            startService(intent);

            mAlreadyStartedService = true;
            isLiveLocationServiceRunning = true;
            liveLocationData.setText(R.string.stop_live_location);
        }
    }

    @Override
    public void onLiveLocationValueUpdated(Location location) {
        if (location == null) return;
        mLocation = location;
        buildTextToDisplay(LIVE_DATA_COLOR);
    }

    private void buildTextToDisplay(String color){
        if (mLocation == null) return;
        showLiveLocationData.setText("");
        showLiveLocationData.append(Html.fromHtml("<h2 style=\"line-height:16px\">LATITUDE:&nbsp;&nbsp;<font color='" + color +"'>" + mLocation.getLatitude() + "</font></h2>", Html.FROM_HTML_MODE_COMPACT));
        showLiveLocationData.append(Html.fromHtml("<h2 style=\"line-height:134%\">LONGITUDE:&nbsp;&nbsp;<font color='" + color +"'>" + mLocation.getLongitude() + "</font></h2>", Html.FROM_HTML_MODE_COMPACT));
        showLiveLocationData.append(Html.fromHtml("<h2 style=\"line-height:1.6\">ACCURACY:&nbsp;&nbsp;<font color='" + color +"'>" + mLocation.getAccuracy() + "</font></h2>", Html.FROM_HTML_MODE_COMPACT));
        showLiveLocationData.append(Html.fromHtml("<h2 style=\"line-height:16px\">PROVIDER:&nbsp;&nbsp;<font color='" + color +"'>" + mLocation.getProvider() + "</font></h2>", Html.FROM_HTML_MODE_COMPACT));
    }

    public void liveLocationDataClicked(View view) {
        if (isLiveLocationServiceRunning) {
            isLiveLocationServiceRunning = false;
            mAlreadyStartedService = false;
            liveLocationData.setText(R.string.start_live_location);
            Intent intent = new Intent(this, LiveLocationUpdateService.class);
            stopService(intent);
            buildTextToDisplay(STOP_DATA_COLOR);
        } else {
            setContext(context);
            checkGoogleService();
            if (allPermissionGranted) startLocationTracking();
        }

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

    public void openGoogleMap(View view) {
        setContext(context);
        checkGoogleService();
        if (allPermissionGranted)
            startActivity(new Intent(context, MapsActivity.class));
    }
}
