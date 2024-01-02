package Route;

import Route.GraphEntity.Connection;
import Route.GraphEntity.Flow;
import Route.GraphEntity.MyGraph;
import Route.ScheduleMethods.IPL;
import Route.Utils.NavigationUtil;

import java.util.ArrayList;
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

    public List<List<Connection>> getSchedule(){
        int[][][][] result = new int[flows.size()][graph.point.length][graph.point.length][hyperPeriod];
        if(algorithm.equals("IPL")){
            IPL ipl = new IPL(graph, flows);
            try {
                result = ipl.schedule();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //转化为connections
        List<List<Connection>> connections = new ArrayList<>();
        for (int i = 0; i < result.length; i++) {
            connections.add(NavigationUtil.result2Connections(result[i]));
        }
        return connections;
    }
}
