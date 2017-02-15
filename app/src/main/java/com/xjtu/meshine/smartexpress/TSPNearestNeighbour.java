package com.xjtu.meshine.smartexpress;

import com.xjtu.meshine.mcloudsdk.annotation.Cloud;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Meshine on 16/12/30.
 */

public class TSPNearestNeighbour implements Serializable
{

    private static final long serialVersionUID = 0L;

    private int numberOfNodes;
    private Stack<Integer> stack;

    public TSPNearestNeighbour()
    {
        stack = new Stack<Integer>();
    }

    @Cloud
    public List<Integer> tsp(int adjacencyMatrix[][], int v[])
    {
        int minCost = 0;
        int index = 0;

        List<Integer> que = new ArrayList<>();

        numberOfNodes = adjacencyMatrix[1].length - 1;
        int[] visited = new int[numberOfNodes + 1];
        visited[1] = 1;
        stack.push(1);
        int element, dst = 0, i;
        int min = Integer.MAX_VALUE;
        boolean minFlag = false;
//        System.out.print(1 + "\t");

        que.add(1);
        v[index] = 1;
        index++;

        while (!stack.isEmpty())
        {
            element = stack.peek();
            i = 1;
            min = Integer.MAX_VALUE;
            while (i <= numberOfNodes)
            {
                if (adjacencyMatrix[element][i] > 1 && visited[i] == 0)
                {
                    if (min > adjacencyMatrix[element][i])
                    {
                        min = adjacencyMatrix[element][i];
                        dst = i;
                        minFlag = true;
                    }
                }
                i++;
            }
            if (minFlag)
            {
                visited[dst] = 1;
                stack.push(dst);
//                System.out.print(dst + "\t");
                que.add(dst);
                v[index] = dst;
                index++;
                minCost += adjacencyMatrix[v[index-2]][v[index-1]];
                if(index == v.length){
                    minCost += adjacencyMatrix[v[index-1]][v[0]];
                }
                minFlag = false;
                continue;
            }
            stack.pop();
        }

        que.add(minCost);


        return que;
    }

//    public static void main(String... arg)
//    {
//        int number_of_nodes = 4;
//        int adjacency_matrix[][] = {
//                {0,0,0,0,0},
//                {0,0,30,6,4},
//                {0,30,0,5,10},
//                {0,6,5,0,20},
//                {0,4,10,20,0}
//        };
//
//
//        TSPNearestNeighbour tspNearestNeighbour = new TSPNearestNeighbour();
//        int v[] = new int[number_of_nodes];
//        int minCost = tspNearestNeighbour.tsp(adjacency_matrix,v);
//        System.out.println("Mini cost is:"+minCost);
//        System.out.println("the citys are visited as follows");
//        for(int i:v){
//            System.out.print(i+"\t");
//        }
//
//
//    }
}