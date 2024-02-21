package Route.ScheduleMethods;

import Route.GraphEntity.Flow;
import Route.GraphEntity.Link;
import Route.GraphEntity.MyGraph;
import Route.ResultEntity.TabuSolution;
import Route.Utils.NavigationUtil;
import Route.Utils.PathUtils.TabuInitSolution;

import java.util.*;

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

    private static List<List<Integer>> permute(List<Integer> nums) {
        List<List<Integer>> permutations = new ArrayList<>();

        if (nums.size() == 0) {
            permutations.add(new ArrayList<>());
            return permutations;
        }

        Integer first = nums.get(0);
        List<Integer> remaining = nums.subList(1, nums.size());
        List<List<Integer>> subPermutations = permute(remaining);

        for (List<Integer> subPermutation : subPermutations) {
            for (int i = 0; i <= subPermutation.size(); i++) {
                List<Integer> permutation = new ArrayList<>(subPermutation);
                permutation.add(i, first);
                permutations.add(permutation);
            }
        }

        return permutations;
    }

    private TabuSolution searchNeighbor(List<Integer> neighbor, int[][][][][] solution) {
        TabuSolution result = new TabuSolution();
        //flow在链路上占据的时隙
        int[][][][][] initSolution = solution;
        int[][][] linkSlotUse = new int[graph.point.length][graph.point.length][hyperPeriod];
        //根据当前解初始化linkSlotUse
        for(int i=0;i< initSolution.length;i++){
            for(int j=0;j< initSolution[i].length;j++){
                for(int k=0;k< initSolution[i][j].length;k++){
                    for(int l=0;l< initSolution[i][j][k].length;l++){
                        for (int m = 0; m < initSolution[i][j][k][l].length; m++) {
                            linkSlotUse[k][l][m] = Math.max(linkSlotUse[k][l][m], initSolution[i][j][k][l][m]);
                        }
                    }
                }
            }
        }

        List<Integer> successIndex = new ArrayList<>();
        List<Integer> failIndex = new ArrayList<>();

        for (int i = 0; i < neighbor.size(); i++) {
            int index = neighbor.get(i);
            eachPath:
            for (int j = 0; j < flowList.get(index).redundantPath.size(); j++) {
                int[][][][][] tempSolution = initSolution;
                int[][][] tempLinkSlotUse = linkSlotUse;
                int groupFlag = 0;
                for (int k = 0; k < flowList.get(index).redundantPath.get(j).redundantPath.size(); k++) {
                    List<Link> linkPathList = Link.getLinks(flowList.get(index).redundantPath.get(j).redundantPath.get(k));
                    eachStart:
                    for (int start = 0; start < hyperPeriod - flowList.get(index).duration; start++) {
                        boolean flag = true;
                        eachLink:
                        for (int l = 0; l < linkPathList.size(); l++) {
                            for (int d = 0; d < flowList.get(index).duration; d++) {
                                for (int p = 0; p < hyperPeriod / flowList.get(index).period; p++) {
                                    if (tempLinkSlotUse[linkPathList.get(l).srcNode][linkPathList.get(l).dstNode]
                                            [(linkPathList.get(l).hops * flowList.get(index).duration + start + d) % hyperPeriod + p * flowList.get(index).period] == 1) {
                                        flag = false;
                                        break eachLink;
                                    } else {
                                        tempLinkSlotUse[linkPathList.get(l).srcNode][linkPathList.get(l).dstNode]
                                                [(linkPathList.get(l).hops * flowList.get(index).duration + start + d) % hyperPeriod + p * flowList.get(index).period] = 1;
                                        tempSolution[index][k][linkPathList.get(l).srcNode][linkPathList.get(l).dstNode]
                                                [(linkPathList.get(l).hops * flowList.get(index).duration + start + d) % hyperPeriod + p * flowList.get(index).period] = 1;
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
                    flowList.get(index).pathIndex = j;
                    successIndex.add(index);
                    break eachPath;
                }else if(j == flowList.get(index).redundantPath.size() - 1){
                    failIndex.add(index);
                }
            }
        }
        result.successIndex = successIndex;
        result.failIndex = failIndex;
        result.solution = initSolution;
        result.successRate = (double) successIndex.size() / (successIndex.size() + failIndex.size());
        return result;
    }

    public TabuSolution search(TabuSolution current, TabuSolution best, int times) {
        if (times >= 100) {
            return best;
        }
        if (best.successRate == 1) {
            return best;
        }
        //将成功的流随机移动到失败的流中，并生成全排列
        List<List<Integer>> neighbors = moveSuccessToFail(current.successIndex, current.failIndex);

        //将failIndex中的流调度清空
        int length1 = current.solution[0].length;
        int length2 = current.solution[0][0].length;
        int length3 = current.solution[0][0][0].length;
        int length4 = current.solution[0][0][0][0].length;
        for (int i = 0; i < current.failIndex.size(); i++) {
            for (int j = 0; j < length1; j++) {
                for (int k = 0; k < length2; k++) {
                    for (int l = 0; l < length3; l++) {
                        for (int m = 0; m < length4; m++) {
                            current.solution[current.failIndex.get(i)][j][k][l][m] = 0;
                        }
                    }
                }
            }
        }

        //遍历所有邻居，找出不在禁忌列表的最优解
        TabuSolution bestNeighbor = new TabuSolution();
        for(List<Integer> neighbor:neighbors){
            TabuSolution neighborSolution = searchNeighbor(neighbor, current.solution);
            TabuNode tabuNode = new TabuNode(neighborSolution.successIndex, neighborSolution.failIndex);
            if(!tabuList.contains(tabuNode)&&neighborSolution.successRate>bestNeighbor.successRate){
                if(tabuList.size()==tabuLength){
                    tabuList.remove(0);
                    tabuList.add(tabuNode);
                }else{
                    tabuList.add(tabuNode);
                }
                bestNeighbor = neighborSolution;
                if(bestNeighbor.successRate>best.successRate){
                    best = bestNeighbor;
                }
            }
            if(tabuList.contains(tabuNode)&&neighborSolution.successRate>best.successRate){
                best = neighborSolution;
                bestNeighbor = neighborSolution;
                tabuList.remove(tabuNode);
                tabuList.add(tabuNode);
            }
        }

        return search(bestNeighbor, best, times + neighbors.size());
    }

    public int[][][][][] schedule() throws Exception {
        //初始解
        TabuInitSolution initSolution = new TabuInitSolution(graph, flowList);
        TabuSolution init = initSolution.initSolution();
        return search(init, init, 0).solution;
    }

    private List<List<Integer>> moveSuccessToFail(List<Integer> successIndex, List<Integer> failIndex) {
        Random random = new Random();
        int moveIndex = Math.min(successIndex.size(), failIndex.size());
        while (moveIndex > 0) {
            int index = random.nextInt(successIndex.size());
            failIndex.add(successIndex.get(index));
            successIndex.remove(index);
            moveIndex--;
        }
        return permute(failIndex);
    }
}
