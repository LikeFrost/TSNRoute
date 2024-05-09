package Route.ScheduleMethods;

import Route.GraphEntity.*;
import Route.ResultEntity.TabuSolution;
import Route.Utils.NavigationUtil;
import Route.Utils.PathUtils.TabuInitSolution;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class TabuSearch {
    private MyGraph graph;
    private List<Flow> flowList;
    private List<TabuNode> tabuList = new ArrayList<>();
    private int hyperPeriod = 1;
    private int tabuLength = 5;

    public TabuSearch(MyGraph graph, List<Flow> flowList) {
        this.graph = graph;
        this.flowList = flowList;
        this.hyperPeriod = NavigationUtil.getHyperPeriod(flowList);
    }

    public TabuSearch() {
    }

    private TabuSolution searchNeighbor(List<Integer> neighbor, List<Integer> _successIndex, List<List<List<LinkUse>>> solution) {
        //flow在链路上占据的时隙
        List<List<List<LinkUse>>> initSolution = NavigationUtil.deepCloneSolution(solution);
        int[][][] linkSlotUse = new int[graph.point.length][graph.point.length][hyperPeriod];
        List<Integer> successIndex = new ArrayList<>(_successIndex);
        //根据当前解初始化linkSlotUse
        for (int i = 0; i < initSolution.size(); i++) {
            for (int j = 0; j < initSolution.get(i).size(); j++) {
                for (int k = 0; k < initSolution.get(i).get(j).size(); k++) {
                    for (int d = initSolution.get(i).get(j).get(k).timeslot.startTime; d < initSolution.get(i).get(j).get(k).timeslot.startTime + flowList.get(i).duration; d++) {
                        linkSlotUse[initSolution.get(i).get(j).get(k).srcNode][initSolution.get(i).get(j).get(k).dstNode][d] = 1;
                    }
                }
            }
        }

        List<Integer> failIndex = new ArrayList<>();

        for (int i = 0; i < neighbor.size(); i++) {
            int index = neighbor.get(i);
            eachPath:
            for (int j = 0; j < flowList.get(index).redundantPath.size(); j++) {
                List<List<List<LinkUse>>> tempSolution = NavigationUtil.deepCloneSolution(initSolution);
                int[][][] tempLinkSlotUse = NavigationUtil.deepCloneArr(linkSlotUse);
                int groupFlag = 0;
                for (int k = 0; k < flowList.get(index).redundantPath.get(j).redundantPath.size(); k++) {
                    List<Link> linkPathList = Link.getLinks(flowList.get(index).redundantPath.get(j).redundantPath.get(k));
                    eachStart:
                    for (int start = 0; start < flowList.get(index).period - flowList.get(index).duration * linkPathList.size(); start++) {
                        boolean flag = true;
                        eachLink:
                        for (int l = 0; l < linkPathList.size(); l++) {
                            for (int d = 0; d < flowList.get(index).duration; d++) {
                                for (int p = 0; p < hyperPeriod / flowList.get(index).period; p++) {
                                    if (tempLinkSlotUse[linkPathList.get(l).srcNode][linkPathList.get(l).dstNode]
                                            [((linkPathList.get(l).hops - 1) * flowList.get(index).duration + start + d) % hyperPeriod + p * flowList.get(index).period] == 1) {
                                        flag = false;
                                        break eachLink;
                                    } else {
                                        tempLinkSlotUse[linkPathList.get(l).srcNode][linkPathList.get(l).dstNode]
                                                [((linkPathList.get(l).hops - 1) * flowList.get(index).duration + start + d) % hyperPeriod + p * flowList.get(index).period] = 1;
                                        tempSolution.get(index).get(k).add(new LinkUse(linkPathList.get(l).srcNode, linkPathList.get(l).dstNode,
                                                new Timeslot(((linkPathList.get(l).hops - 1) * flowList.get(index).duration + start + d) % hyperPeriod + p * flowList.get(index).period, flowList.get(index).duration)));
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
                if (groupFlag == flowList.get(index).redundantPath.get(j).redundantPath.size()) {
                    initSolution = tempSolution;
                    linkSlotUse = tempLinkSlotUse;
                    flowList.get(index).selectedPath = flowList.get(index).redundantPath.get(j);
                    successIndex.add(index);
                    break eachPath;
                } else if (j == flowList.get(index).redundantPath.size() - 1) {
                    failIndex.add(index);
                }
            }
        }
        TabuSolution result = new TabuSolution(successIndex, failIndex, initSolution, (double) successIndex.size() / (successIndex.size() + failIndex.size()), NavigationUtil.calcOF2(graph, linkSlotUse));
        flowList = NavigationUtil.updatePathOF(flowList, linkSlotUse);
        return result;
    }

    public TabuSolution search(TabuSolution current, TabuSolution best, int times) {
        System.out.println("times" + times);
        System.out.println("successIndex" + current.successIndex);
        System.out.println("failIndex" + current.failIndex);
        System.out.println("successRate" + current.successRate);
        System.out.println("bestFail" + best.failIndex);
        if (times >= 200) {
            return best;
        }
        if (best.successRate == 1) {
            return best;
        }
        //将成功的流随机移动到失败的流中
        List<Integer> neighbor = moveSuccessToFail(current.successIndex, current.failIndex);

        //将failIndex中的流调度清空
        if (current.failIndex.size() > 0) {
            for (int i = 0; i < current.failIndex.size(); i++) {
                int index = current.failIndex.get(i);
                for (int j = 0; j < current.solution.get(index).size(); j++) {
                    current.solution.get(index).set(j, new ArrayList<>());
                }
            }
        }

        //遍历邻居，找出不在禁忌列表的最优解

        TabuSolution neighborSolution = searchNeighbor(neighbor, current.successIndex, current.solution);
        TabuNode tabuNode = new TabuNode(neighborSolution.successIndex, neighborSolution.failIndex);
        if (neighborSolution.successRate > best.successRate) {
            if (tabuList.size() == tabuLength) {
                tabuList.remove(0);
                tabuList.add(tabuNode);
            } else {
                tabuList.add(tabuNode);
            }
            best = new TabuSolution(neighborSolution.successIndex, neighborSolution.failIndex, neighborSolution.solution, neighborSolution.successRate, neighborSolution.OF2);
        }
        return search(neighborSolution, best, times + 1);
    }

    public List<List<List<LinkUse>>> schedule() throws Exception {
        //初始解
        TabuInitSolution initSolution = new TabuInitSolution(graph, flowList);
        System.out.println("开始进行禁忌搜索");
        TabuSolution init = initSolution.initSolution();
        TabuSolution result = search(init, init, 0);
        System.out.println("TS-best ");
        System.out.println("TS-successRate " + result.successRate);
        System.out.println("TS-OF2 " + result.OF2);
        System.out.println("TS-score " + result.score);
        System.out.println("TS-successIndex " + result.successIndex);
        System.out.println("TS-failIndex " + result.failIndex);
        return result.solution;
    }

    private List<Integer> moveSuccessToFail(List<Integer> successIndex, List<Integer> failIndex) {
        Random random = new Random();
        int moveIndex = Math.min(successIndex.size(), failIndex.size());
        while (moveIndex > 0) {
            int index = random.nextInt(successIndex.size());
            failIndex.add(successIndex.get(index));
            successIndex.remove(index);
            moveIndex--;
        }
        return failIndex;
    }

    private class TabuNode {
        List<Integer> successIndex;
        List<Integer> failIndex;

        public TabuNode(List<Integer> successIndex, List<Integer> failIndex) {
            this.successIndex = successIndex;
            this.failIndex = failIndex;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            TabuNode other = (TabuNode) obj;
            return Objects.equals(successIndex, other.successIndex)
                    && Objects.equals(failIndex, other.failIndex);
        }
    }
}
