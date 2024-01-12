package Route.GraphEntity;

import Route.RedundantPath;

import java.util.List;

public class Flow {
    public int start;
    public int end;
    public int ddl; //时延
    public int duration;  //流在每条边上的持续时间
    public int period;  //周期
    public double reliability;
    public List<RedundantPath> redundantPath;

    public Flow(){}
    public Flow(int start, int end, int ddl, int duration,int period, double reliability, List<RedundantPath> redundantPath){
        this.start = start;
        this.end = end;
        this.ddl = ddl;
        this.duration = duration;
        this.period = period;
        this.reliability = reliability;
        this.redundantPath = redundantPath;
    }
    @Override
    public String toString(){
        return "起点是： " + start
                +" 终点是： " + end
                +" 最大时延是： " + ddl
                +" 周期是： " + period
                +" 最低可靠度是： " + reliability
                +"\n冗余路径是： " + redundantPath;
    }
}