package Route;

import Route.GraphEntity.Flow;
import Route.GraphEntity.MyGraph;
import Route.Utils.NavigationUtil;
import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class GenData {
    static MyGraph g;
    static List<Flow> flowList = new ArrayList<>();
    static int hyperPeriod = 0;

    public static void genData(){
        int n = 45, e = 106;
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
        //边集
        List<List<Double>> edgeList = NavigationUtil.arrayToList(data);
        for (List<Double> sublist : edgeList) {
            sublist.add(NavigationUtil.generateRandomDoubleNumber(0.999, 0.99999));
        }
        //图
        g = new MyGraph(n, e);
        g.createMyGraph(g, n, e, edgeList);
        //流集
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the flow count:");
        int flowCount = scanner.nextInt();
        flowList = NavigationUtil.generateFlow(flowCount, 13, 43, 0.999, 0.99999);

        int redundantPathNum = 5;  //冗余路径数
        //计算流的冗余路径
        for (Flow flow : flowList) {
            flow.redundantPath = new RedundantPath().getRedundantPath(g, flow.start, flow.end, 5, redundantPathNum, flow.reliability);
        }
        hyperPeriod = NavigationUtil.getHyperPeriod(flowList);  //超周期
        System.out.println("Graph and flow and path generation completed.");
        NavigationUtil.sortPathByScore(flowList, e, hyperPeriod);
        output();
    }

    public static void output() {
        try {
            FileWriter writer = new FileWriter( "initialData.json");
            Gson gson = new Gson();
            Map<String, Object> map = new HashMap<>();
            map.put("graph", g);
            map.put("flows", flowList);
            map.put("hyperPeriod", hyperPeriod);
            writer.write(gson.toJson(map));
            writer.close();
            System.out.println("Initial data has been written to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the initial file.");
            e.printStackTrace();
        }
    }
}
