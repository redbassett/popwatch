package com.redbassett.popwatch.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PopwatchAuthenticatorService extends Service {
    private PopwatchAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new PopwatchAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
