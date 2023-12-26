package GraphEntity;

public class Point {
    public int data;//该点id
    public EdgeNode firstArc;//该点第一条边
    public Point() {}
    public Point(int data)
    {
        this.data=data;
        this.firstArc=null;
    }
}
