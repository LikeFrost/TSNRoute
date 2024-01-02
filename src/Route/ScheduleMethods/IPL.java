package Route.ScheduleMethods;

import Route.GraphEntity.Flow;
import Route.GraphEntity.MyGraph;
import Route.Utils.PathUtils.ShortestPath;
import Route.Utils.PathUtils.ShortestPath.Link;
import ilog.concert.IloException;
import ilog.concert.IloIntExpr;
import ilog.concert.IloIntVar;
import ilog.cplex.IloCplex;

import java.util.*;

public class IPL {
    private MyGraph graph;

    public MyGraph getTopology() {
        return graph;
    }

    public void setTopology(MyGraph graph) {
        this.graph = graph;
    }

    public List<Flow> getFlowList() {
        return flowList;
    }

    public void setFlowList(List<Flow> flowList) {
        this.flowList = flowList;
    }

    public int getHyperPeriod() {
        return hyperPeriod;
    }

    public void setHyperPeriod(int hyperPeriod) {
        this.hyperPeriod = hyperPeriod;
    }

    public int getB() {
        return B;
    }

    public void setB(int b) {
        B = b;
    }


    private List<Flow> flowList;
    private int hyperPeriod = 1;
    private int B;
    private int propagation = 0;

    public IPL(MyGraph graph, List<Flow> flowList) {
        this.graph = graph;
        this.flowList = flowList;
        for (Flow flow : flowList) {
            hyperPeriod = calculateLCM(hyperPeriod, flow.period);
        }
        this.B = hyperPeriod * graph.edge.length + 1;
    }

    public int[][][][] schedule() throws Exception {
        int[][][][] O = new int[flowList.size()][graph.point.length][graph.point.length][hyperPeriod];
        List<List<Link>> linkPathList = findRoutePath(graph,flowList);
        Map<String, Object> stringObjectMap = ILPSchedule(linkPathList, flowList);
        if (stringObjectMap != null) {
            int[][] Xs = (int[][]) stringObjectMap.get("X");
            int[][][][] H = (int[][][][]) stringObjectMap.get("H");

            for (int j = 0; j < flowList.size(); j++) {
                for (int start = 0; start < hyperPeriod; start++) {
                    if (Xs[j][start] == 1) {
//                        for (int k = 0; k < pathList.get(j).size() - 1; k++) {
                        for (Link link: linkPathList.get(j)) {
                            for (int d = 0; d < flowList.get(j).duration; d++) {
                                for (int p = 0; p < hyperPeriod / flowList.get(j).period; p++) {
                                    O[j][link.srcNode][link.dstNode]
                                            [(start + d + link.hops * (propagation + flowList.get(j).duration)) % flowList.get(j).period + p * flowList.get(j).period]++;
                                }
                            }
                        }
                        break;// 当前流的O数组元素计算结束
                    }
                }
            }
        }
        return O;
    }

    private List<List<Link>> findRoutePath(MyGraph graph,List<Flow> flowList) {
        List<List<Link>> linkPathList = new ArrayList<>();
        for (Flow flow : flowList) {
            List<ShortestPath.MyPath> path = ShortestPath.KSP_Yen(graph, flow.start, flow.end,1);
            linkPathList.add(ShortestPath.Link.getLinks(path.get(0)));
        }
        return linkPathList;
    }

    private Map<String, Object> ILPSchedule(List<List<Link>> linkPathList, List<Flow> flowList) throws IloException {
        IloCplex cplex = new IloCplex();
        cplex.setParam(IloCplex.IntParam.TimeLimit, 600);
        cplex.setParam(IloCplex.IntParam.NodeSel, 2);
        cplex.setParam(IloCplex.IntParam.MIPEmphasis, 3);
//        cplex.setOut(null);
        IloIntVar[][] Xs = new IloIntVar[flowList.size()][hyperPeriod];
        IloIntVar[][][][] H = new IloIntVar[flowList.size()][graph.point.length][graph.point.length][hyperPeriod];
        for (int i = 0; i < flowList.size(); i++) {
            for (int j = 0; j < hyperPeriod; j++) {
                Xs[i][j] = cplex.intVar(0, 1);
                cplex.add(Xs[i][j]);
                for (int m = 0; m < graph.point.length; m++) { //遍历起点终点（链路truncated）
                    for (int n = 0; n < graph.point.length; n++) {
                        H[i][m][n][j] = cplex.intVar(0, 1);
                        cplex.add(H[i][m][n][j]);
                    }
                }
            }
        }

        IloIntExpr maxObjectFunction = cplex.intExpr();

        //Transmission Constraints
        for (int i = 0; i < flowList.size(); i++) {
            IloIntExpr iloIntExpr = cplex.intExpr();
            for (int j = 0; j <= flowList.get(i).period - flowList.get(i).duration; j++) {
                iloIntExpr = cplex.sum(iloIntExpr, Xs[i][j]);
                maxObjectFunction = cplex.sum(maxObjectFunction, Xs[i][j]);
            }
//            cplex.addEq(iloIntExpr, 1);
            cplex.addLe(iloIntExpr, 1);
        }

        cplex.addMaximize(maxObjectFunction);

        //Time Slots Reservation Constraint
        for (int i = 0; i < flowList.size(); i++) {
            int slotConsumed = hyperPeriod / flowList.get(i).period * flowList.get(i).duration * (linkPathList.get(i).size());
            for (int j = 0; j <= flowList.get(i).period - flowList.get(i).duration; j++) {  // 如果流i从第j时隙开始发送
                IloIntExpr iloIntExpr = cplex.prod(B, Xs[i][j]);
                iloIntExpr = cplex.sum(iloIntExpr, slotConsumed);
                for (Link link : linkPathList.get(i)) {
                    for (int c = 0; c < hyperPeriod; c++) {
                        if (c % flowList.get(i).period >= (j + link.hops * (propagation + flowList.get(i).duration)) % flowList.get(i).period
                                && c % flowList.get(i).period < (j + link.hops * (propagation + flowList.get(i).duration)) % flowList.get(i).period + flowList.get(i).duration)
                            iloIntExpr = cplex.diff(iloIntExpr, H[i][link.srcNode][link.dstNode][c]);
                    }
                }
                cplex.addLe(iloIntExpr, B);
            }
        }

        //Frames Isolation Constraints
        for (int m = 0; m < graph.point.length; m++) {
            for (int n = 0; n < graph.point.length; n++) {
                for (int j = 0; j < hyperPeriod; j++) {
                    IloIntExpr iloIntExpr = cplex.intExpr();
//                    iloIntExpr = cplex.sum(iloIntExpr, O[j][m][n]);
                    for (int i = 0; i < flowList.size(); i++) {
                        iloIntExpr = cplex.sum(iloIntExpr, H[i][m][n][j]);
                    }
                    cplex.addLe(iloIntExpr, 1);
                }
            }
        }
        Map<String, Object> res = null;
        if (cplex.solve()) {
            res = new HashMap<>();
            int[][] XsRes = new int[flowList.size()][hyperPeriod];
            int[][][][] HRes = new int[flowList.size()][graph.point.length][graph.point.length][hyperPeriod];
            for (int i = 0; i < flowList.size(); i++) {
                for (int j = 0; j < hyperPeriod; j++) {
                    XsRes[i][j] += cplex.getValue(Xs[i][j]);
                    for (int m = 0; m < graph.point.length; m++) {
                        for (int n = 0; n < graph.point.length; n++) {
                            HRes[i][m][n][j] += cplex.getValue(H[i][m][n][j]);
                        }
                    }
                }
            }
            res.put("X", XsRes);
            res.put("H", HRes);
        }

        cplex.clearModel();
        cplex.endModel();
        cplex.end();
        cplex = null;
        System.gc();

        return res;
    }

    private int calculateLCM(int i, int j) {
        return i * j / calculateGCD(i, j);
    }

    private int calculateGCD(int i, int j) {
        int x = i % j;
        while(x != 0){
            i = j;
            j = x;
            x = i % j;
        }
        return j;
    }
}

