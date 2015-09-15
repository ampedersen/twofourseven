package com.molamil.radio24syv;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RadioPlayerService extends Service {
    public RadioPlayerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
