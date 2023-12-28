package Route.Utils;

import Route.GraphEntity.*;
import Route.RedundantPath;
import Route.Utils.PathUtils.CutSetTheorem;
import Route.Utils.PathUtils.ShortestPath;

import java.util.*;

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
        perReliability = CutSetTheorem.getCombinationsReliability(paths,graph);

        return temporaryProbability*temReliability + permanentProbability*perReliability;
    }
    //生成max和min之间的随机数
    public static double generateRandomNumber(double min, double max) {
        Random random = new Random();
        double randomNumber = random.nextDouble() * (max - min) + min;
        return randomNumber;
    }

    //随机生成min到max的一对不相同的值
    private static int[] generateRandomNumbers(int min, int max) {
        if (min == max) {
            throw new IllegalArgumentException("The two numbers must be different.");
        }

        Random random = new Random();
        int random1 = random.nextInt(max - min - 1) + min + 1;
        int random2;
        do {
            random2 = random.nextInt(max - min - 1) + min + 1;
        } while (random2 == random1);

        int[] result = {random1, random2};
        return result;
    }

    public static List<Flow> generateFlow(int count, int min, int max, double minReliability, double maxReliability){
        List<Flow> flowList = new ArrayList<>();
        for (int i = 0; i < count;i++){
            int [] points = generateRandomNumbers(min,max);
            List<RedundantPath> redundantPath = new ArrayList<>();
            flowList.add(new Flow(points[0],points[1],1,generateRandomNumber(minReliability,maxReliability),redundantPath));
        }
        return flowList;
    }

    //将数组转化为List
    public static List<List<Double>> arrayToList(double[][] array) {
        List<List<Double>> list = new ArrayList<>();

        for (double[] row : array) {
            List<Double> sublist = new ArrayList<>();
            for (double value : row) {
                sublist.add(value);
            }
            list.add(sublist);
        }
        return list;
    }
}
