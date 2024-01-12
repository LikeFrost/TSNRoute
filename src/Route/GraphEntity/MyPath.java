package Route.GraphEntity;

import java.util.List;

public class MyPath {
    // 路径上的各个节点对应的数组下标（从起点到终点）
    public List<Integer> path;
    //路径上的各个边对应的id
    public List<Integer> pathEdge;
    // 路径总权值
    public double weight;
    //该条路径可靠度
    public double reliability;

    public MyPath() {
    }

    public MyPath(List<Integer> path, List<Integer> pathEdge, double weight, double reliability) {
        this.path = path;
        this.pathEdge = pathEdge;
        this.weight = weight;
        this.reliability = reliability;
    }

    @Override
    public String toString() {
        return "path=" + path +
                ", pathEdge=" + pathEdge +
//                    ", weight=" + weight +
                ", reliability=" + reliability + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        MyPath path1 = (MyPath) o;
        return path != null ? path.equals(path1.path) : path1.path == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = path != null ? path.hashCode() : 0;
        temp = Double.doubleToLongBits(weight);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
