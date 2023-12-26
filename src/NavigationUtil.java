import GraphEntity.*;

import java.util.List;

public class NavigationUtil {
    /**
     * 获取两点之间权值
     * @param graph
     * @param i
     * @param j
     * @return
     */
    public static double getEdgeWeight(MyGraph graph, int i, int j)
    {
        EdgeNode a = null;
        a=graph.point[i].firstArc;
        while(a!=null)
        {
            if(a.adjvex==j)
            {
                return a.value;
            }
            a=a.nextEdge;                                                    //实际上像是一种遍历链表的行为
        }
        return -1;
    }


    /**
     * 判断两点是否连通
     * @param graph
     * @param i
     * @param j
     * @return
     */
    public static boolean isConnected(MyGraph graph,int i,int j)
    {
        EdgeNode a = null;
        a=graph.point[i].firstArc;
        while(a!=null)
        {
            if(a.adjvex==j)
            {
                return true;
            }
            a=a.nextEdge;                                                    //实际上像是一种遍历链表的行为
        }
        return false;
    }

    /**
     * 获取一条链路的可靠性
     * @param graph
     * @param path
     * @return
     */
    public static double getPathReliability(MyGraph graph, List<Integer> path){
        double reliability = 1;
        for(int i = 0; i < path.size()-1; i++){
            reliability *= 1/NavigationUtil.getEdgeWeight(graph, path.get(i), path.get(i+1));
        }
        return reliability;
    }

    /**
     * 获取冗余路径组的可靠性
     * @param graph
     * @param temporaryProbability
     * @param permanentProbability
     * @param pathList
     * @return
     */
    public static double getRedundantPathReliability(double temporaryProbability, double permanentProbability, MyGraph graph, List<ShortestPath.MyPath> pathList){
        double temReliability = 1;
        double perReliability = 1;
        for(int i = 0; i <= pathList.size()-1; i++){
            temReliability *= (1-pathList.get(i).reliability);
        }
        temReliability = 1-temReliability;
        System.out.println(temReliability);
        return 0;
    }
}
