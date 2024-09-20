package Route.ResultEntity;

import Route.GraphEntity.LinkUse;
import Route.Utils.NavigationUtil;

import java.util.ArrayList;
import java.util.List;

public class TabuSolution {
    public List<Integer> successIndex = new ArrayList<>();
    public List<Integer> failIndex = new ArrayList<>();
    public List<List<List<LinkUse>>> solution = new ArrayList<>();
    public double successRate;  // 成功率
    public double OF2;  //链路负载方差
    public double score;  //评价标准

    // 原始构造函数
    public TabuSolution(List<Integer> successIndex, List<Integer> failIndex, List<List<List<LinkUse>>> solution, double successRate, double OF2) {
        // 对于List<Integer>，使用new ArrayList<>(existingList)来创建一个新的列表副本
        this.successIndex = new ArrayList<>(successIndex);
        this.failIndex = new ArrayList<>(failIndex);

        // 对于多维数组，需要手动复制每一层
        this.solution = NavigationUtil.deepCloneSolution(solution);

        this.successRate = successRate;
        this.OF2 = OF2;
        this.calcScore();
    }

    public TabuSolution() {
    }

    public void calcScore() {
        double k1 = 1;
        double k2 = 1;
        this.score = k1 * this.successRate + k2 * 1 / (1 + this.OF2);
    }

}