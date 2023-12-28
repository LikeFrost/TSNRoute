package Route.GraphEntity;

import java.util.List;

public class MyGraph {
    public Point[] point;
    public Edge[] edge;
    public Edge[][] graph;
    public int numPoint;
    public int numEdges;

    public MyGraph() {
    }

    public MyGraph(int numPoint, int numEdges) {
        this.numPoint = numPoint;
        this.numEdges = numEdges;
        point = new Point[numPoint];  //初始化点集数组
        edge = new Edge[numEdges]; //初始化边集数组
        graph = new Edge[numPoint][numPoint]; //初始化图
    }

    public void createMyGraph(MyGraph MyGraph, int numPoint, int numEdges, List<List<Double>> data)  //创建图
    {
        for (int i = 0; i < numPoint; i++) {
            if (i <= 12) {
                MyGraph.point[i] = new Point(i, "switch");   //编号小于12为交换机
            } else {
                MyGraph.point[i] = new Point(i, "node");
            }
            for (int j = 0; j < numPoint; j++) {
                MyGraph.graph[i][j] = new Edge(-1,-1,-1,0,Double.MAX_VALUE);
            }
        }
        for (int i = 0; i < numEdges; i++) {
            MyGraph.edge[i] = new Edge(i, data.get(i).get(0).intValue(), data.get(i).get(1).intValue(), data.get(i).get(2),1/data.get(i).get(2));
            MyGraph.graph[edge[i].start][edge[i].end] = MyGraph.edge[i];
        }
    }
}
