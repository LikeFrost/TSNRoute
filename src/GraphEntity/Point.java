package GraphEntity;

public class Point {
    public int id;//该点id
    //该点类型，分为node和switch
    public String type;

    public Point() {
    }

    public Point(int id, String type) {
        this.id = id;
        this.type = type;
    }
}
