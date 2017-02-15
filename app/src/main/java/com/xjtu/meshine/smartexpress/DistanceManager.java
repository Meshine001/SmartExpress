package com.xjtu.meshine.smartexpress;

import android.util.Log;

import com.baidu.mapapi.search.core.RouteNode;
import com.baidu.mapapi.search.route.BikingRouteLine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Meshine on 16/12/27.
 */

public class DistanceManager {
    private static final String TAG = "DistanceManager";

    private static DistanceManager instance = null;

    private Queue q;
    private int[][] matrix;
    private List<Distance> distances;

    public DistanceManager(){
        q = new LinkedList();
        distances = new ArrayList<>();
    }

    public static DistanceManager getInstance(){
        if (instance == null){

                if (instance== null){
                    instance = new DistanceManager();
                }

        }
        return instance;
    }

    public void reset(){
        q.clear();
        matrix = null;
        distances.clear();
    }

    public BikingRouteLine getBikingLine(int[] plan){
        List<BikingRouteLine.BikingStep> steps = new ArrayList<>();

        Distance source = null;

        int[] p = new int[plan.length+1];
        for (int i=0;i<p.length;i++){
            if (i == plan.length){
                p[i] = plan[0];
                break;
            }
            p[i] = plan[i];
        }

        for (int i=0;i<p.length-1;i++){
            int s = p[i];
            int d = p[i+1];
            for (Distance distance:distances){
                if (s == distance.getI() && d == distance.getJ()){
                    steps.addAll(distance.getLine().getAllStep());
                    if (s == p [0]){
                        source = distance;
                    }
                }
            }
        }

        BikingRouteLine line = source.getLine();
        line.setStarting(RouteNode.location(source.getS()));
        line.setSteps(steps);
        line.setTerminal(RouteNode.location(source.getS()));

        return line;

    }


    public void setMatrix(int[][] matrix){
        this.matrix = matrix;
    }

    public Distance getUnsearchedDis(){
        return (Distance) q.poll();
    }


    public boolean isUnsearchedEmpty(){
        return q.isEmpty();
    }

    public void setInstance(Distance distance){
        distances.add(distance);
        int i = distance.getI();
        int j = distance.getJ();
        matrix[i][j] = distance.getDistance();
    }

    public void addDistance(Distance distance){
        Log.i(TAG,"add：" + distance.toString());
        if (distance.isSearched()){
            setInstance(distance);
        }else {
            q.add(distance);
        }
    }

    public int getUsearchedCount(){
        return q.size();
    }

    public void showMatrix(){
        int len = matrix[0].length;
        StringBuffer sb = new StringBuffer();
        for (int i=0;i<len;i++){
            for (int j = 0;j<len;j++){
                sb.append(matrix[i][j]+"  ");
            }
            Log.i(TAG,sb.toString());
            sb = new StringBuffer();
        }
    }
}
