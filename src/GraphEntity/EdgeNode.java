package GraphEntity;

public class EdgeNode {
    public int adjvex;//边指向的点
    public double value;//边权值
    public EdgeNode nextEdge;
    public EdgeNode() {}
    public EdgeNode(int adjvex,double value)   //初始化边结点
    {
        this.adjvex=adjvex;
        this.value=value;
        this.nextEdge=null;
    }
}
