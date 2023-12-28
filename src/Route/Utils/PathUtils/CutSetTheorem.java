package Route.Utils.PathUtils;
import Route.GraphEntity.MyGraph;
import Route.Utils.NavigationUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CutSetTheorem {
    private static List<List<List<Integer>>> generateCombinations(List<List<Integer>> sets) {
        List<List<List<Integer>>> combinations = new ArrayList<>();

        int setSize = sets.size();
        for (int i = 1; i <= setSize; i++) {
            generateCombinationsHelper(sets, i, new ArrayList<>(), combinations);
        }

        return combinations;
    }

    private static void generateCombinationsHelper(List<List<Integer>> sets, int length, List<List<Integer>> currentCombination, List<List<List<Integer>>> combinations) {
        if (length == 0) {
            combinations.add(new ArrayList<>(currentCombination));
            return;
        }

        int setSize = sets.size();
        int lastIndex = currentCombination.isEmpty() ? -1 : sets.indexOf(currentCombination.get(currentCombination.size() - 1));

        for (int i = lastIndex + 1; i < setSize; i++) {
            List<Integer> set = sets.get(i);
            currentCombination.add(set);
            generateCombinationsHelper(sets, length - 1, currentCombination, combinations);
            currentCombination.remove(currentCombination.size() - 1);
        }
    }

    public static double getCombinationsReliability(List<List<Integer>> paths,MyGraph graph) {
        Set<Set<Integer>> minimalCutSets = MinimalCutSetsFinder.findMinimalCutSets(paths);
        List<List<Integer>> minimalCutLists = convertSetOfSetsToList(minimalCutSets);
        List<List<List<Integer>>> combinations = generateCombinations(minimalCutLists);
        double result = 0.0;
        for (List<List<Integer>> combination : combinations) {
            Set<Integer> set = new HashSet<>();
            for(List<Integer> com: combination){
                set.addAll(com);
            }
            double cutReliability = 1.0;
            for (int i : set){
                cutReliability *= graph.edge[i].reliability;
            }
            if(combination.size()%2 == 1){
                result += cutReliability;
            }else{
                result -= cutReliability;
            }
        }
        return result;
    }
    //将set转化为list
    private static List<List<Integer>> convertSetOfSetsToList(Set<Set<Integer>> setOfSets) {
        List<List<Integer>> resultList = new ArrayList<>();

        for (Set<Integer> set : setOfSets) {
            List<Integer> list = new ArrayList<>(set);
            resultList.add(list);
        }

        return resultList;
    }
}

class MinimalCutSetsFinder {
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
