package Route.GraphEntity;

public class Point {
    public int id; //该点id
    public String type; //该点类型，分为node和switch


    public Point() {
    }

    public Point(int id, String type) {
        this.id = id;
        this.type = type;
    }
}
