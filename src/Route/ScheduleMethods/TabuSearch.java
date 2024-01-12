package Route.ScheduleMethods;

import Route.GraphEntity.Flow;
import Route.GraphEntity.Link;
import Route.GraphEntity.MyGraph;
import Route.Utils.NavigationUtil;
import Route.Utils.PathUtils.TabuInitSolution;

import java.util.List;

public class TabuSearch {
    private MyGraph graph;
    private List<Flow> flowList;
    private List<List<Link>> linkPathList;
    private int hyperPeriod = 1;

    public TabuSearch(MyGraph graph, List<Flow> flowList, List<List<Link>> linkPathList) {
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
