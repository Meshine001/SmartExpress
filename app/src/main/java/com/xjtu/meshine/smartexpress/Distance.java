package com.xjtu.meshine.smartexpress;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.BikingRouteLine;

/**
 * Created by Meshine on 16/12/27.
 */

public class Distance {
    private int i;
    private int j;
    private int distance;
    private boolean isSearched;

    private LatLng s;
    private LatLng d;

    private BikingRouteLine line;


    public Distance(int i, int j, int distance, boolean isSearched, LatLng s, LatLng d, BikingRouteLine line) {
        this.i = i;
        this.j = j;
        this.distance = distance;
        this.isSearched = isSearched;
        this.s = s;
        this.d = d;
        this.line = line;
    }

    public BikingRouteLine getLine() {
        return line;
    }

    public void setLine(BikingRouteLine line) {
        this.line = line;
    }

    public LatLng getS() {
        return s;
    }

    public void setS(LatLng s) {
        this.s = s;
    }

    public LatLng getD() {
        return d;
    }

    public void setD(LatLng d) {
        this.d = d;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public boolean isSearched() {
        return isSearched;
    }

    public void setSearched(boolean searched) {
        isSearched = searched;
    }

    @Override
    public String toString() {
        return "Distance{" +
                "i=" + i +
                ", j=" + j +
                ", distance=" + distance +
                ", isSearched=" + isSearched +
                ", s=" + s +
                ", d=" + d +
                ", line=" + line +
                '}';
    }
}
