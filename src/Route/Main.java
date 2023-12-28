package Route;

import Route.GraphEntity.Flow;
import Route.GraphEntity.MyGraph;
import Route.Utils.NavigationUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int n = 45, e = 106;
        //起点、终点、可靠度
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
        for (List<Double> sublist : edgeList){
            sublist.add(NavigationUtil.generateRandomNumber(0.999,0.9999));
        }
        //图
        MyGraph g = new MyGraph(n, e);
        g.createMyGraph(g, n, e, edgeList);
        //流集
        List<Flow> flowList = NavigationUtil.generateFlow(10,13,43,0.999,0.99999);
        for (Flow flow:flowList){
            flow.redundantPath = RedundantPath.getRedundantPath(g, flow.start, flow.end, 5,5,flow.reliability);
            System.out.println(flow);
        }
    }

}