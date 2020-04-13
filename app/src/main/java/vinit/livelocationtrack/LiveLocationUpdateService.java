package vinit.livelocationtrack;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import androidx.annotation.NonNull;

public class LiveLocationUpdateService extends Service {
    private static final String TAG = LiveLocationUpdateService.class.getName();

    private LiveLocation mLiveLocation;

    public LiveLocationUpdateService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mLiveLocation = new LiveLocation(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mLiveLocation.startLiveDataCapturing();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (mLiveLocation != null)
            mLiveLocation.stopGettingLiveLocation();
        Log.d(TAG, "LiveLocationUpdateService:onDestroy");
        super.onDestroy();
    }
}
