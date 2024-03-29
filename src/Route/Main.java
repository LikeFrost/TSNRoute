package Route;

import Route.GraphEntity.Connection;
import Route.GraphEntity.Flow;
import Route.GraphEntity.MyGraph;
import Route.Utils.NavigationUtil;
import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    static MyGraph g;
    static List<Flow> flowList = new ArrayList<>();

    //初始化函数，生成边、流、图、冗余路径、超周期
    public static void init() {
        int n = 45, e = 106;
//        起点、终点、可靠度
        double data[][] = {{0, 13}, {0, 14}, {0, 15}, {0, 4}, {0, 5},
                {1, 16}, {1, 17}, {1, 4}, {1, 5},
                {2, 18}, {2, 19}, {2, 20}, {2, 21}, {2, 22}, {2, 4}, {2, 5},
                {3, 23}, {3, 24}, {3, 4}, {3, 5},
                {4, 25}, {4, 6}, {4, 7}, {4, 10},
                {5, 26}, {5, 27}, {5, 8}, {5, 9}, {5, 10},
                {6, 28}, {6, 29}, {6, 7}, {6, 11},
                {7, 30}, {7, 31}, {7, 32}, {7, 10}, {7, 11},
                {8, 33}, {8, 34}, {8, 35}, {8, 9}, {8, 10}, {8, 12},
                {9, 36}, {9, 37}, {9, 12},
                {10, 38}, {10, 39},
                {11, 40}, {11, 41},
                {12, 42}, {12, 43},
                {13, 0}, {14, 0}, {15, 0}, {4, 0}, {5, 0},
                {16, 1}, {17, 1}, {4, 1}, {5, 1},
                {18, 2}, {19, 2}, {20, 2}, {21, 2}, {22, 2}, {4, 2}, {5, 2},
                {23, 3}, {24, 3}, {4, 3}, {5, 3},
                {25, 4}, {6, 4}, {7, 4}, {10, 4},
                {26, 5}, {27, 5}, {8, 5}, {9, 5}, {10, 5},
                {28, 6}, {29, 6}, {7, 6}, {11, 6},
                {30, 7}, {31, 7}, {32, 7}, {10, 7}, {11, 7},
                {33, 8}, {34, 8}, {35, 8}, {9, 8}, {10, 8}, {12, 8},
                {36, 9}, {37, 9}, {12, 9},
                {38, 10}, {39, 10},
                {40, 11}, {41, 11},
                {42, 12}, {43, 12}
        };
//        int n = 4, e = 6;
//        double data [][] = {{0,1},{1,2},{2,3},{1,0},{2,1},{3,2} };
        //边集
        List<List<Double>> edgeList = NavigationUtil.arrayToList(data);
        for (List<Double> sublist : edgeList) {
            sublist.add(NavigationUtil.generateRandomDoubleNumber(0.999, 0.9999));
        }
        //图
        g = new MyGraph(n, e);
        g.createMyGraph(g, n, e, edgeList);
        //流集
        flowList = NavigationUtil.generateFlow(200, 13, 43, 0.999, 0.99999);
//        flowList = NavigationUtil.generateFlow(4, -1, 4, 0.999, 0.99999);

        int redundantPathNum = 5;  //冗余路径数
        //计算流的冗余路径
        for (Flow flow : flowList) {
            flow.redundantPath = new RedundantPath().getRedundantPath(g, flow.start, flow.end, 5, redundantPathNum, flow.reliability);
        }

        System.out.println("Graph and flow and path generation completed.");
        int hyperPeriod = NavigationUtil.getHyperPeriod(flowList);
        NavigationUtil.sortPathByScore(flowList, e, hyperPeriod);
    }

    public static void output(Map<String, Object> scheduleResult, String name) {
        try {
            FileWriter writer = new FileWriter(name + ".json");
            Gson gson = new Gson();
            Map<String, Object> map = new HashMap<>();
            map.put("flows", flowList);
            map.put("connections", scheduleResult );
            writer.write(gson.toJson(map));
            writer.close();
//            System.out.println(gson.toJson(map));
            System.out.println("Data has been written to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        init();
//        System.out.println("Redundant paths calculation completed.");
        //计算流的超周期
        int hyperPeriod = NavigationUtil.getHyperPeriod(flowList);

        //整数线性规划
//        ScheduleWrapper scheduleIPL = new ScheduleWrapper(g, flowList, "IPL", hyperPeriod);
//        Map<String,Object> scheduleResultIPL = scheduleIPL.getSchedule();
//        output(scheduleResultIPL, "IPL");

        //禁忌搜索
        ScheduleWrapper scheduleTS = new ScheduleWrapper(g, flowList, "TS", hyperPeriod);
        Map<String,Object> scheduleResultTS = scheduleTS.getSchedule();
        output(scheduleResultTS, "TS");
    }
}