package com.xjtu.meshine.mcloudsdk;

import android.content.Context;

import com.xjtu.meshine.mcloudsdk.component.Application;
import com.xjtu.meshine.mcloudsdk.net.NetInfo;

/**
 * 框架主程序
 * Created by Meshine on 17/1/3.
 */

public class MCloudSDK {

    /**
     * 初始化框架
     * @param context
     */
    public static void initialize(Context context){
        Application.initialize(context);
    }

    public static void setNetOptions(NetOptions options){
        Application.setNetOptions(options);
        if (NetInfo.getOptions().isServer()){
            startServerPoxy();
        }
    }

    public static void startServerPoxy(){
        Application.startServerProxyService();
    }

}
