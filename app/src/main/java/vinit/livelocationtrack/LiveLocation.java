package vinit.livelocationtrack;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LiveLocation implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private static final String TAG = LiveLocation.class.getName();

    private Context context;

    private GoogleApiClient mLocationClient;
    private LocationRequest mLocationRequest;

    public static final int LOCATION_INTERVAL = 10000;
    public static final int FASTEST_LOCATION_INTERVAL = 5000;

    public LiveLocation(Context context) {
        this.context = context;
        this.mLocationRequest = new LocationRequest();

        this.mLocationClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest.setInterval(LOCATION_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_LOCATION_INTERVAL);


        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY;


        mLocationRequest.setPriority(priority);

    }

    public void startLiveDataCapturing(){
        mLocationClient.connect();
    }

    public boolean stopGettingLiveLocation(){
        if (mLocationClient != null) {
            mLocationClient.disconnect();
            return true;
        }
        mLocationClient = null;
        mLocationRequest = null;
        return false;
    }

    private void sendMessageToUI(Location location) {
        if (location == null) return;

        Intent intent = new Intent(LiveLocationBroadcast.ACTION_LOCATION_BROADCAST);
        intent.putExtra(LiveLocationBroadcast.EXTRA_LATITUDE, String.valueOf(location.getLatitude()));
        intent.putExtra(LiveLocationBroadcast.EXTRA_LONGITUDE, String.valueOf(location.getLongitude()));
        intent.putExtra(LiveLocationBroadcast.EXTRA_ACCURACY, String.valueOf(location.getAccuracy()));
        intent.putExtra(LiveLocationBroadcast.EXTRA_PROVIDER, String.valueOf(location.getProvider()));
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }

    @Override
    public void onConnected(@Nullable Bundle dataBundle) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);

        Log.d(TAG, "Connected to Google API");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) return;

        sendMessageToUI(location);
    }
}
