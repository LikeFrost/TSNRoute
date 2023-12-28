package Route.GraphEntity;

import Route.RedundantPath;

import java.util.List;

public class Flow {
    public int start;
    public int end;
    public int time;
    public double reliability;
    public List<RedundantPath> redundantPath;

    public Flow(){}
    public Flow(int start, int end, int time, double reliability, List<RedundantPath> redundantPath){
        this.start = start;
        this.end = end;
        this.time = time;
        this.reliability = reliability;
        this.redundantPath = redundantPath;
    }
}
