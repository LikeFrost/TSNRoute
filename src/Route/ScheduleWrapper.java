package Route;

import Route.GraphEntity.Flow;
import Route.GraphEntity.MyGraph;
import Route.ScheduleMethods.IPL;

import java.util.List;

public class ScheduleWrapper {
    MyGraph graph;
    List<Flow> flows;
    String algorithm;
    int hyperPeriod;

    public ScheduleWrapper(MyGraph graph, List<Flow> flows, String algorithm, int hyperPeriod){
        this.graph = graph;
        this.flows = flows;
        this.algorithm = algorithm;
        this.hyperPeriod = hyperPeriod;
    }

    public int[][][][] getSchedule(){
        int[][][][] result = new int[flows.size()][graph.point.length][graph.point.length][hyperPeriod];
        if(algorithm.equals("IPL")){
            IPL ipl = new IPL(graph, flows);
            try {
                result = ipl.schedule();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
