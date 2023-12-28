package Route.Utils;

import Route.GraphEntity.*;
import Route.ShortestPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        return graph.graph[i][j].weight;
    }

    /**
     * 获取两点之间可靠度
     * @param graph
     * @param i
     * @param j
     * @return
     */
    public static double getEdgeReliability(MyGraph graph, int i, int j)
    {
        return 1/graph.graph[i][j].weight;
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
        return graph.graph[i][j].weight < Double.MAX_VALUE;
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

        //瞬时故障
        for(int i = 0; i <= pathList.size()-1; i++){
            temReliability *= (1-pathList.get(i).reliability);
        }
        temReliability = 1-temReliability;

        //永久故障
        List<List<Integer>> paths = new ArrayList<>();
        for(ShortestPath.MyPath path : pathList){
            paths.add(path.pathEdge);
        }
        Set<Set<Integer>> minimalCutSets = MinimalCutSetsFinder.findMinimalCutSets(paths);
        List<List<Integer>> minimalCutLists = convertSetOfSetsToList(minimalCutSets);
        perReliability = CutSetTheorem.getCombinationsReliability(minimalCutLists,graph);

        return temporaryProbability*temReliability + permanentProbability*perReliability;
    }
    //将set转化为list
    private static List<List<Integer>> convertSetOfSetsToList(Set<Set<Integer>> setOfSets) {
        List<List<Integer>> resultList = new ArrayList<>();

        for (Set<Integer> set : setOfSets) {
            List<Integer> list = new ArrayList<>(set);
            resultList.add(list);
        }

        return resultList;
    }
}
