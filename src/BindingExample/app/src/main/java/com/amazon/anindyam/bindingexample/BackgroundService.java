package com.amazon.anindyam.bindingexample;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

import java.util.Random;

public class BackgroundService extends Service {
    private static final String TAG = "BackgroundService";
    // Binder given to clients
    private final IBinder binder = new LocalBinder();
    // Random number generator
    private final Random mGenerator = new Random();
    // manages bound count
    private int mBoundCount;

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        BackgroundService getService() {
            Log.i("BG Service Binder", "CallingUid: "+ getCallingUid() + ", CallingPid: " + getCallingPid() + ", Me: " + Process.myUid());
            // Return this instance of LocalService so clients can call public methods
            return BackgroundService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBoundCount = 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Starting BG service");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Destroy BG service");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        mBoundCount++;
        Log.i(TAG, "Binding, Bound clients: " + mBoundCount);
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mBoundCount--;
        Log.i(TAG, "Unbinding, Bound clients: " + mBoundCount);
        super.onUnbind(intent);
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        mBoundCount++;
        Log.i(TAG, "Rebinding, Bound clients: " + mBoundCount);
        super.onRebind(intent);
    }


    /** method for clients */
    public int getRandomNumber() {
        return mGenerator.nextInt(100);
    }

    public int getBoundCount() {
        return mBoundCount;
    }

    public void stopService() {
        Log.i(TAG, "stopping BG service");
        stopSelf();
        Log.i(TAG, "stopped BG service");

    }
}
