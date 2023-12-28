package Route;

import Route.GraphEntity.MyGraph;
import Route.Utils.PathCombination;

import java.util.List;
import java.util.Map;

public class RedundantPath {
    public static List<Map<String,Object>> getRedundantPath(MyGraph g, int startIndex, int endIndex, int pathCount, int targetCombinationCount, double reliabilityThreshold){
        List<ShortestPath.MyPath> path = ShortestPath.KSP_Yen(g, startIndex, endIndex,pathCount);
        List<Map<String,Object>> combinations = PathCombination.generateRedundantPath(g,path,targetCombinationCount,reliabilityThreshold);
        for (Map<String,Object> combination:combinations){
            System.out.println(combination);
        }
        return combinations;
    }
}
