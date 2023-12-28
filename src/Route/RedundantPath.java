package Route;

import Route.GraphEntity.MyGraph;
import Route.Utils.PathUtils.GenerateRedundantPath;
import Route.Utils.PathUtils.ShortestPath;

import java.util.List;

public class RedundantPath {
    public double redundantPathReliability;
    public List<ShortestPath.MyPath> redundantPath;
    public int HC = 0;  //最大跳数
    public int OF = 0;  //瓶颈链路负载计数
    public int CO = 0;  //路径条数

    public RedundantPath(){}
    public RedundantPath(List<ShortestPath.MyPath> redundantPath,double redundantPathReliability){
        this.redundantPathReliability = redundantPathReliability;
        this.redundantPath = redundantPath;
        for (ShortestPath.MyPath path:redundantPath){
            if(path.pathEdge.size()>this.HC){
                this.HC = path.pathEdge.size();
            }
        }
        this.OF = 0; //后续计算
        this.CO = redundantPath.size();
    }

    @Override
    public String toString() {
        return "可靠度为:"+redundantPathReliability
                +"; 最大跳数："+HC
                +"; 瓶颈链路负载计数："+OF
                +"; 路径条数："+CO
                +"\n路径为:"+redundantPath+"\n";
    }

    public static List<RedundantPath> getRedundantPath(MyGraph g, int startIndex, int endIndex, int pathCount, int targetCombinationCount, double reliabilityThreshold){
        List<ShortestPath.MyPath> path = ShortestPath.KSP_Yen(g, startIndex, endIndex,pathCount);
        List<RedundantPath> combinations = GenerateRedundantPath.generateRedundantPath(g,path,targetCombinationCount,reliabilityThreshold);
        return combinations;
    }
}
