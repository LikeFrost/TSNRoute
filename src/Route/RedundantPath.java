package Route;

import Route.GraphEntity.MyGraph;
import Route.Utils.PathUtils.GenerateRedundantPath;
import Route.Utils.PathUtils.ShortestPath;

import java.util.List;
import java.util.Map;

public class RedundantPath {
    public static List<Map<String,Object>> getRedundantPath(MyGraph g, int startIndex, int endIndex, int pathCount, int targetCombinationCount, double reliabilityThreshold){
        List<ShortestPath.MyPath> path = ShortestPath.KSP_Yen(g, startIndex, endIndex,pathCount);
        List<Map<String,Object>> combinations = GenerateRedundantPath.generateRedundantPath(g,path,targetCombinationCount,reliabilityThreshold);
        return combinations;
    }
}
