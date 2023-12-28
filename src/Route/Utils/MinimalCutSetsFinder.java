package Route.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class MinimalCutSetsFinder {
    public static Set<Set<Integer>> findMinimalCutSets(List<List<Integer>> paths) {
        List<Set<Integer>> pathEdges = paths.stream()
                .map(path -> new HashSet<>(path))
                .collect(Collectors.toList());

        Set<Set<Integer>> allCombinations = new HashSet<>();
        generateCombinations(pathEdges, 0, new HashSet<>(), allCombinations);

        return deleteSet(allCombinations.stream()
                .filter(set -> isMinimalCutSet(set, paths))
                .collect(Collectors.toSet()));
    }

    private static void generateCombinations(List<Set<Integer>> pathEdges, int index, Set<Integer> current, Set<Set<Integer>> allCombinations) {
        if (index == pathEdges.size()) {
            allCombinations.add(new HashSet<>(current));
            return;
        }
        for (Integer edge : pathEdges.get(index)) {
            current.add(edge);
            generateCombinations(pathEdges, index + 1, current, allCombinations);
            current.remove(edge);
        }
        if (index > 0) { // Skip adding empty set for the first path
            generateCombinations(pathEdges, index + 1, current, allCombinations);
        }
    }

    private static boolean isMinimalCutSet(Set<Integer> cutSet, List<List<Integer>> paths) {
        for (List<Integer> path : paths) {
            boolean containsNumber = false;
            for (Integer number : cutSet) {
                if (path.contains(number)) {
                    containsNumber = true;
                    break;
                }
            }
            if (!containsNumber) {
                return false;
            }
        }
        return true;
    }

    private static Set<Set<Integer>> deleteSet(Set<Set<Integer>> allCombinations){
        Set<Set<Integer>> setsToRemove = new HashSet<>();
        for (Set<Integer> currentSet : allCombinations) {
            for (Set<Integer> otherSet : allCombinations) {
                if (!currentSet.equals(otherSet) && otherSet.containsAll(currentSet)) {
                    // 如果当前集合是其他集合的超集，则将其添加到要删除的集合中
                    setsToRemove.add(otherSet);
                }
            }
        }
        allCombinations.removeAll(setsToRemove);
        return allCombinations;
    }

}