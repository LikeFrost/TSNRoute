package Route.ScheduleMethods;

import Route.GraphEntity.Flow;
import Route.GraphEntity.MyGraph;
import Route.Utils.NavigationUtil;
import Route.Utils.PathUtils.ShortestPath;
import Route.Utils.PathUtils.TabuInitSolution;

import java.util.List;

public class TabuSearch {
    private MyGraph graph;
    private List<Flow> flowList;
    private List<List<ShortestPath.Link>> linkPathList;
    private int hyperPeriod = 1;
    private int B;
    private int propagation = 0;

    public TabuSearch(MyGraph graph, List<Flow> flowList, List<List<ShortestPath.Link>> linkPathList) {
        this.graph = graph;
        this.flowList = flowList;
        this.hyperPeriod = NavigationUtil.getHyperPeriod(flowList);
        this.linkPathList = linkPathList;
    }

    public int[][][][] schedule() throws Exception{
        //初始解
        TabuInitSolution initSolution = new TabuInitSolution(graph, flowList, linkPathList);
        int[][][][] result = initSolution.initSolution();
        return result;
    }
}
