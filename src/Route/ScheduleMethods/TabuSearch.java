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

    public TabuSearch(MyGraph graph, List<Flow> flowList){
        this.graph = graph;
        this.flowList = flowList;
//        this.hyperPeriod = NavigationUtil.getHyperPeriod(flowList);
    }

    public int[][][][][] schedule() throws Exception{
        //初始解
        TabuInitSolution initSolution = new TabuInitSolution(graph, flowList);
        int[][][][][] result = initSolution.initSolution();
        return result;
    }
}
