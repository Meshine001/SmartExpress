package com.xjtu.meshine.mcloudsdk.component;

/**
 * 卸载分析器
 * Created by Meshine on 16/12/14.
 */

public class Profiler {

    private static Profiler instance = null;


    public static Profiler getInstance(){

        if(instance == null){
            synchronized (Profiler.class) {
                if(instance == null){
                    instance = new Profiler();
                }
            }
        }
        return instance;
    }

}
