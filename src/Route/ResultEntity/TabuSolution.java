package Route.ResultEntity;

import java.util.List;

public class TabuSolution {
    public List<Integer> successIndex;
    public List<Integer> failIndex;
    public int[][][][][] solution;
    public double successRate = 0;

    public TabuSolution(List<Integer> successIndex, List<Integer> failIndex, int[][][][][] solution, double successRate){
        this.successIndex = successIndex;
        this.failIndex = failIndex;
        this.solution = solution;
        this.successRate = successRate;
    }
    public TabuSolution(){}
}
