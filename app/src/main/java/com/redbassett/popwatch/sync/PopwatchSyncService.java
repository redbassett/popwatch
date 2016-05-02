package com.redbassett.popwatch.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PopwatchSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static PopwatchSyncAdapter sPopwatchSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sPopwatchSyncAdapter == null) {
                sPopwatchSyncAdapter = new PopwatchSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sPopwatchSyncAdapter.getSyncAdapterBinder();
    }
}
