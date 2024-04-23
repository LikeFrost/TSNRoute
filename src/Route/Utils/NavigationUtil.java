package Route.Utils;

import Route.GraphEntity.*;
import Route.RedundantPath;
import Route.Utils.PathUtils.CutSetTheorem;

import java.util.*;

import static java.lang.Math.sqrt;

public class NavigationUtil {
    public static double totalDoor = 0;
    public static boolean isUpdateOF = false;

    /**
     * 获取两点之间权值
     *
     * @param graph
     * @param i
     * @param j
     * @return
     */
    public static double getEdgeWeight(MyGraph graph, int i, int j) {
        return graph.graph[i][j].weight;
    }

    /**
     * 获取两点之间可靠度
     *
     * @param graph
     * @param i
     * @param j
     * @return
     */
    public static double getEdgeReliability(MyGraph graph, int i, int j) {
        return 1 / graph.graph[i][j].weight;
    }

    /**
     * 判断两点是否连通
     *
     * @param graph
     * @param i
     * @param j
     * @return
     */
    public static boolean isConnected(MyGraph graph, int i, int j) {
        return graph.graph[i][j].weight < Double.MAX_VALUE;
    }

    /**
     * 获取一条链路的可靠性
     *
     * @param graph
     * @param path
     * @return
     */
    public static double getPathReliability(MyGraph graph, List<Integer> path) {
        double reliability = 1;
        for (int i = 0; i < path.size() - 1; i++) {
            reliability *= 1 / NavigationUtil.getEdgeWeight(graph, path.get(i), path.get(i + 1));
        }
        return reliability;
    }

    /**
     * 获取冗余路径组的可靠性
     *
     * @param graph
     * @param temporaryProbability
     * @param permanentProbability
     * @param pathList
     * @return
     */
    public static double getRedundantPathReliability(double temporaryProbability, double permanentProbability, MyGraph graph, List<MyPath> pathList) {
        double temReliability = 1;
        double perReliability = 1;

        //瞬时故障
        for (int i = 0; i <= pathList.size() - 1; i++) {
            temReliability *= (1 - pathList.get(i).reliability);
        }
        temReliability = 1 - temReliability;

        //永久故障
        List<List<Integer>> paths = new ArrayList<>();
        for (MyPath path : pathList) {
            paths.add(path.pathEdge);
        }
        perReliability = CutSetTheorem.getCombinationsReliability(paths, graph);

        return temporaryProbability * temReliability + permanentProbability * perReliability;
    }

    //生成max和min之间的随机数
    public static double generateRandomDoubleNumber(double min, double max) {
        Random random = new Random();
        double randomNumber = random.nextDouble() * (max - min) + min;
        return randomNumber;
    }

    public static int generateRandomIntNumber(int min, int max) {
        Random random = new Random();
        int randomNumber = random.nextInt(max - min) + min;
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

    public static List<Flow> generateFlow(int count, int min, int max, double minReliability, double maxReliability) {
        List<Flow> flowList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int[] points = generateRandomNumbers(min, max);
            List<RedundantPath> redundantPath = new ArrayList<>();
            flowList.add(new Flow(points[0], points[1], 1000, 1, generateRandomIntNumber(1, 4) * 100, generateRandomDoubleNumber(minReliability, maxReliability), redundantPath));
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

    public static int getHyperPeriod(List<Flow> flowList) {
        int hyperPeriod = 1;
        for (Flow flow : flowList) {
            hyperPeriod = calculateLCM(hyperPeriod, flow.period);
        }
        return hyperPeriod;
    }

    private static int calculateLCM(int i, int j) {
        return i * j / calculateGCD(i, j);
    }

    private static int calculateGCD(int i, int j) {
        int x = i % j;
        while (x != 0) {
            i = j;
            j = x;
            x = i % j;
        }
        return j;
    }

    private static int findNextTarget(int[] array, int startIndex, int target) {
        for (int index = startIndex; index < array.length; index++)
            if (array[index] == target)
                return index;
        return array.length;
    }

    //结果数组转化为业务链接
    public static List<Connection> result2Connections(int[][][] result) {
        List<Connection> connections = new ArrayList<>();
        for (int srcIndex = 0; srcIndex < result.length; srcIndex++) {
            for (int dstIndex = 0; dstIndex < result[0].length; dstIndex++) {
                List<Timeslot> timeslots;
                int[] array = result[srcIndex][dstIndex];
                // 找到第一个1, 代表时隙的起点
                int startIndex = findNextTarget(array, 0, 1);
                // 如果能够找到1, 证明这条链路有时隙分配
                if (startIndex < array.length) {
                    timeslots = new ArrayList<>();
                    while (startIndex < array.length) {
                        // 找到下一个0, 代表时隙的结尾
                        int endIndex = findNextTarget(array, startIndex, 0);
                        // 构建时隙结构体
                        Timeslot timeslot = new Timeslot();
                        timeslot.startTime = startIndex;
                        timeslot.duration = endIndex - startIndex;
                        timeslots.add(timeslot);
                        // 继续找下一个1, 即下一个时隙的起点
                        startIndex = findNextTarget(array, endIndex, 1);
                    }
                    // 构建业务连接结构体
                    Connection connection = new Connection();
                    connection.srcNodeId = srcIndex + "";
                    connection.dstNodeId = dstIndex + "";
                    connection.timeslot = timeslots;
                    connections.add(connection);
                }
            }
        }
        Collections.sort(connections, new Comparator<Connection>() {
            @Override
            public int compare(Connection c1, Connection c2) {
                // 比较 Connection 对象的 timeslot 数组的第一个元素的 startTime
                int startTime1 = c1.getTimeslot().get(0).startTime;
                int startTime2 = c2.getTimeslot().get(0).startTime;
                return Integer.compare(startTime1, startTime2);
            }
        });

        return connections;
    }

    public static List<Flow> sortPathByScore(List<Flow> flowList, int e, int hyperPeriod) {
        double n = 0; //每组冗余路径的平均条数
        double _HC = e;
        double _OF = 0;
        double _CO = 0;
        for (Flow flow : flowList) {
            n += flow.redundantPath.size();
        }
        n = n / flowList.size();
        _CO = flowList.size() * n;
        for (Flow flow : flowList) {
            _OF += hyperPeriod / flow.period;
        }
        _OF = _OF * n;
        for (Flow flow : flowList) {
            for (RedundantPath redundantPath : flow.redundantPath) {
                redundantPath.WT = getPathScore(redundantPath, _HC, _OF, _CO);
            }
            Collections.sort(flow.redundantPath, new Comparator<RedundantPath>() {
                @Override
                public int compare(RedundantPath o1, RedundantPath o2) {
                    return Double.compare(o2.WT, o1.WT);
                }
            });
        }
        return flowList;
    }

    public static double getPathScore(RedundantPath redundantPath, double _HC, double _OF, double _CO) {
        double w1 = 1;
        double w2 = 1;
        double w3 = 1;
        return w1 * redundantPath.HC / (1 + _HC) + w2 * redundantPath.OF / (1 + _OF) + w3 * redundantPath.CO / (1 + _CO);
    }

    public static int[][] getPathUse(int[][][] slotUse) {
        int[][] pathUse = new int[slotUse.length][slotUse[0].length];
        for (int i = 0; i < slotUse.length; i++) {
            for (int j = 0; j < slotUse[i].length; j++) {
                for (int k = 0; k < slotUse[i][j].length; k++) {
                    pathUse[i][j] += slotUse[i][j][k];
                }
            }
        }
        return pathUse;
    }

    public static List<Flow> updatePathOF(List<Flow> flowList, int[][][] slotUse) {
        int[][] pathUse = getPathUse(slotUse);

        for (Flow flow : flowList) {
            for (RedundantPath redundantPath : flow.redundantPath) {
                for (int i = 0; i < redundantPath.redundantPath.size(); i++) {
                    for (int j = 0; j < redundantPath.redundantPath.get(i).path.size() - 1; j++) {
                        int src = redundantPath.redundantPath.get(i).path.get(j);
                        int dst = redundantPath.redundantPath.get(i).path.get(j + 1);
                        redundantPath.OF = pathUse[src][dst];
                    }
                }
            }
        }
        return flowList;
    }

    public static double calcOF2(MyGraph myGraph, int[][][] slotUse){
        int[][] pathUse = getPathUse(slotUse);
        double OF2 = 0; //方差
        double avg = 0;
        for(Edge edge : myGraph.edge){
            avg += pathUse[edge.start][edge.end];
        }
        avg = avg / myGraph.edge.length;
        for(Edge edge : myGraph.edge){
            OF2 += Math.pow(pathUse[edge.start][edge.end] - avg, 2);
        }
        OF2 = sqrt(OF2 / myGraph.edge.length);
        return OF2;
    }

    public static List<Flow> calcDoor(List<Flow> flowList, int hyperPeriod, int[][][] slotUse) {
        if(!isUpdateOF){
            if (totalDoor == 0) {
                int count = 0;
                for (Flow flow : flowList) {
                    int path = 0;
                    for (int i = 0; i < flow.redundantPath.get(0).redundantPath.size(); i++) {
                        path += flow.redundantPath.get(0).redundantPath.get(i).path.size();
                    }
                    count += hyperPeriod / flow.period * path;
                }
                totalDoor = count / 106; //106条边
            }
            int[][] pathUse = getPathUse(slotUse);
            for (int i = 0; i < pathUse.length; i++) {
                for (int j = i + i; j < pathUse[0].length; j++) {
                    if (totalDoor - pathUse[i][j] <= 0) {
                        isUpdateOF = true;
                        return updatePathOF(flowList, slotUse);
                    }
                }
            }
            return flowList;
        }
        else return updatePathOF(flowList, slotUse);
    }
}
