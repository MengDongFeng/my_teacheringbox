package com.example.teachingbox3;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by 孟东风 on {20190622}.
 */
public class TcpServer extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
