package Route.Utils.PathUtils;

import Route.GraphEntity.*;
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
//        int[][][][][] initSolution = new int[flowList.size()][6][graph.point.length][graph.point.length][hyperPeriod];
        //flow、path、step
        List<List<List<LinkUse>>> initSolution = new ArrayList<>();
//        for(int i = 0;i<flowList.size();i++){
//            initSolution.add(new ArrayList<>());
//            for(int j = 0; j<flowList.get(i).redundantPath.size();j++){
//                initSolution.get(i).add(new ArrayList<>());
//            }
//        }
        int[][][] linkSlotUse = new int[graph.point.length][graph.point.length][hyperPeriod];

        List<Integer> successIndex = new ArrayList<>();
        List<Integer> failIndex = new ArrayList<>();

        //第i条流
        for (int i = 0; i < flowList.size(); i++) {
            initSolution.add(new ArrayList<>());
            eachPath:
            //第j条冗余路径
            for (int j = 0; j < flowList.get(i).redundantPath.size(); j++) {
                initSolution.set(i,new ArrayList<>());
                List<List<List<LinkUse>>> tempSolution = NavigationUtil.deepClone(initSolution);
                int[][][] tempLinkSlotUse = linkSlotUse;
                int groupFlag = 0;
                //第j条冗余路径的第k条路径
                for (int k = 0; k < flowList.get(i).redundantPath.get(j).redundantPath.size(); k++) {
                    tempSolution.get(i).add(new ArrayList<>());
                    //将路径转化为链路格式
                    List<Link> linkPathList = Link.getLinks(flowList.get(i).redundantPath.get(j).redundantPath.get(k));
                    eachStart:
                    //遍历每一个开始时间
                    for (int start = 0; start < flowList.get(i).period - flowList.get(i).duration*linkPathList.size(); start++) {
                        boolean flag = true;
                        eachLink:
                        //遍历每一个链路
                        for (int l = 0; l < linkPathList.size(); l++) {
                            //遍历当前流的持续长度
                            for (int d = 0; d < flowList.get(i).duration; d++) {
                                //遍历超周期包含的每一个周期
                                for (int p = 0; p < hyperPeriod / flowList.get(i).period; p++) {
                                    if (tempLinkSlotUse[linkPathList.get(l).srcNode][linkPathList.get(l).dstNode]
                                            [(linkPathList.get(l).hops * flowList.get(i).duration + start + d) % hyperPeriod + p * flowList.get(i).period] == 1) {
                                        flag = false;
                                        //如果当前时隙被占用，从下一个开始时间开始
                                        break eachLink;
                                    } else {
                                        tempLinkSlotUse[linkPathList.get(l).srcNode][linkPathList.get(l).dstNode]
                                                [(linkPathList.get(l).hops * flowList.get(i).duration + start + d) % hyperPeriod + p * flowList.get(i).period] = 1;
                                        tempSolution.get(i).get(k).add(new LinkUse(linkPathList.get(l).srcNode, linkPathList.get(l).dstNode,
                                                new Timeslot((linkPathList.get(l).hops * flowList.get(i).duration + start + d) % hyperPeriod + p * flowList.get(i).period,flowList.get(i).duration)));
//                                        tempSolution[i][k][linkPathList.get(l).srcNode][linkPathList.get(l).dstNode]
//                                                [(linkPathList.get(l).hops * flowList.get(i).duration + start + d) % hyperPeriod + p * flowList.get(i).period] = 1;
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
        result.OF2 = NavigationUtil.calcOF2(graph, linkSlotUse);
        result.calcScore();
        System.out.println(initSolution);
        return result;
    }
}
