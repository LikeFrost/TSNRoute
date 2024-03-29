package Route.Utils.PathUtils;

import Route.GraphEntity.Flow;
import Route.GraphEntity.Link;
import Route.GraphEntity.MyGraph;
import Route.ResultEntity.TabuSolution;
import Route.Utils.NavigationUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabuInitSolution {
    private MyGraph graph;
    private List<Flow> flowList;
    private int hyperPeriod = 1;

    public TabuInitSolution(MyGraph graph, List<Flow> flowList) {
        this.graph = graph;
        this.flowList = flowList;
        this.hyperPeriod = NavigationUtil.getHyperPeriod(flowList);
    }

    public TabuSolution initSolution() throws Exception {
        TabuSolution result = new TabuSolution();
        //flow在链路上占据的时隙
        int[][][][][] initSolution = new int[flowList.size()][10][graph.point.length][graph.point.length][hyperPeriod];
        int[][][] linkSlotUse = new int[graph.point.length][graph.point.length][hyperPeriod];

        List<Integer> successIndex = new ArrayList<>();
        List<Integer> failIndex = new ArrayList<>();

        for (int i = 0; i < flowList.size(); i++) {
            eachPath:
            for (int j = 0; j < flowList.get(i).redundantPath.size(); j++) {
                int[][][][][] tempSolution = initSolution;
                int[][][] tempLinkSlotUse = linkSlotUse;
                int groupFlag = 0;
                for (int k = 0; k < flowList.get(i).redundantPath.get(j).redundantPath.size(); k++) {
                    List<Link> linkPathList = Link.getLinks(flowList.get(i).redundantPath.get(j).redundantPath.get(k));
                    eachStart:
                    for (int start = 0; start < flowList.get(i).period - flowList.get(i).duration*linkPathList.size(); start++) {
                        boolean flag = true;
                        eachLink:
                        for (int l = 0; l < linkPathList.size(); l++) {
                            for (int d = 0; d < flowList.get(i).duration; d++) {
                                for (int p = 0; p < hyperPeriod / flowList.get(i).period; p++) {
                                    if (tempLinkSlotUse[linkPathList.get(l).srcNode][linkPathList.get(l).dstNode]
                                            [(linkPathList.get(l).hops * flowList.get(i).duration + start + d) % hyperPeriod + p * flowList.get(i).period] == 1) {
                                        flag = false;
                                        break eachLink;
                                    } else {
                                        tempLinkSlotUse[linkPathList.get(l).srcNode][linkPathList.get(l).dstNode]
                                                [(linkPathList.get(l).hops * flowList.get(i).duration + start + d) % hyperPeriod + p * flowList.get(i).period] = 1;
                                        tempSolution[i][k][linkPathList.get(l).srcNode][linkPathList.get(l).dstNode]
                                                [(linkPathList.get(l).hops * flowList.get(i).duration + start + d) % hyperPeriod + p * flowList.get(i).period] = 1;
                                    }
                                }
                            }

                        }
                        if (flag) {
                            groupFlag++;
                            break eachStart;
                        }
                    }
                }
                if (groupFlag == flowList.get(i).redundantPath.get(j).redundantPath.size()) {
                    initSolution = tempSolution;
                    linkSlotUse = tempLinkSlotUse;
                    flowList.get(i).pathIndex = j;
                    successIndex.add(i);
                    break eachPath;
                }else if(j == flowList.get(i).redundantPath.size() - 1){
                    failIndex.add(i);
                }
            }
        }
        result.successIndex = successIndex;
        result.failIndex = failIndex;
        result.solution = initSolution;
        result.successRate = (double) successIndex.size() / (successIndex.size() + failIndex.size());
        return result;
    }
}
