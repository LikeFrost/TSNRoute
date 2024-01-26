package Route.Utils.PathUtils;

import Route.GraphEntity.Flow;
import Route.GraphEntity.Link;
import Route.GraphEntity.MyGraph;
import Route.Utils.NavigationUtil;

import java.util.List;

public class TabuInitSolution {
    private MyGraph graph;
    private List<Flow> flowList;
    private List<List<Link>> linkPathList;
    private int hyperPeriod = 1;
    private List<Integer> flowPathCount;

    public TabuInitSolution(MyGraph graph, List<Flow> flowList, List<List<Link>> linkPathList,List<Integer> flowPathCount) {
        this.graph = graph;
        this.flowList = flowList;
        this.hyperPeriod = NavigationUtil.getHyperPeriod(flowList);
        this.linkPathList = linkPathList;
        this.flowPathCount =flowPathCount;
    }

    public int[][][][] initSolution() throws Exception{
        //flow在链路上占据的时隙
        int[][][][] initSolution = new int[flowList.size()][graph.point.length][graph.point.length][hyperPeriod];
        int[][][] linkSlotUse = new int[graph.point.length][graph.point.length][hyperPeriod];
        //输出linkSlotUse

        int i = -1;
        for(int count = 0; count <flowPathCount.size(); count++){
            int groupFlag = 0;
            int[][][][] tempSolution = initSolution;
            int[][][] tempLinkSlotUse = linkSlotUse;
            for(int current = 0;current<flowPathCount.get(count);current++){
                i++;
                for(int start = 0; start < hyperPeriod-flowList.get(i).duration; start++){
                    boolean flag = true;
                    loop:for(int j = 0; j <linkPathList.get(i).size();j++){
                        for(int d = 0; d < flowList.get(i).duration; d++){
                            for (int p = 0; p < hyperPeriod / flowList.get(i).period; p++) {
                                if(tempLinkSlotUse[linkPathList.get(i).get(j).srcNode][linkPathList.get(i).get(j).dstNode]
                                        [(linkPathList.get(i).get(j).hops*flowList.get(i).duration+start+d)%hyperPeriod + p * flowList.get(i).period] == 1){
                                    flag = false;
                                    break loop;
                                }else{
                                    tempSolution[i][linkPathList.get(i).get(j).srcNode][linkPathList.get(i).get(j).dstNode]
                                            [(linkPathList.get(i).get(j).hops*flowList.get(i).duration+start+d)%hyperPeriod + p * flowList.get(i).period] = 1;
                                    tempLinkSlotUse[linkPathList.get(i).get(j).srcNode][linkPathList.get(i).get(j).dstNode]
                                            [(linkPathList.get(i).get(j).hops*flowList.get(i).duration+start+d)%hyperPeriod + p * flowList.get(i).period] = 1;
                                }
                            }
                        }
                    }
                    if(flag){
                        groupFlag++;
                        break;
                    }
                }
            }
            if(groupFlag == flowPathCount.get(count)) {
                initSolution = tempSolution;
                linkSlotUse = tempLinkSlotUse;
            }
        }

//        System.out.println(NavigationUtil.updatePathOF(flowList,linkSlotUse));
        return initSolution;
    }
}
