package com.xjtu.meshine.smartexpress;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.xjtu.meshine.mcloudsdk.MCloudSDK;
import com.xjtu.meshine.mcloudsdk.NetOptions;

/**
 * Created by Meshine on 16/12/22.
 */

public class MyApplication extends Application {

    public LocationService locationService;

    @Override
    public void onCreate() {
        super.onCreate();

        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(getApplicationContext());

        SDKInitializer.initialize(getApplicationContext());

        /**
         * 初始化MCloudSDK
         */
        MCloudSDK.initialize(getApplicationContext());

        /**
         * 初始化计算卸载的网络参数
         */
        String serverIp = "192.168.1.176";
        int serverPort = 4444;
        boolean isServer = false;
//        boolean isServer = true;
        NetOptions options = new NetOptions();
        options.setIp(serverIp);
        options.setPort(serverPort);
        options.setServer(isServer);
        options.setTimeOut(1000000);

        MCloudSDK.setNetOptions(options);

    }
}
