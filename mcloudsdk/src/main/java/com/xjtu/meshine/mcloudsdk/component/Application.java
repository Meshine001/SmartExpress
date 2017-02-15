package com.xjtu.meshine.mcloudsdk.component;

import android.content.Context;
import android.content.Intent;

import com.xjtu.meshine.mcloudsdk.NetOptions;
import com.xjtu.meshine.mcloudsdk.net.NetInfo;
import com.xjtu.meshine.mcloudsdk.service.ServerProxyService;

/**
 * Created by Meshine on 17/1/3.
 */

public class Application {

    private static Context context;

    public static void initialize(Context context){
        Application.context = context;
        //初始化网络设置
        NetOptions netOptions = new NetOptions();
        netOptions.setPort(NetInfo.DEFAULT_PORT);
        netOptions.setIp(NetInfo.DEFAULT_IP);
        netOptions.setServer(NetInfo.DEFAULT_IS_SERVER);
        NetInfo.setOptions(netOptions);

        //初始化数据库
        DBManager.getInstance().initialize(context);
    }

    public static void setNetOptions(NetOptions options){
        NetInfo.setOptions(options);
    }

    public static void startServerProxyService(){
        if (NetInfo.getOptions().isServer()){
            Intent intent = new Intent(context,ServerProxyService.class);
            context.startService(intent);
        }
    }



}
