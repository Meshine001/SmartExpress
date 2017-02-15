package com.xjtu.meshine.smartexpress;


import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 旅行售货员问题--优先队列式分支限界法
 *
 */
public class BBTSP {
    int[][] a;//图G的邻接矩阵
    public BBTSP(int[][] a){
        this.a=a;
    }
    public static class HeapNode implements Comparable{
        Integer lcost;//子树费用的下界
        Integer cc;//当前费用
        Integer rcost;//x[s:n-1]中顶点最小出边费用和
        int s;//根节点到当前节点的路径为x[0:s]
        int[] x;//需要进一步搜索的顶点是x[s+1:n-1]

        //构造方法
        public HeapNode(Integer lc,Integer ccc,Integer rc,int ss,int[] xx){
            lcost=lc;
            cc=ccc;
            s=ss;
            x=xx;
        }
        public int compareTo(Object x){
            float xlc=((HeapNode) x).lcost;
            if(lcost<xlc) return -1;
            if(lcost==xlc) return 0;
            return 1;
        }
    }

    public List<Integer> bbTsp(int[] v){
        List<Integer> result = new ArrayList<>();

        int n=v.length-1;//节点数
        LinkedList<HeapNode> heap=new LinkedList<HeapNode>();
        //minOut[i]=i的最小出边费用
        Integer[] minOut=new Integer[n+1];
        Integer minSum=0;//最小出边费用和
        for(int i=1;i<=n;i++){//针对每个节点，找到最小出边
            //计算minOut[i]和minSum
            Integer min= Integer.MAX_VALUE;
            for(int j=1;j<=n;j++){
                if(a[i][j]<Integer.MAX_VALUE&&a[i][j]<min)
                    min=a[i][j];
            }
            if(min==Integer.MAX_VALUE){
                result.add(Integer.MAX_VALUE);
                return result;
            }
            minOut[i]=min;
            minSum+=min;
        }

        //初始化
        int[] x=new int[n];
        for(int i=0;i<n;i++)
            x[i]=i+1;
        HeapNode enode=new HeapNode(0,0,minSum,0,x);
        Integer bestc=Integer.MAX_VALUE;

        //搜索排列空间树
        while(enode!=null&&enode.s<n-1){
            //非叶节点
            x=enode.x;
            if(enode.s==n-2){
                //当前扩展结点是叶节点的父节点
                //再加两条边构成回路
                //所构成回路是否优于当前最优解
                if(a[x[n-2]][x[n-1]]!=-1&&a[x[n-1]][1]!=-1&&enode.cc+a[x[n-2]][x[n-1]]+a[x[n-1]][1]<bestc){
                    //找到费用更小的回路
                    bestc=enode.cc+a[x[n-2]][x[n-1]]+a[x[n-1]][1];
                    enode.cc=bestc;
                    enode.lcost=bestc;
                    enode.s++;
                    heap.add(enode);
                    Collections.sort(heap);
                }
            }else{//内部结点
                //产生当前扩展结点的儿子结点
                for(int i=enode.s+1;i<n;i++){
                    if(a[x[enode.s]][x[i]]!=-1){
                        //可行儿子结点
                        Integer cc=enode.cc+a[x[enode.s]][x[i]];
                        Integer rcost=enode.rcost=minOut[x[enode.s]];
                        Integer b=cc+rcost;//下界
                        if(b<bestc){
                            //子树可能含有最优解，结点插入最小堆
                            int[] xx=new int[n];
                            for(int j=0;j<n;j++)
                                xx[j]=x[j];
                            xx[enode.s+1]=x[i];
                            xx[i]=x[enode.s+1];
                            HeapNode node=new HeapNode(b,cc,rcost,enode.s+1,xx);
                            heap.add(node);
                            Collections.sort(heap);
                        }
                    }
                }

            }

            //取下一个扩展结点
            enode=heap.poll();
        }
        //将最优解复制到v[1...n]
        for(int i=0;i<n;i++){
            result.add(x[i]);
        }
        result.add(bestc);
        return result;
    }
    public static void main(String[] args) {
//        int[][] a={{0,0,0,0,0,0},{0,-1,5,-1,7,9},{0,5,-1,10,3,6},{0,-1,10,-1,8,-1},{0,7,3,8,-1,4},{0,9,6,-1,4,-1}};
        int[][] a = {
                {0,0,0,0},
                {0,-1,23,22},
                {0,1,-1,22},
                {0,1,2,-1}
        };
        int n = 3;
        BBTSP b = new BBTSP(a);
        int[] v = new int[n+1];
        List<Integer> result = b.bbTsp(v);
        System.out.println(result);
    }
}