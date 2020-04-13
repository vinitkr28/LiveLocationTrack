package vinit.livelocationtrack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.text.Html;
import android.util.Log;

public class LiveLocationBroadcast extends BroadcastReceiver {
    private static final String TAG = LiveLocationBroadcast.class.getName();

    public static final String ACTION_LOCATION_BROADCAST = "vinit.livelocationtrack" + ".locationBroadcast";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";
    public static final String EXTRA_ACCURACY = "extra_accuracy";
    public static final String EXTRA_PROVIDER = "extra_provider";

    private LiveLocationUpdate mLiveLocationUpdate;

    public LiveLocationBroadcast(LiveLocationUpdate liveLocationUpdate) {
        this.mLiveLocationUpdate = liveLocationUpdate;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_LOCATION_BROADCAST)) {
            if (intent == null || intent.getExtras() == null) return;

            Location location = new Location(LocationManager.GPS_PROVIDER);

            if (intent.getExtras().containsKey(EXTRA_LATITUDE)) {
                Log.d(TAG, "LATITUDE: " + intent.getStringExtra(EXTRA_LATITUDE));
//                location.setLatitude(intent.getDoubleExtra(EXTRA_LATITUDE, 0.0));
                location.setLatitude(Double.valueOf(intent.getStringExtra(EXTRA_LATITUDE)));
            }
            if (intent.getExtras().containsKey(EXTRA_LONGITUDE)) {
                Log.d(TAG, "LONGITUDE: " + intent.getStringExtra(EXTRA_LONGITUDE));
                location.setLongitude(Double.valueOf(intent.getStringExtra(EXTRA_LONGITUDE)));
            }
            if (intent.getExtras().containsKey(EXTRA_ACCURACY)) {
                Log.d(TAG, "ACCURACY: " + intent.getStringExtra(EXTRA_ACCURACY));
                location.setAccuracy(Float.valueOf(intent.getStringExtra(EXTRA_ACCURACY)));
            }
            if (intent.getExtras().containsKey(EXTRA_PROVIDER)) {
                Log.d(TAG, "PROVIDER: " + intent.getStringExtra(EXTRA_PROVIDER));
                location.setProvider(intent.getStringExtra(EXTRA_PROVIDER));
            }

            if (location != null && mLiveLocationUpdate != null)
                mLiveLocationUpdate.onLiveLocationValueUpdated(location);
        }
    }

    public interface LiveLocationUpdate{
        void onLiveLocationValueUpdated(Location location);
    }
}
