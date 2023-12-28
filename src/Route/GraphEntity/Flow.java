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
    @Override
    public String toString(){
        return "起点是： " + start
                +" 终点是： " + end
                +" 最大时延是： " + time
                +" 最低可靠度是： " + reliability
                +"\n冗余路径是： " + redundantPath;
    }
}
