package Route.Utils;

import Route.GraphEntity.MyGraph;
import Route.ShortestPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PathCombination {

    public static List<Map<String,Object>> generateRedundantPath(MyGraph g, List<ShortestPath.MyPath> path, int targetCombinationCount, double reliabilityThreshold){
        List<Map<String,Object>> selectedCombinations = new ArrayList<>();
        int combinationCount = 0;

        for (int i = 1; i <= path.size(); i++) {
            List<List<ShortestPath.MyPath>> combinations = generateCombinations(path, i);
            for (List<ShortestPath.MyPath> combination : combinations) {
                double reliability = NavigationUtil.getRedundantPathReliability(0.5,0.5,g,combination);
                if (reliability >= reliabilityThreshold) {
                    selectedCombinations.add(Map.of("冗余路径",combination,"冗余路径可靠度",reliability));
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
        return selectedCombinations;
    }

    // 生成指定长度的路径组合
    private static List<List<ShortestPath.MyPath>> generateCombinations(List<ShortestPath.MyPath> paths, int length) {
        List<List<ShortestPath.MyPath>> combinations = new ArrayList<>();
        generateCombinationsHelper(paths, length, 0, new ArrayList<>(), combinations);
        return combinations;
    }

    private static void generateCombinationsHelper(List<ShortestPath.MyPath> paths, int length, int index, List<ShortestPath.MyPath> currentCombination, List<List<ShortestPath.MyPath>> combinations) {
        if (currentCombination.size() == length) {
            combinations.add(new ArrayList<>(currentCombination));
            return;
        }

        for (int i = index; i < paths.size(); i++) {
            ShortestPath.MyPath path = paths.get(i);
            currentCombination.add(path);
            generateCombinationsHelper(paths, length, i + 1, currentCombination, combinations);
            currentCombination.remove(currentCombination.size() - 1);
        }
    }
}