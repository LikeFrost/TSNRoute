package Route.GraphEntity;

public class Edge {
    public int id; //边id
    public int start; //边起点
    public int end; //边终点
    public double reliability; //边可靠度
    public double weight; //边权值

    public Edge() {
    }

    public Edge(int id, int start, int end, double reliability, double weight)   //初始化边结点
    {
        this.id = id;
        this.start = start;
        this.end = end;
        this.reliability = reliability;
        this.weight = weight;
    }
}
