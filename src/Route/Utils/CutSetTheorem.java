package Route.Utils;
import Route.GraphEntity.MyGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public static double getCombinationsReliability(List<List<Integer>> sets,MyGraph graph) {
        List<List<List<Integer>>> combinations = generateCombinations(sets);
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
}
