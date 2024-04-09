package Route.ResultEntity;

import java.util.ArrayList;
import java.util.List;

public class TabuSolution {
    public List<Integer> successIndex;
    public List<Integer> failIndex;
    public int[][][][][] solution;
    public double successRate;

    // 原始构造函数
    public TabuSolution(List<Integer> successIndex, List<Integer> failIndex, int[][][][][] solution, double successRate) {
        // 对于List<Integer>，使用new ArrayList<>(existingList)来创建一个新的列表副本
        this.successIndex = new ArrayList<>(successIndex);
        this.failIndex = new ArrayList<>(failIndex);

        // 对于多维数组，需要手动复制每一层
        this.solution = deepCopySolution(solution);

        this.successRate = successRate;
    }

    public TabuSolution() {
    }

    // 深拷贝多维数组的方法
    public int[][][][][] deepCopySolution(int[][][][][] original) {
        if (original == null) return null;

        int[][][][][] copy = new int[original.length][][][][];
        for (int i = 0; i < original.length; i++) {
            if (original[i] == null) continue;

            copy[i] = new int[original[i].length][][][];
            for (int j = 0; j < original[i].length; j++) {
                if (original[i][j] == null) continue;

                copy[i][j] = new int[original[i][j].length][][];
                for (int k = 0; k < original[i][j].length; k++) {
                    if (original[i][j][k] == null) continue;

                    copy[i][j][k] = new int[original[i][j][k].length][];
                    for (int l = 0; l < original[i][j][k].length; l++) {
                        if (original[i][j][k][l] == null) continue;

                        copy[i][j][k][l] = original[i][j][k][l].clone(); // 最内层的数组可以直接使用clone()
                    }
                }
            }
        }
        return copy;
    }
}
