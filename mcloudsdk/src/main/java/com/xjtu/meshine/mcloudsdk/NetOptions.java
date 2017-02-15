package com.xjtu.meshine.mcloudsdk;

/**
 * SDK的网络配置参数
 * Created by Meshine on 17/1/3.
 */
public class NetOptions {
    /**
     * 云端网络端口
     */
    private int port;
    /**
     * 云端网络地址
     */
    private String ip;

    /**
     * 本程序运行在云端还是本地
     * 若是云端为true,若是本地为false
     */
    private boolean isServer = false;

    private int timeOut;

    public NetOptions(){

    }

    public NetOptions(int port, String ip) {
        this.port = port;
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isServer() {
        return isServer;
    }

    public void setServer(boolean server) {
        isServer = server;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }
}
