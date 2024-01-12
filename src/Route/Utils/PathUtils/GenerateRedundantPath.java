package Route.Utils.PathUtils;

import Route.GraphEntity.MyGraph;
import Route.GraphEntity.MyPath;
import Route.RedundantPath;
import Route.Utils.NavigationUtil;

import java.util.ArrayList;
import java.util.List;

public class GenerateRedundantPath {

    public static List<RedundantPath> generateRedundantPath(MyGraph g, List<MyPath> path, int targetCombinationCount, double reliabilityThreshold){
        List<RedundantPath> selectedCombinations = new ArrayList<>();
        int combinationCount = 0;

        for (int i = 1; i <= path.size(); i++) {
            List<List<MyPath>> combinations = generateCombinations(path, i);
            for (List<MyPath> combination : combinations) {
                double reliability = NavigationUtil.getRedundantPathReliability(0.5,0.5,g,combination);
                if (reliability >= reliabilityThreshold) {
                    selectedCombinations.add(new RedundantPath(combination,reliability));
                    combinationCount++;
                    if (combinationCount >= targetCombinationCount) {
                        break;
                    }
                }
            }
            if (combinationCount >= targetCombinationCount) {
                break;
            }
        }
        if (selectedCombinations.size() == 0) {
            selectedCombinations.add(new RedundantPath(path,path.get(0).reliability));
        }
        return selectedCombinations;
    }

    // 生成指定长度的路径组合
    private static List<List<MyPath>> generateCombinations(List<MyPath> paths, int length) {
        List<List<MyPath>> combinations = new ArrayList<>();
        generateCombinationsHelper(paths, length, 0, new ArrayList<>(), combinations);
        return combinations;
    }

    private static void generateCombinationsHelper(List<MyPath> paths, int length, int index, List<MyPath> currentCombination, List<List<MyPath>> combinations) {
        if (currentCombination.size() == length) {
            combinations.add(new ArrayList<>(currentCombination));
            return;
        }

        for (int i = index; i < paths.size(); i++) {
            MyPath path = paths.get(i);
            currentCombination.add(path);
            generateCombinationsHelper(paths, length, i + 1, currentCombination, combinations);
            currentCombination.remove(currentCombination.size() - 1);
        }
    }
}