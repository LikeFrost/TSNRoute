package Route;

import Route.GraphEntity.Connection;
import Route.GraphEntity.Flow;
import Route.GraphEntity.MyGraph;
import Route.ScheduleMethods.IPL;
import Route.Utils.NavigationUtil;
import Route.Utils.PathUtils.ShortestPath;

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

    public List<List<List<Connection>>> getSchedule(){
        //新流
        List<Flow> newFlows = new ArrayList<>();
        //流对应的路径
        List<List<ShortestPath.Link>> newLinkPathList = new ArrayList<>();
        //流对应的路径数
        List<Integer> flowPathCount = new ArrayList<>();
        for (Flow flow : flows) {
            for (int i = 0; i < flow.redundantPath.get(0).redundantPath.size(); i++) {
                newFlows.add((flow));
                newLinkPathList.add(ShortestPath.Link.getLinks(flow.redundantPath.get(0).redundantPath.get(i)));
            }
            flowPathCount.add(flow.redundantPath.get(0).redundantPath.size());
        }
        int[][][][] result = new int[newFlows.size()][graph.point.length][graph.point.length][hyperPeriod];
        if(algorithm.equals("IPL")){
            IPL ipl = new IPL(graph, newFlows,newLinkPathList);
            try {
                result = ipl.schedule();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //转化为connections
        List<List<List<Connection>>> connections = new ArrayList<>();
        int k = 0;
        for (int i = 0; i < flows.size(); i++) {
            List<List<Connection>> connection = new ArrayList<>();
            for (int j = 0; j < flowPathCount.get(i); j++) {
                connection.add(NavigationUtil.result2Connections(result[k++]));
            }
            connections.add(connection);
        }
        return connections;
    }
}
