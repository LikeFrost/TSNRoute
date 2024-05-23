package Route.Utils;

import Route.GraphEntity.MyGraph;
import Route.GraphEntity.MyPath;
import Route.Utils.PathUtils.ShortestPath;

import java.util.List;

public class CountPath {
    public double redundantPathReliability;
    public List<MyPath> redundantPath;

    public CountPath() {
    }

    public CountPath(List<MyPath> redundantPath, double redundantPathReliability) {
        this.redundantPathReliability = redundantPathReliability;
        this.redundantPath = redundantPath;
    }

    public CountPath getCountPath(MyGraph g, int startIndex, int endIndex, int pathCount) {
        List<MyPath> path = ShortestPath.KSP_Yen(g, startIndex, endIndex, pathCount);
        double reliability = NavigationUtil.getRedundantPathReliability(0.5, 0.5, g, path);
        CountPath countPath = new CountPath(path, reliability);
        return countPath;
    }
}
