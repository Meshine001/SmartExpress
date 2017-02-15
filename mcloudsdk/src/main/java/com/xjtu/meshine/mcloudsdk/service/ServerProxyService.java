package com.xjtu.meshine.mcloudsdk.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.xjtu.meshine.mcloudsdk.net.NetInfo;
import com.xjtu.meshine.mcloudsdk.net.ServerProxy;

/**
 * Created by Meshine on 16/12/8.
 */

public class ServerProxyService extends Service{
    private static final String TAG = ServerProxyService.class.getSimpleName();


    private ServerProxy proxy;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startProxy();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        stopProxy();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void startProxy(){
        Log.i(TAG,"Start proxy =========>");
       new Thread(new Runnable() {
           @Override
           public void run() {
               proxy = new ServerProxy(NetInfo.getOptions().getPort());
               proxy.startServer();
           }
       }).start();
    }

    public void stopProxy(){
        Log.i(TAG,"Stop proxy ========>");
        new Thread(new Runnable() {
            @Override
            public void run() {
                proxy.stopServer();
            }
        }).start();
    }

    public class ProxyBinder extends Binder{
        public ServerProxyService getService(){
            return ServerProxyService.this;
        }
    }

    private ProxyBinder binder = new ProxyBinder();
}
